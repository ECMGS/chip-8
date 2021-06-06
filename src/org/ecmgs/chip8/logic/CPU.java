package org.ecmgs.chip8.logic;

import org.ecmgs.chip8.debugger.Logger;
import org.ecmgs.chip8.graphics.Screen;
import org.ecmgs.chip8.logic.timers.DelayTimer;
import org.ecmgs.chip8.logic.timers.SoundTimer;

public class CPU {
	
	private int PC;
	private int[] V;
	private int I;
	private int retAddr[];
	private int retAddrIndex;
	
	private byte mem[];
	
	Screen screen;
	KeyboardHandler kHandler;
	
	// Timers
	SoundTimer sTimer;
	DelayTimer dTimer;
	
	public CPU(Screen screen) {
		this.screen = screen;
		
		sTimer = new SoundTimer();
		dTimer = new DelayTimer();
		
		kHandler = new KeyboardHandler(screen);
		
		// Program counter to 0x0
		PC = 0x0;
		
		// Sets up registers
		V = new int[0x10];
		for (int i : V) {
			i = 0;
		}
		
		// Sets up the retun addres register
		retAddr = new int[0xff];
		for (int i : retAddr) {
			i = 0;
		}
		
		retAddrIndex = 0;
		
		// Sets up the adress register
		I = 0;
		
		Logger.log("Started");
	}
	
	/**
	 * 	gets the PC
	 * 
	 * @return (int) PC
	 */
	public int getPC() {
		return PC;
	}
	
	/**
	 *	Sets the memory (for reading sprites)
	 *
	 *	@param mem memory
	 */
	public void setMem(byte mem[]) {
		this.mem = mem;
	}
	
	/**
	 * Prints the first 4 registers
	 */
	public void printVReg() {
		System.out.println("Printing 1st 4 regs");
		for (int i = 0; i < 4; i++) {
			System.out.print("V"+i+": "+V[i]);
		}
		System.out.println();
	}
	
	/**
	 * increments PC by one instruction
	 */
	private void incPC() {
		PC+=2;
	}
	
	/**
	 * sets PC to the desired instruction
	 */
	private void setPC(int PC) {
		this.PC = PC;
	}
	
	// INSTRUCTIONS (they start with a _)
	public void _clearScreen() {
		screen.clearScreen();
		incPC();
	}
	
	public void _ret() {
		if (retAddrIndex > 0) {
			PC = retAddr[--retAddrIndex];
		} else {
			Logger.log("Can't return from main");
		}
		incPC();
	}
	
	public void _goto(int nnn) {
		setPC(nnn - 0x200);
	}
	
	public void _call(int nnn) {
		retAddr[retAddrIndex++] = getPC();
		_goto(nnn);
	}
	
	public void _skipEqual(int Vx, int n) {
		if (V[Vx] == n) {
			_skip();
		}
		incPC();
	}
	
	public void _skipNEqual(int Vx, int n) {
		if (V[Vx] != n) {
			_skip();
		}
		incPC();
	}
	
	public void _skipRegEqual(int Vx, int Vy) {
		if (V[Vx] == V[Vy]) {
			_skip();
		}
		incPC();
	}
	
	public void _setReg(int Vx, int nn) {
		V[Vx] = nn;
		incPC();
	}
	
	public void _incReg(int Vx, int nn) {
		V[Vx] = (V[Vx] + nn) & 0xFF;
		incPC();
	}
	
	public void _setRegToReg(int Vx, int Vy) {
		V[Vx] = V[Vy];
		incPC();
	}
	
	public void _or(int Vx, int Vy) {
		V[Vx] = V[Vx] | V[Vy];
		incPC();
	}
	
	public void _and(int Vx, int Vy) {
		V[Vx] = V[Vx] & V[Vy];
		incPC();
	}
	
	public void _xor(int Vx, int Vy) {
		V[Vx] = V[Vx] ^ V[Vy];
		incPC();
	}
	
	public void _incRegToReg(int Vx, int Vy) {
		V[Vx] += V[Vy];
		if (V[Vx] > 0xFF) {
			V[Vx] &= 0xFF;
			V[0xF] = 1;
		} else {
			V[0xF] = 0;
		}
		incPC();
	}
	
	public void _subRegToReg(int Vx, int Vy) {
		if (V[Vx] < V[Vy]) {
			V[Vx] = Math.abs(V[Vx]-V[Vy]);
			V[0xF] = 1;
		} else {
			V[Vx] -= V[Vy];
			V[0xF] = 0;
		}
		incPC();
	}
	
	public void _rightShift(int Vx) {
		V[0xF] = V[Vx] & 0b00000001;
		V[Vx] >>= 1;
		incPC();
	}
	
	public void _subYReg(int Vx, int Vy) {
		if (V[Vy] > V[Vx]) {
			V[Vx] = Math.abs(V[Vy] - V[Vx]);
			V[0xF] = 1;
		} else {
			V[Vx] = V[Vy] - V[Vx];
			V[0xF] = 0;
		}
		incPC();
	}
	
