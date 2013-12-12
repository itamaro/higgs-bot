void setup()
{
    SetupPins();
}

void loop()
{
    MoveForward(255);
    delay(1000);
    MoveBackward(255);
    delay(1000);
    SpinLeft(255);
    delay(1000);
    SpinRight(255);
    delay(1000);
}
