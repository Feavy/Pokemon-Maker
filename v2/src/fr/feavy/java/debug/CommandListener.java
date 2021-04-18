package fr.feavy.java.debug;

import java.util.Scanner;
import java.util.Set;

import fr.feavy.java.Main;
import fr.feavy.java.map.Map;
import fr.feavy.java.rendering.GameScreen;

/**
 * Debug tool - commands
 * @author Feavy
 *
 */
public class CommandListener implements Runnable{

	private GameScreen display;
	
	public CommandListener(GameScreen display){
		this.display = display;
	}
	
	public void run() {
		Scanner sc = new Scanner(System.in);
		String command;
		String[] args;
		Map m;
		while(true){
			command = sc.nextLine();
			if(command.startsWith("setcamera")){
				args = command.split(" ");
				m = Main.getGameDisplay().getCurrentMap();
				display.warpCameraTo(m, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				System.out.println("Camera changed.");
			}else if(command.startsWith("speed_fast")){
				Main.getGameDisplay().SCROLLING_SPEED = 1;
			}else if(command.startsWith("speed_normal")){
				Main.getGameDisplay().SCROLLING_SPEED = 5;
			}else if(command.startsWith("showthreads")){
				System.out.println("Running thread amount: "+Thread.activeCount());
			}
		}
	}
	
}