	public void _leftShift(int Vx) {
		V[0xF] = (V[Vx] & 0b10000000) >> 7;
		V[Vx] = (V[Vx] << 1) & 0xFF;
		incPC();
	}
	
	public void _skipRegNEqual(int Vx, int Vy) {
		if (V[Vx] != V[Vy]) {
			incPC();
		}
		incPC();
	}
	
	public void _setMemTo(int nnn) {
		I = nnn - 0x200;
		incPC();
	}
	
	public void _gotoOfsset(int nnn) {
		setPC(nnn+V[0x0]-0x400);
	}
	
	public void _rand(int Vx, int nn) {
		V[Vx] = (int) (Math.random()*255) & nn;
		incPC();
	}
	
	public void _draw(int Vx, int Vy, int n) {
		try {
			if (mem == null) {
				throw new Exception("mem wasn't inicialized");
			}
			
			int memPointer = I;
			
			V[0xF] = 0;
			
			if (I > 0) {
				Logger.log("Started drawing");
				for (int i = 0; i < n; i++) {
					Logger.log("> " + i);
					for (int j = 7; j >= 0; j--) {
						if (((mem[memPointer] >> j) & 0x01) == 1) {
							if (!screen.invertPixel(V[Vx]+(7-j), V[Vy]+i)) {
								V[0xF] = 1;
							}
						}
					}
					memPointer++;
				}
			} else {
				int numArr[][] = {
					{0xF0, 0x90, 0x90, 0x90, 0xF0},
					{0x20, 0x60, 0x20, 0x20, 0x70},
					{0xF0, 0x10, 0xF0, 0x80, 0xF0},
					{0xF0, 0x10, 0xF0, 0x10, 0xF0},
					{0x90, 0x90, 0xF0, 0x10, 0x10},
					{0xF0, 0x80, 0xF0, 0x10, 0xF0},
					{0xF0, 0x80, 0xF0, 0x90, 0xF0},
					{0xF0, 0x10, 0x20, 0x40, 0x40},
					{0xF0, 0x90, 0xF0, 0x90, 0xF0},
					{0xF0, 0x90, 0xF0, 0x10, 0xF0},
					{0xF0, 0x90, 0xF0, 0x90, 0x90},
					{0xE0, 0x90, 0xE0, 0x90, 0xE0},
					{0xF0, 0x80, 0x80, 0x80, 0xF0},
					{0xE0, 0x90, 0x90, 0x90, 0xE0},
					{0xF0, 0x80, 0xF0, 0x80, 0xF0},
					{0xF0, 0x80, 0xF0, 0x80, 0x80}
				};
				
				for (int i = 0; i < 5; i++) {
					for (int j = 7; j >= 0; j--) {
						if (((numArr[Math.abs(I)][i] >> j) & 0x01) == 1) {
							if (!screen.invertPixel(V[Vx]+(7-j), V[Vy]+i)) {
								V[0xF] = 1;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		incPC();
	}
	
	public void _keyEqualVx(int Vx) {
		if (kHandler.getKey() == V[Vx]) {
			incPC();
		}
		incPC();
	}
	
	public void _keyNEqualVx(int Vx) {
		if (kHandler.getKey() != V[Vx]) {
			incPC();
		}
		incPC();
	}
	
	public void _getDelay(int Vx) {
		V[Vx] = dTimer.getTimer();
		incPC();
	}
	
	public void _getKey(int Vx) {
		try {
			Thread.sleep(150);
		} catch (Exception e) {
			// TODO: handle exception
		}
		while ((V[Vx] = kHandler.getKey()) == -1) {
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		incPC();
	}
	
	public void _setDelayTimer(int Vx) {
		dTimer.setTimer(V[Vx]);
		incPC();
	}
	
	public void _setSoundTimer(int Vx) {
		sTimer.setTimer(V[Vx]);
		incPC();
	}
	
	public void _addVxToI(int Vx) {
		I += V[Vx];
		Logger.log("I: "+I);
		incPC();
	}
	
	public void _setIToSpriteAddr(int Vx) {
		I = 0 - V[Vx];
		incPC();
	}
	
	public void _BCDofVx(int Vx) {
		mem[I] 		= (byte) ((byte) (V[Vx] / 100) % 10);
		mem[I+1] 	= (byte) ((byte) (V[Vx] / 10) % 10);
		mem[I+2]	= (byte) ((byte) (V[Vx] / 1) % 10);
		incPC();
	}
	
	public void _reg_dump(int Vx) {
		for (int i = 0; i <= Vx; i++) {
			mem[I+i] = (byte) V[i];
		}
		incPC();
	}
	
	public void _reg_load(int Vx) {
		for (int i = 0; i <= Vx; i++) {
			V[i] = (int) mem[I+i];
		}
		incPC();
	}
	
	public void _skip() {
		incPC();
	}
}
