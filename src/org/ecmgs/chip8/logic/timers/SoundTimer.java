package org.ecmgs.chip8.logic.timers;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.ecmgs.chip8.Config;
import org.ecmgs.chip8.debugger.Logger;

public class SoundTimer extends ChipTimer implements Runnable{

	private Thread thread;
	
	public SoundTimer() {
		super();
		thread = new Thread(this);
	}
	
	@Override
	public void updateCallback() {
		int timerRegister = getTimer();
		
		if (timerRegister > 0) {
			try {
				if (!thread.isAlive()) {
					thread = new Thread(this);
					thread.start();
				}
			} catch (Exception e) {
				Logger.err("error starting thread");
			}
		}
	}

	/**
	 * Plays a sound (Configuration in the config interface)
	 */
	@Override
	public void run(){
		double fCyclePosition = 0;
		
		SourceDataLine line;
		AudioFormat aFormat = new AudioFormat(Config.SAMPLING_RATE, 16, 1, true, true);
		
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, aFormat);
		
		if (!AudioSystem.isLineSupported(info)) {
			System.err.println("Sound isn't supported in this computer");
		}
		
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(aFormat);
			line.start();
			
			ByteBuffer cBuf = ByteBuffer.allocate(line.getBufferSize());
			
			int ctSamplesTotal = (int) (Config.SAMPLING_RATE * 0.15);
			
			while (ctSamplesTotal > 0) {
				double fCycleInc = Config.S_FREQ/Config.SAMPLING_RATE;

		         cBuf.clear();

		      	 int ctSamplesThisPass = line.available()/Config.SAMPLE_SIZE;   
		         for (int i=0; i < ctSamplesThisPass; i++) {
		            cBuf.putShort((short)(Short.MAX_VALUE * Math.sin(2*Math.PI * fCyclePosition)));

		            fCyclePosition += fCycleInc;
		            if (fCyclePosition > 1)
		               fCyclePosition -= 1;
		         }
		         
		         line.write(cBuf.array(), 0, cBuf.position());            
		         ctSamplesTotal -= ctSamplesThisPass; 

		         while (line.getBufferSize()/2 < line.available())   
		            Thread.sleep(1);   
			}
			
			line.drain();
			line.close();
			
		} catch (LineUnavailableException | InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

}
