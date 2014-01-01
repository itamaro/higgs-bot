package com.higgsbot.wifidirect;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioModem {

    private int buffSize = 8400 / 2;	// in shorts!
    private double toneFreq = 2*441.;
    private int sampleRate = 44100;
    private short audioRef[];
    private short audioHigh[];
    private short audioLow[];
    private AudioTrack audioTrack;
	
	public AudioModem() {
		initAudioData();
		
		// configure audio channel
		audioTrack = new AudioTrack(
    			AudioManager.STREAM_MUSIC,
    			sampleRate,
    			AudioFormat.CHANNEL_OUT_MONO,
    			AudioFormat.ENCODING_PCM_16BIT,
    			buffSize * 2,	// doubled to convert from size in shorts to size in bytes
    			AudioTrack.MODE_STREAM);
    	
        // start audio
        audioTrack.play();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
            	
            }
        }).start();
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
    	audioRef = generatePaddedSineCycle(9000, toneFreq, sampleRate, 0, 0);
    	audioHigh = generatePaddedSineCycle(12000, toneFreq, sampleRate, 0, 0);
    	audioLow = generatePaddedSineCycle(6000, toneFreq, sampleRate, 0, 0);
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
    
	// ATTENTION:
	// The Arduino decoding logic is very basic and polling-based!
	// So you must pay attention to the following considerations when sending messages:
	// - Wait a little between messages
	//   (if you don't, the Arduino might still be handling the previous message, and might miss the next one)
	// - Don't use the following values in the data: 0, 0xA5
	//   (0 will end the message, 0xA5 will be stripped from the message)
    public void sendData(final char data[]) {
    	assert audioRef.length == audioHigh.length;
    	assert audioRef.length == audioLow.length;
    	
        // sync
        for (int i=0; i < 3; ++i) {
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
        for (int i=0; i < 6; ++i) {
            sendByteToArduino((byte) 0xa5, audioTrack);
        	//audioTrack.write(audioRef, 0, audioRef.length);
        }

    	// stop audio
        //audioTrack.stop();
        //audioTrack.release();
    }
}
