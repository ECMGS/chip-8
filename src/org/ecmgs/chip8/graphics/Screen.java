package org.ecmgs.chip8.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.ecmgs.chip8.Config;

public class Screen extends JPanel {
	
	private boolean[][] screenPixels;
	
	public Screen() {
		// inicializes the jpanel
		setBackground(Color.RED);
		setFocusable(true);
		
		// Sets up the screen Pixels array and inicialices them
		screenPixels = new boolean[Config.WIDTH][Config.HEIGHT];
		for (boolean[] sPx : screenPixels) {
			for (boolean sPy : sPx) {
				sPy = false;
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D G2D = (Graphics2D) g;
		
		// Creates a rectangle2D for every pixel
		Rectangle2D[][] PixelRects = new Rectangle2D[Config.WIDTH][Config.HEIGHT];
		
		// x & y positions in the screen
		int x = 0, y = 0;
		
		// Color is set to black for default
		G2D.setColor(Color.black);
		
		// Writes every pixel in the screen
		for (int i = 0; i < PixelRects.length; i++) {
			for (int j = 0; j < PixelRects[0].length; j++) {
				PixelRects[i][j] = new Rectangle2D.Double(x, y, (double) calculatePixelSize().width, (double) calculatePixelSize().height);
				if (screenPixels[i][j]) G2D.setColor(Color.white);
				else G2D.setColor(Color.black);
				G2D.fill(PixelRects[i][j]);
				y += calculatePixelSize().height;
			}
			y = 0;
			x += calculatePixelSize().width;
		}
	}
	
	/**
	 * calculatPixelSize()
	 * 
	 * Calculates and returns the size of a pixel
	 * 
	 * @return (Dimension) The size in pixels of a chip-8 pixel
	 */
	private Dimension calculatePixelSize() {
		Dimension ret = new Dimension();
		
		ret.width = Config.SCALE;
		ret.height = Config.SCALE;
		
		return ret;
	}
	
	/**
	 * Inverts the pixel (if it was off it's turned on and the other way around)
	 * 
	 * @param x (int) position of the column in the screen
	 * @param y (int) position of the row in the screen
	 * 
	 * @return (boolean) if the pixel is turned on it returns true, else it returns false
	 */
	public boolean invertPixel(int x, int y) {
		boolean ret = (screenPixels[x][y] = !screenPixels[x][y]);
		repaint();
		return ret;
	}
	
	/**
	 * Clears the entire screen
	 */
	public void clearScreen() {
		for (boolean[] pixerRow : screenPixels) {
			for (boolean sPixel : pixerRow) {
				sPixel = false;
			}
		}
		repaint();
	}
	
}
