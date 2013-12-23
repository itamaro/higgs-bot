package com.higgsbot.robodroid;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioModem {

    private int buffSize = 12800 / 2;	// in shorts!
    private double toneFreq = 2*441.;
    private int sampleRate = 44100;
    private short audioRef[];
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
    	audioRef = generatePaddedSineCycle(6000, toneFreq, sampleRate, 0, 0);
    	audioHigh = generatePaddedSineCycle(8000, toneFreq, sampleRate, 0, 0);
    	audioLow = generatePaddedSineCycle(4000, toneFreq, sampleRate, 0, 0);
    }
    
    private static Boolean isBitSet(byte b, int bit) {
        return (b & (1 << bit)) != 0;
    }
    
    private void sendByteToArduino(byte byte_to_send, AudioTrack audioTrack) {
    	for (int bit=0; bit < 8; ++bit) {
    		audioTrack.write(audioRef, 0, audioRef.length);
    		if (isBitSet(byte_to_send, bit)) {
    			audioTrack.write(audioHigh, 0, audioHigh.length);
    		} else {
    			audioTrack.write(audioLow, 0, audioLow.length);
    		}
    	}
    }
    
    public void sendData(final char data[]) {
    	assert audioRef.length == audioHigh.length;
    	assert audioRef.length == audioLow.length;
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
        for (int i=0; i < 2; ++i) {
            sendByteToArduino((byte) 0xa5, audioTrack);
        	//audioTrack.write(audioRef, 0, audioRef.length);
        }
    	
        // send data
    	for (int i=0; i < data.length; ++i) {
    		sendByteToArduino((byte) data[i], audioTrack);
    	}
    	
    	// send termination
        sendByteToArduino((byte) 0x00, audioTrack);
    	
    	// post sync
        for (int i=0; i < 7; ++i) {
            sendByteToArduino((byte) 0xa5, audioTrack);
        	//audioTrack.write(audioRef, 0, audioRef.length);
        }

    	// stop audio
        audioTrack.stop();
        audioTrack.release();
    }
}
