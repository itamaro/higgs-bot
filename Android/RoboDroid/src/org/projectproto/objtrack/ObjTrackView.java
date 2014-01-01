package org.projectproto.objtrack;

import com.higgsbot.wifidirect.Globals;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

class ObjTrackView extends SampleViewBase {

	private int mFrameSize;
	private Bitmap mBitmap;
	private int[] mRGBA;

    public ObjTrackView(Context context) {
        super(context);
    }

	@Override
	protected void onPreviewStared(int previewWidtd, int previewHeight) {
		mFrameSize = previewWidtd * previewHeight;
		mRGBA = new int[mFrameSize];
		mBitmap = Bitmap.createBitmap(previewWidtd, previewHeight, Bitmap.Config.ARGB_8888);
	}

	@Override
	protected void onPreviewStopped() {
		if(mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		mRGBA = null;
		
		
	}

    @Override
    protected Bitmap processFrame(byte[] data) {
        int[] rgba = mRGBA;
        //Log.d(VIEW_LOG_TAG, "x" + String.valueOf(getFrameWidth()));
        //Log.d(VIEW_LOG_TAG, "y" + String.valueOf(getFrameHeight()));
        CircleObjectTrack(getFrameWidth(), getFrameHeight(), data, rgba, ObjTrackActivity.bShowTresholded);
        
        Bitmap bmp = mBitmap; 
        bmp.setPixels(rgba, 0/* offset */, getFrameWidth() /* stride */, 0, 0, getFrameWidth(), getFrameHeight());
        return bmp;
    }
    
    // please, let me live even though I used this dark programming technique
    public void messageMe(int x, int y, int r) {
        //Log.d(VIEW_LOG_TAG, "!!! x:" + x + " y:" + y + " r:" + r);
    	if (Globals.isAutonomous == true)
    	{
	        int d = 200*45/r;
	        int v = (d-25)*2 /30 + 3;
	        if (x > (getFrameWidth() / 2))
	        {
	        	Log.d(VIEW_LOG_TAG, "L+4R+3");
	        } else if (x < (getFrameWidth() / 2))
	        {
	        	Log.d(VIEW_LOG_TAG, "L+3R+4");
	        }
	        else
	        {
	        	Log.d(VIEW_LOG_TAG, "L+4R+4");
	        }
    	}
    }
    
    
    public native void CircleObjectTrack(int width, int height, byte yuv[], int[] rgba, boolean debug);

    static {
    	System.loadLibrary("objtrack_opencv_jni");
    }
}
