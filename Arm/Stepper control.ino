class MotorPin
{
private:
    int m_PosNegPin;
    int m_ZPin;
    
public:
    MotorPin(int PosNegPin, int ZPin)
    {
        this->m_PosNegPin = PosNegPin;
        this->m_ZPin = ZPin;
        
        pinMode(this->m_PosNegPin, OUTPUT);
        pinMode(this->m_ZPin, OUTPUT);
    }
    
    void Pos()
    {
        digitalWrite(this->m_PosNegPin, LOW);
        digitalWrite(this->m_ZPin, LOW);
    }
    
    void Neg()
    {
        digitalWrite(this->m_PosNegPin, HIGH);
        digitalWrite(this->m_ZPin, LOW);
    }
    
    void Z()
    {
        digitalWrite(this->m_ZPin, HIGH);
    }
};

MotorPin A = MotorPin(2, 3);
MotorPin B = MotorPin(4,5);
MotorPin C = MotorPin(6,7);

void setup()
{
}

void loop(){
  int delaytime = 5 ;
  /******
  for (int i = 0; i<3; i++)
  {
  C.Pos();
  B.Neg();
  A.Z();

  delay(delaytime);

  A.Pos();
  B.Neg();
  C.Z();
  
    delay(delaytime);

  A.Pos();
  B.Z();
  C.Neg();
  
    delay(delaytime);

  A.Z();
  B.Pos();
  C.Neg();
  
    delay(delaytime);

  A.Neg();
  B.Pos();
  C.Z();
  
    delay(delaytime);

  A.Neg();
  B.Z();
  C.Pos();
  }
  
  delay (1000);
  /////////////////////////////////////////////
  
  
 for (int i=0; i<3; i++)
 {
  A.Z();
  B.Pos();
  C.Neg();
  
    delay(delaytime);

  A.Neg();
  B.Pos();
  C.Z();
  
    delay(delaytime);

  A.Neg();
  B.Z();
  C.Pos();
  
  delay(delaytime);
  
  C.Pos();
  B.Neg();
  A.Z();

  delay(delaytime);

  A.Pos();
  B.Neg();
  C.Z();
  
    delay(delaytime);

  A.Pos();
  B.Z();
  C.Neg();
 }
  
    delay(1000);
    
    ***********/
    
    for (int i=0; i<4; i++)
    {
  B.Pos();
    C.Pos();
    A.Neg();
  
  delay(delaytime);
  
    B.Z();

  delay(delaytime);
  
  B.Neg();
  
  delay(delaytime);
  
  A.Z();
  
  delay(delaytime);
  
  A.Pos();
  
  delay(delaytime);
  
  C.Neg();
  
  delay(delaytime);
  
  B.Z();
  
  delay(delaytime);
  
  B.Pos();
  
  delay(delaytime);
  
  A.Z();
  
  delay(delaytime);
  
  A.Neg();
  
  delay(delaytime);
  
  C.Z();
  
  delay (10);
    }
    
  delay(10);
    
}
