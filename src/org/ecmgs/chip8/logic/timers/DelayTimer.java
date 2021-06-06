package org.ecmgs.chip8.logic.timers;

public class DelayTimer extends ChipTimer {

	public DelayTimer() {
		super ();
	}
	
	@Override
	public void updateCallback() {
		System.out.println("timer updated "+super.getTimer());
	}

}
