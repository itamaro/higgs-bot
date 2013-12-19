/**
this file controls the basic motor functionallity
*/

#include <arduino.h>

//motor A connected between A01 (POS) and A02 (NEG)
//motor B connected between B01 (POS) and B02 (NEG)

class MotorController
{
private:
    int m_STBY;

    // Motor A
    int m_PWMA;
    int m_AIN1;
    int m_AIN2;
    
    // Motor B
    int m_PWMB;
    int m_BIN1;
    int m_BIN2;
    
public:
    MotorController(int STBY, int PWMA, int AIN1, int AIN2, int PWMB, int BIN1, int BIN2)
    {
        this->m_STBY = STBY;
        
        this->m_PWMA = PWMA;
        this->m_AIN1 = AIN1;
        this->m_AIN2 = AIN2;
        this->m_PWMB = PWMB;
        this->m_BIN1 = BIN1;
        this->m_BIN2 = BIN2;
    }
        
    void Setup()
    {
        pinMode(this->m_STBY, OUTPUT);
    
        pinMode(this->m_PWMA, OUTPUT);
        pinMode(this->m_AIN1, OUTPUT);
        pinMode(this->m_AIN2, OUTPUT);
    
        pinMode(this->m_PWMB, OUTPUT);
        pinMode(this->m_BIN1, OUTPUT);
        pinMode(this->m_BIN2, OUTPUT);
    }
    
    void Move(int motor, int speed, int direction)
    {
        //Move specific motor at speed and direction
        //motor: 0 for B 1 for A
        //speed: 0 is off, and 255 is full speed
        //direction: 0 clockwise, 1 counter-clockwise
        
          digitalWrite(this->m_STBY, HIGH); //disable standby
        
          boolean inPin1 = LOW;
          boolean inPin2 = HIGH;
        
          if(direction == 1)
          {
            inPin1 = HIGH;
            inPin2 = LOW;
          }
        
          if(motor == 1)
          {
            digitalWrite(this->m_AIN1, inPin1);
            digitalWrite(this->m_AIN2, inPin2);
            analogWrite(this->m_PWMA, speed);
          }
          else
          {
            digitalWrite(this->m_BIN1, inPin1);
            digitalWrite(this->m_BIN2, inPin2);
            analogWrite(this->m_PWMB, speed);
          }
    }
    
    void Stop()
    {
          //enable standby  
          digitalWrite(this->m_STBY, LOW); 
    }
};

const int STBY = 10; //standby

//Motor A
const int PWMA = 3; //Speed control 
const int AIN1 = 9; //Direction
const int AIN2 = 8; //Direction

//Motor B
const int PWMB = 5; //Speed control
const int BIN1 = 11; //Direction
const int BIN2 = 12; //Direction

// Setup the pins to control the bot


/************************************
****** BASIC CONTROL ****************
************************************/



