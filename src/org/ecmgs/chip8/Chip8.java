package org.ecmgs.chip8;

import org.ecmgs.chip8.graphics.Screen;
import org.ecmgs.chip8.graphics.windows.MainWindow;
import org.ecmgs.chip8.logic.CPU;
import org.ecmgs.chip8.logic.Parser;

/**
 * 
 * JChip-8
 * @author ecm - 2020
 *
 */

public class Chip8 {

	public static void main(String[] args) {
		
		// temporal
		String file = "/mnt/dataDisk/Documentos/Programacion/java/chip-8/src/maze.ch8";
		
		// Inicializes the screen
		Screen screen = new Screen();
		MainWindow window = new MainWindow(screen);
		window.setVisible();
		
		// Inicialices the CPU and Parser
		CPU cpu = new CPU(screen);
		Parser parser = new Parser(cpu);
		if(parser.readFile(file)) {
			parser.start();
		} else {
			window.dispose();
		}
	}

}
