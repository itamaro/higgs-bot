#include <Servo.h>
#include "ArmLogic.h"
#include "MovementLogic.h"

RobotArm arm(9);
MotorController MtrCtrl(10, 3, 9, 8, 5, 11, 12);
RobotMovement Mvmnt(&MtrCtrl);

void setup()
{
    //Mvmnt.Setup();
    arm.Attach();
    pinMode(11, OUTPUT);
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
    
    /*
    arm.RawWrite(800);
    delay(1000);
    arm.RawWrite(1500);
    delay(1000);
    arm.RawWrite(2200);
    delay(1000);
    */
}
