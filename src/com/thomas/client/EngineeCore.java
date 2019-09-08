package com.thomas.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import com.thomas.logic.YaShuoJni;

/**
 * 麦克风录音
 * 
 * @author 123456
 *
 */
public class EngineeCore {
	public AudioFormat audioFormat;
	public TargetDataLine targetDataLine;
	public boolean flag = true;
	public YaShuoJni parent;

	public EngineeCore(YaShuoJni yaShuoJni) {
		this.parent = yaShuoJni;
	}

	public EngineeCore() {
	}

	public static void main(String args[]) {
		EngineeCore engineeCore = new EngineeCore();

		engineeCore.startRecognize();

	}

	public void stopRecognize() {
		flag = false;
		if (targetDataLine == null) {
			return;
		}
		targetDataLine.stop();
		targetDataLine.close();
	}

	private AudioFormat getAudioFormat() {
		float sampleRate = 8000;
		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 1;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}// end getAudioFormat

	public void startRecognize() {
		try {
			// 获得指定的音频格式
			audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

			// Create a thread to capture the microphone
			// data into an audio file and start the
			// thread running. It will run until the
			// Stop button is clicked. This method
			// will return after starting the thread.
			flag = true;
			new CaptureThread().start();
		} catch (Exception e) {
			e.printStackTrace();
		} // end catch
	}// end captureAudio method

	class CaptureThread extends Thread {
		public void run() {
			// 声音录入的权值
			int weight = 2;
			// 判断是否停止的计数
			int downSum = 0;

			ByteArrayInputStream bais = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			AudioInputStream ais = null;
			try {
				targetDataLine.open(audioFormat);
				targetDataLine.start();
				byte[] fragment = new byte[48000];

				ais = new AudioInputStream(targetDataLine);
				int index = 1;
				boolean waiting = true;// 标识暂时没有声音输入暂停录音
				while (flag) {
					targetDataLine.read(fragment, 0, fragment.length);
					// 暂停录音时判断数组末位是否大于weight(有声音传入)
					if (waiting && Math.abs(fragment[fragment.length - 1]) > weight) {
						waiting = false;
					}
					if (!waiting) {
						baos.write(fragment);
						System.out.println("首位：" + fragment[0] + ",末尾：" + fragment[fragment.length - 1] + " length: " + fragment.length);
						byte[] audioData = baos.toByteArray();
						// EngineeCore.this.parent.send(audioData);
						bais = new ByteArrayInputStream(audioData);
						ais = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());
						// 定义最终保存的文件名
						System.out.println("开始生成语音文件");
						File audioFile = new File("C:\\test\\voice_cache" + (index++) + ".wav");
						AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);

						// 判断语音是否停止
						if (Math.abs(fragment[fragment.length - 1]) <= weight) {
							downSum++;
						} else {
							System.out.println("重置计数");
							downSum = 0;
						}
						// 计数超过20说明此段时间没有声音传入(值也可更改)
						if (downSum > 20) {// 暂停录入
							System.out.println("暂停录入");
							waiting = true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 关闭流
				try {
					ais.close();
					bais.close();
					baos.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}// end run
	}// end inner class CaptureThread
}