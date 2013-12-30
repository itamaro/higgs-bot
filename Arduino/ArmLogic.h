// Seems like you have to manually invluce Servo.h ... don't know why

#include <Arduino.h>
//#include <Servo.h> // This is not included for some reason

#define SERVO_MIN_VALUE            0
#define SERVO_MAX_VALUE            180
#define SERVO_VALUE_RANGE       (SERVO_MAX_VALUE - SERVO_MIN_VALUE)
#define SERVO_MID_VALUE            (SERVO_MIN_VALUE + (SERVO_VALUE_RANGE / 2))

#define SERVO_MAX_SPEED    (SERVO_VALUE_RANGE/2)

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
    
    void Setup()
    {
        this->m_srv.attach(this->m_ControlPin);
        this->SetSpeed(0, 1);
    }
    
    // Writes raw data to the servo
    // The servo will get values from 0-180
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
        
        this->m_srv.write(ms);
    }
    
    /**
    This method will set the speed of the servo to the speed&direction you desire.
    The servo supports 90 values of speed (0-90).
    dir is either -1 or 1
    */
    void SetSpeed(int spd, int dir)
    {
        if (spd > SERVO_MAX_SPEED)
        {
            spd = SERVO_MAX_SPEED;
        }
        
        if (spd < -SERVO_MAX_SPEED)
        {
            spd = -SERVO_MAX_SPEED;
        }
        
        if (dir>1)
        {
            dir = 1;
        }
        
        if (dir<-1)
        {
            dir = -1;
        }
        
        // ms = mid + (speed * dir)
        int ms = SERVO_MID_VALUE + (spd*dir);
        
        this->RawWrite(ms);
    }
};
