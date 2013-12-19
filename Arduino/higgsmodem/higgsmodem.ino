#define MAX_ANDROID_MESSAGE 20
#define MESSAGE_TIMEOUT 25
// *** THESE VALUES WILL REQUIRE CALIBRATION ***
#define PEAK_DETECT_THRESH 175
#define FALLING_EDGE_TRIG 5
#define STATE_RESET_THRESH 160
#define SYNC_PEAKS 16

int _syncPeaksCyclic[SYNC_PEAKS];
int _syncHead;

char AndroidMessage[MAX_ANDROID_MESSAGE+1];
int _byte_pos;

int _getNextAudioPeak(int timeout) {
  // look for peaks in audio signal once sample exceeds PEAK_DETECT_THRESH (and saved as peak candidate)
  // if current sample is greater than peak candidate - promote sample
  // if peak candidate was set, and is greater than following FALLING_EDGE_TRIG samples, then we have detected a peak!
  // reset state for next peak only after falling under STATE_RESET_THRESH
  int sample, peak, falling_edge_length, no_sig_length;
  enum PeakStates {
    WaitingForPeak,
    InPeak,
    WaitingForReset,
  } peak_detect_state=WaitingForPeak;
  no_sig_length = 0;
  while (1) {
    sample = analogRead(A0);
    switch (peak_detect_state) {
      case WaitingForPeak:
      if (sample > PEAK_DETECT_THRESH) {
        peak = sample;
        falling_edge_length = 0;
        no_sig_length = 0;
        peak_detect_state = InPeak;
      }
      if (sample < STATE_RESET_THRESH) {
        no_sig_length++;
        if ((timeout > 0) && (no_sig_length > timeout)) {
          return 0;
        }
      }
      break;
      
      case InPeak:
      if (sample < peak) {
        falling_edge_length++;
        if (falling_edge_length > FALLING_EDGE_TRIG) {
          peak_detect_state = WaitingForReset;
        }
      } else {
        peak = sample;
        falling_edge_length = 0;
      }
      break;
      
      case WaitingForReset:
      if (sample < STATE_RESET_THRESH) {
        return peak;
      }
    }
  }
}

int _isInSync() {
  // look for a 0xA5 pattern in incoming audio bits
  static int diffsign[SYNC_PEAKS-1] = {-1, +1, +1, -1, -1, +1, +1, -1, +1, -1, -1, +1, +1, -1, -1};
  for (int i=0; i < sizeof(_syncPeaksCyclic) / sizeof(int); ++i) {
    int 
    diff = _syncPeaksCyclic[(_syncHead + i + 1) % SYNC_PEAKS] - _syncPeaksCyclic[(_syncHead + i) % SYNC_PEAKS];
    if (diff * diffsign[i] < 0) {
      return 0;
    }
  }
  return 1;
}

char _getNextByte() {
  // decode audio-encoded message from Android, 1 bit at a time
  // wait for first Ref peak, then decode data from following peaks
  // until there's no next peak for MESSAGE_TIMEOUT samples after last data peak
  int ref_sig_val, value_sig_val;
  char b=0;
  
  // bit-decode loop
  for (int bitpos=0; bitpos < 8; ++bitpos) {
    ref_sig_val = _getNextAudioPeak(MESSAGE_TIMEOUT);
    value_sig_val = _getNextAudioPeak(MESSAGE_TIMEOUT);
    if (0 == ref_sig_val * value_sig_val) {
      // timeout...
      return 0;
    }
    if (value_sig_val > ref_sig_val) {
      b |= 1 << bitpos;
    }
  }
  // return byte with swapped nibbles
  return ((b & 0x0F) << 4) | ((b & 0xF0) >> 4);
}

int getAndroidMessage() {
  // will remove 0xA5 and 0x5A bytes, and stop on 0x00,
  // so don't use those in your messages!
  _byte_pos = 0;
  memset(AndroidMessage, 0, sizeof(AndroidMessage));
  
  // lock on sync (0xa5)
  for (int i=0; i < sizeof(_syncPeaksCyclic) / sizeof(int); ++i) {
    _syncPeaksCyclic[i] = _getNextAudioPeak(-1);
  }
  _syncHead=0;
  while (0 == _isInSync()) {
    _syncPeaksCyclic[_syncHead] = _getNextAudioPeak(-1);
    _syncHead = (_syncHead + 1) % SYNC_PEAKS;
  }
  
  // byte-decode loop
  unsigned char b;
  while ((b = _getNextByte()) != 0) {
    // remove redundant sync bytes
    if ((0xA5 == b) || (0x5A == b)) {
      continue;
    }
    AndroidMessage[_byte_pos++] = (char)b;
    if (MAX_ANDROID_MESSAGE == _byte_pos) {
      Serial.println("Android message reached maximal length!");
      Serial.println(AndroidMessage);
      memset(AndroidMessage, 0, sizeof(AndroidMessage));
      _byte_pos = 0;
    }
  }
  return _byte_pos;
}

void setup() {
  Serial.begin(9600);
  Serial.println("I'm listening");
}

void loop() {
  int msg_len = getAndroidMessage();
  Serial.print("Got message of length ");
  Serial.println(msg_len);
  for (int i=0; i < msg_len; ++i) {
    Serial.println((unsigned char)AndroidMessage[i], HEX);
    delay(1);
  }
  Serial.println(AndroidMessage);
}
