// Seems like you have to manually invluce Servo.h ... don't know why

#include <Arduino.h>
//#include <Servo.h> // This is not included for some reason

#define SERVO_MIN_VALUE            800
#define SERVO_MAX_VALUE            2200
#define SERVO_VALUE_RANGE       (SERVO_MAX_VALUE - SERVO_MIN_VALUE)
#define SERVO_MID_VALUE            (SERVO_MIN_VALUE + (SERVO_VALUE_RANGE / 2))

#define MAX_ANGLE    80

class RobotArm
{
private:
    int m_ControlPin;
    Servo m_srv;
    
public:    
    RobotArm(int ControlPin)
    {
        this->m_ControlPin = ControlPin;
    }
    
    void Attach()
    {
        this->m_srv.attach(this->m_ControlPin);
    }
    
    // Writes raw data to the servo
    // The servo will get values from 800-2200
    // This method will round to these numbers if passed
    void RawWrite(int ms)
    {
        if (ms < SERVO_MIN_VALUE)
        {
            ms = SERVO_MIN_VALUE;
        }
        
        if (ms > SERVO_MAX_VALUE)
        {
            ms = SERVO_MAX_VALUE;
        }
        
        this->m_srv.writeMicroseconds(ms);
    }
    
    /**
    This method will set the angle of the servo to the angle you desire.
    The servo supports only 160 degrees.
    The method considers 0 as the middle and so it gets values ranging from -80 to 80.
    Negative numbers are on the left while positive numbers are on the right.
    */
    void SetAngle(int angle)
    {
        if (angle > MAX_ANGLE)
        {
            angle = MAX_ANGLE;
        }
        
        if (angle < -MAX_ANGLE)
        {
            angle = -MAX_ANGLE;
        }
        
        // ms = min + (angle if left was 0) * (relation between angle and ms values)
        int ms = SERVO_MIN_VALUE + ((angle + MAX_ANGLE) * (SERVO_VALUE_RANGE/(MAX_ANGLE*2)));
        
        this->RawWrite(ms);
    }
};
