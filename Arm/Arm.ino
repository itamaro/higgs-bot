/*
Arduino Servo Test sketch
*/
#include <Servo.h>
Servo servoMain; // Define our Servo

void setup()
{
   servoMain.attach(9); // servo on digital pin 9
}

void loop()
{
    servoMain.writeMicroseconds(800); //min (left)
    delay(1000);
    servoMain.writeMicroseconds(1500); //mid
    delay(1000);
    servoMain.writeMicroseconds(2200); // max (right)
    delay(1000);
}
