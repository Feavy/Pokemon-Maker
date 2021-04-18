package fr.feavy.java;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.feavy.java.data.Direction;
import fr.feavy.java.data.KeyCode;
import fr.feavy.java.debug.CommandListener;
import fr.feavy.java.drawing.Tiles;
import fr.feavy.java.events.entity.NPC;
import fr.feavy.java.events.entity.Player;
import fr.feavy.java.file.Project;
import fr.feavy.java.map.Map;
import fr.feavy.java.rendering.EntityRendering;
import fr.feavy.java.rendering.GUIRendering;
import fr.feavy.java.rendering.GameScreen;
import fr.feavy.java.rendering.MapRendering;

public class Main extends JFrame {

	private static GameScreen gameDisplay;
	private static Player player;

	private static Project project;
	
	private static int keyPressed = -1;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		Tiles.initialize(this);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Map m = null;
		
		File f = showFileBrower("load");
		this.project = new Project();
		try {
			project.load(f);
			m = project.getMap(0);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		setResizable(true);
		setTitle("Pokemon Maker - Geneneration I");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 645, 604);
		setLocationRelativeTo(null);
		
		gameDisplay = new GameScreen(m, 0, 0);
		gameDisplay.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(gameDisplay);
		gameDisplay.setLayout(null);
		
		addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {}
			
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == keyPressed)
					onKeyRelease(e);
			}
			
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() != keyPressed)
					onKeyPress(e);
			}
			
		});
		
		try {
			player = new Player();
		} catch (IOException e1) {
			this.dispose();
		}
		
		new Thread(new CommandListener(gameDisplay)).start();
		
	}
	
	/**
	 * @return instance of the game screen
	 */
	public static GameScreen getGameDisplay(){
		return gameDisplay;
	}
	
	/**
	 * @return instance of the player
	 */
	public static Player getPlayer(){
		return player;
	}
	
	/**
	 * @return instance of the project
	 */
	public static Project getProject(){
		return project;
	}
	
	/**
	 * @return keycode of the key wich is actually pressed (or -1)
	 */
	public static int getKeyPressed(){
		return keyPressed;
	}
	
	/**
	 * KeyPressedEvent
	 * @param e instance of the event
	 */
	public void onKeyPress(KeyEvent e){	
		int keyCode = e.getKeyCode();
		
		if(player.isMovementAnimationRunning())
			player.stopMovingAnimation();
		
		if(KeyCode.isArrow(keyCode))
			player.startMoving(keyCode-37);
		
		keyPressed = keyCode;
	}
	
	/**
	 * KeyReleasedEvent
	 * @param e instance of the event
	 */
	public void onKeyRelease(KeyEvent e){
		player.stopMoving();
		keyPressed = -1;
	}

	/**
	 * Refresh game screen
	 */
	public static void refreshScreen(){
		gameDisplay.repaint();
	}
	
	/**
	 * Open a JfileChooser in order to choose the game file
	 * @param Message box type ("save" or "load")
	 * @return File which has been chosen (or null)
	 */
	public File showFileBrower(String type){
		
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Game (.pkrg)", new String[]{"pkrg"});
		chooser.setFileFilter(filter);
		int response;
		File f;
		if(type.equalsIgnoreCase("save")){
			response = chooser.showSaveDialog(null);
			f = chooser.getSelectedFile().getAbsolutePath().contains(".pkrg") ? chooser.getSelectedFile() : new File(chooser.getSelectedFile()+".pkrg");
		}else{
			response = chooser.showOpenDialog(null);
			f = chooser.getSelectedFile();
		}
		if(response == JFileChooser.APPROVE_OPTION)
			return f;
		return null;
		
	}
	
}
