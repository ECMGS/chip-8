package org.ecmgs.chip8.logic;

import java.nio.file.*;

import org.ecmgs.chip8.debugger.Logger;

import java.io.*;

public class Parser {
	
	CPU cpu;
	
	byte[] program;
	byte[] rawData;
	
	int position;
	
	boolean offsetCanBeChanged;
	int offset;
	
	/**
	 * sets up the parser
	 * 
	 * readFile HAS TO BE CALLED BEFORE USING THE CLASS
	 */
	public Parser(CPU cpu) {
		this.cpu = cpu;
		
		program = new byte[1];
		
		position = cpu.getPC();
		
		offset = 0;
	}
	
	/**
	 * Gets the actual instruction
	 * 
	 * @return 4 byte instruction
	 */
	private int getInstruction() {
		position = cpu.getPC();
		return 
			((int)(program[position] & 0xff) << 8)+
			(int)(program[position+1] & 0xff);
	}
	/**
	 * 
	 * Reads a file and stores it in memory
	 * 
	 * @param URL path to the file
	 * @return true if the file is loaded suscessfuly
	 */
	public boolean readFile(String URL) {
		boolean ret = false;
		try {
			program = Files.readAllBytes(Paths.get(URL));
			Logger.log("Read file, size "+Files.size(Paths.get(URL)));
			cpu.setMem(program);
			ret = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Starts parsing
	 */
	public void start() {
		while(true) {
			step();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Executes a step of the program
	 */
	public void step() {
		position = cpu.getPC();
		
		// gets the instruction
		int instruction = getInstruction();
		
		Logger.log("Reading Instruction @ 0x"+Integer.toHexString(position)+" instruction 0x"+Integer.toHexString(instruction));
		
		int Vx = (instruction & 0x0F00) >> 8;
		int Vy = (instruction & 0x00F0) >> 4;
		int nnn = (instruction & 0x0FFF);
		int nn = (instruction & 0x00FF);
		int n = (instruction & 0x000F);
		
		switch (instruction & 0xF000) {
			case 0x0000:
				switch (instruction) {
				case 0x0000:
					Logger.log("Skipping instruction");
					cpu._skip();
					break;
				case 0x00E0:
					Logger.log("Executing clear instruction");
					cpu._clearScreen();
					break ;
				case 0x00EE:
					Logger.log("Executing return instruction");
					cpu._ret();
					break ;
				default:
					Logger.err("Instruction not recognized: "+Integer.toHexString(instruction));
					break ;
				}
				break ;
			case 0x1000:
				Logger.log("Executing goto instruction");
				cpu._goto(nnn);
				break ;
			case 0x2000:
				Logger.log("Calling subrutine");
				cpu._call(nnn);
				break ;
			case 0x3000:
				Logger.log("Skip equal instruction (Vx == n)");
				cpu._skipEqual(Vx, nn);
				break ;
			case 0x4000:
				Logger.log("Skip nequal instruction (Vx != n)");
				cpu._skipNEqual(Vx, nn);
				break ;
			case 0x5000:
				Logger.log("Skip equal instruction (Vx == Vy)");
				cpu._skipRegEqual(Vx, Vy);
				break ;
			case 0x6000:
				Logger.log("Set instruction V"+Vx);
				cpu._setReg(Vx, nn);
				break ;
			case 0x7000:
				Logger.log("Increment instruction");
				cpu._incReg(Vx, nn);
				break;
			case 0x8000:
				switch (instruction & 0x000F) {
					case 0x0000:
						Logger.log("Set vx to vy");
						cpu._setRegToReg(Vx, Vy);
						break ;
					case 0x0001:
						Logger.log("Bitwise OR");
						cpu._or(Vx, Vy);
						break ;
					case 0x0002:
						Logger.log("Bitwise AND");
						cpu._and(Vx, Vy);
						break ;
					case 0x0003:
						Logger.log("Bitwise XOR");
						cpu._xor(Vx, Vy);
						break ;
					case 0x0004:
						Logger.log("Inc reg to reg");
						cpu._incRegToReg(Vx, Vy);
						break ;
					case 0x0005:
						Logger.log("Dec reg to reg");
						cpu._subRegToReg(Vx, Vy);
						break ;
					case 0x0006:
						 Logger.log("Right shift");
						 cpu._rightShift(Vx);
						 break ;
					case 0x0007:
						Logger.log("Dec Vy by Vx");
						cpu._subYReg(Vx, Vy);
						break ;
					case 0x000E:
						Logger.log("Left sift");
						cpu._leftShift(Vx);
						break ;
					default:
						Logger.err("Instruction not recognized: "+Integer.toHexString(instruction));
						break;
				}
				break;
			case 0x9000:
				Logger.log("Skip if reg are equals");
				cpu._skipRegNEqual(Vx, Vy);
				break ;
			case 0xA000:
				Logger.log("Set i to "+nnn);
				cpu._setMemTo(nnn);
				break ;
			case 0xB000:
				Logger.log("nnn + V0");
				cpu._gotoOfsset(nnn);
				break ;
			case 0xC000:
				Logger.log("random number");
				cpu._rand(Vx, nn);
				break ;
			case 0xD000:
				Logger.log("Drawing...");
				cpu._draw(Vx, Vy, n);
				break ;
			case 0xE000:
				switch (instruction & 0x00FF) {
					case 0x009E:
						Logger.log("Skips if key = Vx");
						cpu._keyEqualVx(Vx);
						break ;
					case 0x00A1:
						Logger.log("Skips if key != Vx");
						cpu._keyNEqualVx(Vx);
						break ;
					default:
						Logger.err("Instruction not recognized: "+Integer.toHexString(instruction));
						System.exit(0);
						
				}
				break ;
			case 0xF000:
				switch (instruction & 0x00FF) {
					case 0x0007:
						Logger.log("Getting delay timer");
						cpu._getDelay(Vx);
						break ;
					case 0x000A:
						Logger.log("Getting key");
						cpu._getKey(Vx);
						break ;
					case 0x0015:
						Logger.log("setting delay timer");
						cpu._setDelayTimer(Vx);
						break ;
					case 0x0018:
						Logger.log("Playing sound");
						cpu._setSoundTimer(Vx);
						break;
					case 0x001E:
						Logger.log("Add Vx to I");
						cpu._addVxToI(Vx);
						break ;
					case 0x0029:
						Logger.log("Setting I to Vx");
						cpu._setIToSpriteAddr(Vx);
						break ;
					case 0x0033:
						Logger.log("BCD");
						cpu._BCDofVx(Vx);
						break ;
					case 0x0055:
						Logger.log("memory dump");
						cpu._reg_dump(Vx);
						break ;
					case 0x0065:
						Logger.log("memory load");
						cpu._reg_load(Vx);
						break ;
					default:
						Logger.err("Instruction not recognized: "+Integer.toHexString(instruction));
						System.exit(0);
				}
				break ;
			default:
				Logger.err("Instruction not recognized: "+Integer.toHexString(instruction));
				System.exit(0);
		}
		
		return;
	}
	
}
