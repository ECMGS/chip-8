package org.ecmgs.chip8.logic.timers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public abstract class ChipTimer implements ActionListener{
	
	private Timer timer;
	private int timerRegister;
	
	public ChipTimer() {
		timer = new Timer(16, this);
		timerRegister = 0;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		updateCallback();
		if (timerRegister == 0) {
			timer.stop();
			return ;
		}
		
		timerRegister--;
	}
	
	/**
	 * Sets the timer countodown to the specified number
	 * 
	 * @param timerRegister the timer is set to the timer register
	 */
	public void setTimer(int timerRegister) {
		if (this.timerRegister == 0)
			timer.start();
		this.timerRegister = timerRegister;
	}
	
	/**
	 * Gets the timer's timerRegister
	 * 
	 * @return timer register
	 */
	public int getTimer() {
		return timerRegister;
	}
	
	public abstract void updateCallback();
}
