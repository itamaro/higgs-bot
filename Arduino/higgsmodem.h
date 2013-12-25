#define MAX_ANDROID_MESSAGE 40
#define SYNC_PEAKS 16
// *** THESE VALUES WILL REQUIRE CALIBRATION ***
#define PEAK_DETECT_THRESH 10
#define CYCLE_RESET_THRESH 8
#define MESSAGE_TIMEOUT 15

int _syncPeaksCyclic[SYNC_PEAKS];
int _syncHead;

char AndroidMessage[MAX_ANDROID_MESSAGE+1];
int _byte_pos;

int _getNextAudioPeak(int timeout) {
  // look for peaks in audio signal once sample exceeds PEAK_DETECT_THRESH
  // return peak after sample falls under CYCLE_RESET_THRESH
  int sample, peak, no_sig_length;

  no_sig_length = 0;
  sample = analogRead(A0);
  while(sample <= PEAK_DETECT_THRESH) {
    if ((timeout > 0) && (no_sig_length > timeout)) {
      return 0;
    }
    ++no_sig_length;
    sample = analogRead(A0);
  }
  peak = sample;

  while (sample >= CYCLE_RESET_THRESH) {
    if (sample > peak) {
      peak = sample;
    }
    sample = analogRead(A0);
  }

  return peak;
}

int _isInSync() {
  // look for a 0xA5 pattern in incoming audio peaks
  static int diffsign[SYNC_PEAKS-1] = { 
    +1, -1, -1, +1, +1, -1, -1, +1, -1, +1, +1, -1, -1, +1, +1  };
  for (int i=0; i < SYNC_PEAKS-1; ++i) {
    int diff = _syncPeaksCyclic[(_syncHead + i + 1) % SYNC_PEAKS] - _syncPeaksCyclic[(_syncHead + i) % SYNC_PEAKS];
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
  return b;
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
    if (0xA5 == b) {
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
