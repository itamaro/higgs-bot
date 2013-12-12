/**
This file implements the logical movements like 
"forward", "backward", "spin" etc...
*/

/** DirectionFlipper - This is the constant that indicates where is the "front"
of the robot.
0 - Flat side is front
1 - Round side is front
*/
const int DF = 1;

const int Forward = DF;
const int Backward = DF^1;

const int LeftWheel = DF^1;
const int RightWheel = DF;

void MoveForward(int speed)
{
    Move(LeftWheel, speed, Forward);
    Move(RightWheel, speed, Forward);
}

void MoveBackward(int speed)
{
    Move(LeftWheel, speed, Backward);
    Move(RightWheel, speed, Backward);
}

void SpinLeft(int speed)
{
    Move(LeftWheel, speed, Backward);
    Move(RightWheel, speed, Forward);
}

void SpinRight(int speed)
{
    Move(LeftWheel, speed, Forward);
    Move(RightWheel, speed, Backward);
}
