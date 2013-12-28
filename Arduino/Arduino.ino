#include <Servo.h>
#include "ArmLogic.h"
#include "MovementLogic.h"
#include "SimpleControl.h"
#include "higgsmodem.h"

RobotArm Arm(6);
MotorController MtrCtrl(10, 3, 9, 8, 5, 11, 12);
RobotMovement Mvmnt(&MtrCtrl);

SimpleControl Knife(7);
SimpleControl Nitro(4);

// Speed levels
int WheelSpeeds[] = {0, 36, 72, 108, 144, 180, 216, 255};
int ArmSpeeds[] = {0, 12, 24, 36, 48, 60, 72, 90};

void setup()
{
    Mvmnt.Setup();
    Arm.Setup();
    Knife.Setup();
    Nitro.Setup();
    
    Knife.Off();
    Nitro.Off();
}

void loop()
{
    int msg_len = getAndroidMessage();
    
    if (msg_len != 2 && msg_len != 1)
    {
        // TODO: ERROR? HOW?
    }
    else
    {
        int index = 0;
        
        int LDir, LSpeed, RDir, RSpeed;
        
        // Special case of A5 in first byte
        if (msg_len == 1)
        {
            LDir = 1;
            LSpeed = 2;
            RDir = 0;
            RSpeed = 5;
        }
        else
        {
            /****************** Parse ******************/
            // LeftDir(1) + LeftSpeed(3) + RightDir(1) + RightSpeed(3) + ArmDir(1) + ArmSpeed(3) + Knife(1) + Nitro(1) + Padding(2)
            LDir = (AndroidMessage[0] & 0x80) >> 7;
            LSpeed = (AndroidMessage[0] & 0x70) >> 4;
            RDir = (AndroidMessage[0] & 0x08) >> 3;
            RSpeed = (AndroidMessage[0] & 0x07) >> 0;
            
            index += 1;
        }
        
        int ADir = (AndroidMessage[index] & 0x80) >> 7;
        int ASpeed = (AndroidMessage[index] & 0x70) >> 4;
        int K = (AndroidMessage[index] & 0x08) >> 3;
        int N = (AndroidMessage[index] & 0x04) >> 2;
        int Padding = (AndroidMessage[index] & 0x03) >> 0;
        
        /****************** Handle ******************/
        Mvmnt.MoveWheel(LeftWheel, WheelSpeeds[LSpeed], (LDir*2)-1);
        Mvmnt.MoveWheel(RightWheel, WheelSpeeds[RSpeed], (RDir*2)-1);
        
        Arm.SetSpeed(ArmSpeeds[ASpeed], ADir);
        
        if (K == 0)
        {
            Knife.Off();
        }
        else
        {
            Knife.On();
        }
        
        if (N == 0)
        {
            Nitro.Off();
        }
        else
        {
            Nitro.On();
        }
    }
}



/****************** SOME USAGE EXAMPLES ****************************
    Mvmnt.MoveForward(255);
    delay(1000);
    Mvmnt.MoveBackward(255);
    delay(1000);
    Mvmnt.SpinLeft(255);
    delay(1000);
    Mvmnt.SpinRight(255);
    delay(1000);

 #if 0
 arm.RawWrite(90);
 #else
 
    //arm.SetSpeed(10, 1);
    arm.RawWrite(80);
    delay(1000);
    //arm.SetSpeed(0, 0);
    arm.RawWrite(90);
    delay(1000);
    //arm.SetSpeed(10, -1);
    arm.RawWrite(100);
    delay(1000);
    //arm.SetSpeed(0, 0);
    arm.RawWrite(90);
    delay(1000);
 #endif
    arm.RawWrite(800);
    delay(1000);
    arm.RawWrite(1500);
    delay(1000);
    arm.RawWrite(2200);
    delay(1000);
*******************************************************************/
