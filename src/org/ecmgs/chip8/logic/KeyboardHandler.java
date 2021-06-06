package org.ecmgs.chip8.logic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.ecmgs.chip8.debugger.Logger;
import org.ecmgs.chip8.graphics.Screen;

public class KeyboardHandler implements KeyListener {

	private Screen screen;
	
	// Keyboard constants, change the second array if you want to change the config
	final private int[][] KEYBOARD = {{
		0x1, 0x2, 0x3, 0xC,
		0x4, 0x5, 0x6, 0xD,
		0x7, 0x8, 0x9, 0xE,
		0xA, 0x0, 0xB, 0xF
	}, {
		KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
		KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R,
		KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F,
		KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V
	}};
	
	private int keyPressed = -1;
	
	public KeyboardHandler(Screen screen) {
		this.screen = screen;
		this.screen.addKeyListener(this);
		System.out.println("keyboard started");
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		for (int i = 0; i < KEYBOARD[1].length; i ++) {
			if (e.getKeyCode() == KEYBOARD[1][i]) {
				keyPressed = KEYBOARD[0][i];
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyPressed = -1;
	}
	
	/**
	 * @return the code of the key press
	 */
	public int getKey() {
		return keyPressed;
	}

}
