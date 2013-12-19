/**
This file implements the logical movements like 
"forward", "backward", "spin" etc...
*/

#include "MotorControl.h"

/** DF = DirectionFlipper - This is the constant that indicates where is the "front"
                                                            of the robot.
                                                            0 - Flat side is front
                                                            1 - Round side is front
*/
const int DF = 1;

const int Forward = DF;
const int Backward = DF^1;

const int LeftWheel = DF^1;
const int RightWheel = DF;

class RobotMovement
{
private:
    MotorController* m_pMtrCtrl;
    
public:
    RobotMovement(MotorController* pMtrCtrl)
    {
        this->m_pMtrCtrl = pMtrCtrl;
    }
    
    void Setup()
    {
        this->m_pMtrCtrl->Setup();
    }
    
    void MoveForward(int speed)
    {
        this->m_pMtrCtrl->Move(LeftWheel, speed, Forward);
        this->m_pMtrCtrl->Move(RightWheel, speed, Forward);
    }
    
    void MoveBackward(int speed)
    {
        this->m_pMtrCtrl->Move(LeftWheel, speed, Backward);
        this->m_pMtrCtrl->Move(RightWheel, speed, Backward);
    }
    
    void SpinLeft(int speed)
    {
        this->m_pMtrCtrl->Move(LeftWheel, speed, Backward);
        this->m_pMtrCtrl->Move(RightWheel, speed, Forward);
    }
    
    void SpinRight(int speed)
    {
        this->m_pMtrCtrl->Move(LeftWheel, speed, Forward);
        this->m_pMtrCtrl->Move(RightWheel, speed, Backward);
    }
};
