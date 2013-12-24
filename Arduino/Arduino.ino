#include <Servo.h>
#include "ArmLogic.h"
#include "MovementLogic.h"
#include "SimpleControl.h"

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
}

void loop()
{
 /*   
    Mvmnt.MoveForward(255);
    delay(1000);
    Mvmnt.MoveBackward(255);
    delay(1000);
    Mvmnt.SpinLeft(255);
    delay(1000);
    Mvmnt.SpinRight(255);
    delay(1000);
    */
 
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
    /*
    arm.RawWrite(800);
    delay(1000);
    arm.RawWrite(1500);
    delay(1000);
    arm.RawWrite(2200);
    delay(1000);
    */
}
