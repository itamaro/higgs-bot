#include <Servo.h>
#include "ArmLogic.h"
#include "MovementLogic.h"
#include "SimpleControl.h"
#include "higgsmodem.h"

RobotArm arm(6);
MotorController MtrCtrl(10, 3, 9, 8, 5, 11, 12);
RobotMovement Mvmnt(&MtrCtrl);

SimpleControl Knife(7);
SimpleControl Nitro(4);

void setup()
{
    Mvmnt.Setup();
    arm.Setup();
    Knife.Setup();
    Nitro.Setup();
    
    Knife.Off();
    Nitro.Off();
    
    Serial.begin(9600);
    Serial.println("I'm listening");
}

void loop()
{
      int msg_len = getAndroidMessage();
      Serial.print("Got message of length ");
      Serial.println(msg_len);
      for (int i=0; i < msg_len; ++i) 
      {
    Serial.println((unsigned char)AndroidMessage[i], HEX);
    delay(1);
      }
      Serial.println(AndroidMessage);
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
