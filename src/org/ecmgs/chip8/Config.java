package org.ecmgs.chip8;

// NOT FINAL, CHANGE ON PRODUCTION

public interface Config {
	// WINDOW DIMENSIONS
	final int WIDTH = 64;
	final int HEIGHT = 32;
	
	// WINDOW SCALE
	final int SCALE = 10;
	
	// AUDIO CONFIGURATION
	final int SAMPLING_RATE = 44100;
	final int SAMPLE_SIZE = 2;
	
	final double S_FREQ = 440;
	
}