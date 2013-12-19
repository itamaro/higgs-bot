#define DUMP_SIZE 250
#define ANALOG_TRIGGER 160

int samples[DUMP_SIZE];

void setup() {
  Serial.begin(9600);
}

void loop() {
  int i;
  // Choose your trigger
  //if (Serial.available() > 0)
  if (analogRead(A0) > ANALOG_TRIGGER)
  {
    int inbyte = Serial.read();
    for (i=0; i < DUMP_SIZE; ++i)
    {
      samples[i] = analogRead(A0);
    }
    for (i=0; i < DUMP_SIZE; ++i) {
      Serial.println(samples[i], DEC);
      delay(1);
    }
  }
}
