#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/imgproc/imgproc_c.h>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_org_projectproto_objtrack_ObjTrackView_CircleObjectTrack(JNIEnv* env, jobject obj,
    jint width, jint height, jbyteArray yuv, jintArray bgra, jboolean debug)
{
    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);

	jclass cls = env->GetObjectClass(obj);  // instead of FindClass
	jmethodID mid = env->GetMethodID(cls, "messageMe", "(IIII)V");
 		
    Mat mYuv(height + height/2, width, CV_8UC1, (unsigned char *)_yuv);
    Mat mBgra(height, width, CV_8UC4, (unsigned char *)_bgra);
    Mat mGray(height, width, CV_8UC1, (unsigned char *)_yuv);

    CvSize size = cvSize(width, height);
    IplImage *hsv_frame    = cvCreateImage(size, IPL_DEPTH_8U, 3);
    IplImage *thresholded  = cvCreateImage(size, IPL_DEPTH_8U, 1);
    
    IplImage img_color = mBgra;
    IplImage img_gray = mGray;

    //Please make attention about BGRA byte order
    //ARGB stored in java as int array becomes BGRA at native level
    cvtColor(mYuv, mBgra, CV_YUV420sp2BGR, 4);

    // convert to HSV color-space
    cvCvtColor(&img_color, hsv_frame, CV_BGR2HSV);

    // Filter out colors which are out of range. (ping-pong ball hue ~ 14)
    cvInRangeS(hsv_frame, cvScalar(22, 110, 110, 0), cvScalar(30, 255, 255, 0), thresholded);

    // Memory for hough circles
    CvMemStorage* storage = cvCreateMemStorage(0);

    // some smoothing of the image
    cvSmooth( thresholded, thresholded, CV_GAUSSIAN, 9, 9 );

    // show thresholded
    if(debug)    cvCvtColor(thresholded, &img_color, CV_GRAY2BGR);

    // find circle patterns
    CvSeq* circles = cvHoughCircles(thresholded, storage, CV_HOUGH_GRADIENT, 1.5,
                                        thresholded->height/4, 100, 40, 30, 300);
    // draw found circles
    //for (int i = 0; i<circles->total; i++)
    char buffer[20];
    
    if (circles->total == 0)
    {
    	env->CallVoidMethod(obj, mid, circles->total, 0, 0, 0);
    }
    
    for (int i = 0; i<circles->total && i<3; i++) // max 3 circles
    {
        float* p = (float*)cvGetSeqElem( circles, i );
		int x = p[0];
		int y = p[1];
		int r = p[2];		
		
 		env->CallVoidMethod(obj, mid, x, y, r, 1);
	    
        //putText(mBgra, buffer, Point(100,100), CV_FONT_HERSHEY_COMPLEX, 1, Scalar(255, 0, 0));
        circle(mBgra, Point(p[0],p[1]), 3, Scalar(0,255,0,255), 2);
        circle(mBgra, Point(p[0],p[1]), p[2], Scalar(0,0,255,255), 4);
    }

    // cleanup resources
    cvReleaseMemStorage(&storage);
    cvReleaseImage(&hsv_frame);
    cvReleaseImage(&thresholded);

    env->ReleaseIntArrayElements(bgra, _bgra, 0);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);
}

}
