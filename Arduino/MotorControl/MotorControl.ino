/**
this file controls the basic motor functionallity
*/

//motor A connected between A01 (POS) and A02 (NEG)
//motor B connected between B01 (POS) and B02 (NEG)

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
void SetupPins()
{
    pinMode(STBY, OUTPUT);

    pinMode(PWMA, OUTPUT);
    pinMode(AIN1, OUTPUT);
    pinMode(AIN2, OUTPUT);

    pinMode(PWMB, OUTPUT);
    pinMode(BIN1, OUTPUT);
    pinMode(BIN2, OUTPUT);
}

/************************************
****** BASIC CONTROL ****************
************************************/

void Move(int motor, int speed, int direction)
{
//Move specific motor at speed and direction
//motor: 0 for B 1 for A
//speed: 0 is off, and 255 is full speed
//direction: 0 clockwise, 1 counter-clockwise

  digitalWrite(STBY, HIGH); //disable standby

  boolean inPin1 = LOW;
  boolean inPin2 = HIGH;

  if(direction == 1)
  {
    inPin1 = HIGH;
    inPin2 = LOW;
  }

  if(motor == 1)
  {
    digitalWrite(AIN1, inPin1);
    digitalWrite(AIN2, inPin2);
    analogWrite(PWMA, speed);
  }
  else
  {
    digitalWrite(BIN1, inPin1);
    digitalWrite(BIN2, inPin2);
    analogWrite(PWMB, speed);
  }
}

void Stop()
{
  //enable standby  
  digitalWrite(STBY, LOW); 
}



