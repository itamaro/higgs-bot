package com.higgsbot.wifidirect;

import android.util.Log;

public class Globals {
	
	public static char left_dir='+';
	public static int left_speed=0;
	public static char right_dir='+';
	public static int right_speed=0;
	public static char arm_dir='+';
	public static int arm_speed=0;
	public static boolean nitro=false;
	public static boolean knife=false;
		
	private static void normalize() {
		if (left_speed == 0) {
			left_dir = '+';
		}
		if (right_speed == 0) {
			right_dir = '+';
		}
		if (arm_speed == 0) {
			arm_dir = '+';
		}
	}
	
	private static char toDirectionalEngineChar(char dir, int speed) {
		return (char) ((dir == '+' ? 0x8 : 0) | (speed & 0x7));
	}
	
	public static char[] getAudioCommand() {
		normalize();
		char audioCommand[] = {0x80,0x80};
		audioCommand[0] = (char) ((toDirectionalEngineChar(left_dir, left_speed) << 4) | 
				toDirectionalEngineChar(right_dir, right_speed));
		audioCommand[1] = (char) ((toDirectionalEngineChar(arm_dir, arm_speed) << 4) |
				(nitro ? 0x08 : 0) | (knife ? 0x04 : 0));
		return audioCommand;
	}
	
	public static void updateState(String message) {
		if (message == null) {
			Log.d("NetworkService", "wifiMsg: null message");
		} else {
			Log.d("NetworkService", "wifiMsg: " + message);
			// parse WiFi message
			int i=0;
			while (i < message.length()) {
				switch (message.charAt(i)) {
				case 'L':
					left_dir = message.charAt(i+1);
					left_speed = message.charAt(i+2) - '0';
					i += 3;
					break;
				case 'R':
					right_dir = message.charAt(i+1);
					right_speed = message.charAt(i+2) - '0';
					i += 3;
					break;
				case 'A':
					arm_dir = message.charAt(i+1);
					arm_speed = message.charAt(i+2) - '0';
					i += 3;
					break;
				case 'N':
					nitro = (message.charAt(i+1) == '+');
					i += 2;
					break;
				case 'K':
					knife = (message.charAt(i+1) == '+');
					i += 2;
					break;
				default:
					++i;
					break;
				}
			}
		}
	}

}
