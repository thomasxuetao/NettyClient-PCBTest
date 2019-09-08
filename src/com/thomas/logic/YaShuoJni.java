package com.thomas.logic;

import com.thomas.client.EngineeCore;

public class YaShuoJni {

	public EngineeCore engineeCore;

	public native byte[] yaShuo(byte[] audioData);

	static {
		System.loadLibrary("YaShuoJni");
	}
	
	public YaShuoJni() {
		engineeCore = new EngineeCore(this);
	}

	public void send(byte[] audioData) {
		byte[] value = this.yaShuo(audioData);
	}

	public void start() {
		engineeCore.startRecognize();
	}

	public void stop() {
		if (engineeCore.flag) {
			engineeCore.stopRecognize();
		}
	}

}
