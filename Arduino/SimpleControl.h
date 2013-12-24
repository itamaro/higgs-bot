class SimpleControl
{
private:
    int m_pin;
    
public:
    SimpleControl(int pin)
    {
        this->m_pin = pin;
    }
    
    void Setup()
    {
        pinMode(this->m_pin, OUTPUT);
    }
    
    void On()
    {
        digitalWrite(this->m_pin, HIGH);
    }
    
    void Off()
    {
        digitalWrite(this->m_pin, LOW);
    }
};
