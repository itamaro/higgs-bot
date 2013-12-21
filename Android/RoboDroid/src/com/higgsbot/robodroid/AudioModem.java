package com.higgsbot.robodroid;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioModem {

    private int buffSize = 12800 / 2;	// in shorts!
    private double toneFreq = 441.;
    private int sampleRate = 44100;
    private short audioBase[];
    private short audioHigh[];
    private short audioLow[];
	
	public AudioModem() {
		initAudioData();
	}
    
    private short[] generatePaddedSineCycle(int amp, double freq, int sr, int prepad, int postpad) {
    	int cycle_len = (int) (sr / freq);
    	short waveform[] = new short[cycle_len + prepad + postpad];
    	// no need to explicitly zero pre/post-pad, as it's the default in Java
    	for (int t=0; t < cycle_len; ++t) {
    		waveform[prepad+t] = (short) (amp * Math.sin(2 * Math.PI * freq * t / sr));
    	}
    	return waveform;
    }
    
    private void initAudioData() {
    	audioBase = generatePaddedSineCycle(3000, toneFreq, sampleRate, 0, 0);
    	audioHigh = generatePaddedSineCycle(6000, toneFreq, sampleRate, 0, 0);
    	audioLow = generatePaddedSineCycle(2000, toneFreq, sampleRate, 0, 0);
    }
    
    private static Boolean isBitSet(byte b, int bit) {
        return (b & (1 << bit)) != 0;
    }
    
    private void sendByteToArduino(byte byte_to_send, AudioTrack audioTrack) {
    	byte_to_send = (byte) (((byte_to_send & 0x0F) << 4) | ((byte_to_send & 0xF0) >> 4));
    	for (int bit=0; bit < 8; ++bit) {
    		audioTrack.write(audioBase, 0, audioBase.length);
    		if (isBitSet(byte_to_send, bit)) {
    			audioTrack.write(audioHigh, 0, audioHigh.length);
    		} else {
    			audioTrack.write(audioLow, 0, audioLow.length);
    		}
    	}
    }
    
    public void sendData(final char data[]) {
    	assert audioBase.length == audioHigh.length;
    	assert audioBase.length == audioLow.length;
    	AudioTrack audioTrack = new AudioTrack(
    			AudioManager.STREAM_MUSIC,
    			sampleRate,
    			AudioFormat.CHANNEL_OUT_MONO,
    			AudioFormat.ENCODING_PCM_16BIT,
    			buffSize * 2,	// doubled to convert from size in shorts to size in bytes
    			AudioTrack.MODE_STREAM);
    	
        // start audio
        audioTrack.play();
        
        // sync
        for (int i=0; i < 4; ++i) {
            sendByteToArduino((byte) 0xa5, audioTrack);
        }
    	
    	for (int i=0; i < data.length; ++i) {
    		sendByteToArduino((byte) data[i], audioTrack);
    	}
    	
    	// post sync
        for (int i=0; i < 4; ++i) {
            sendByteToArduino((byte) 0xa5, audioTrack);
        }

    	// stop audio
        audioTrack.stop();
        audioTrack.release();
    }
}
