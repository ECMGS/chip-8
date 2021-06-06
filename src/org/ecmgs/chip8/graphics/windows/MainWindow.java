package org.ecmgs.chip8.graphics.windows;

import org.ecmgs.chip8.Config;
import org.ecmgs.chip8.graphics.Screen;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {
	
	private final String WINDOW_TITLE = "chip-8 emulator";
	
	public Screen screen;
	
	public MainWindow(Screen screen) {
		// Set basic propierties
		this.setTitle(WINDOW_TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setResizable(true);
		
		// Adds a JPanel to set the size of the frame
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension((Config.WIDTH*Config.SCALE), (Config.HEIGHT*Config.SCALE)));
		panel.setLayout(new BorderLayout());
		
		panel.add(screen);
		
		this.getContentPane().add(panel);
		this.pack();
		
		// Inicializes the screen
		screen = new Screen();
	}
	
	public void setVisible() {
		this.setVisible(true);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
}
