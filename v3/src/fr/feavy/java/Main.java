package fr.feavy.java;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.feavy.java.constants.Direction;
import fr.feavy.java.constants.Tiles;
import fr.feavy.java.drawing.GameScreen;
import fr.feavy.java.entity.Player;
import fr.feavy.java.file.Project;
import fr.feavy.java.map.Map;
import fr.feavy.java.map.events.Events;
import fr.feavy.java.systemEvent.events.Event;
import fr.feavy.java.systemEvent.listeners.EventListener;
import fr.feavy.java.systemEvent.listeners.KeyListener;

public class Main extends JFrame {

	private JPanel contentPane;
	private GameScreen screen;
	private static Project project;
	
	private static List<EventListener> eventListeners = new ArrayList<>();
	
	public final static int SCREEN_WIDTH = 645;
	public final static int SCREEN_HEIGHT = 604;
	
	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the frame.
	 */
	public Main() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		setResizable(false);
		setTitle("Pokemon 1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 645, 604);
		setLocationRelativeTo(null);
		
		Map map = null;
		Player p = null;
		
		File f = showFileChooser();
		this.project = new Project();
		try {
			project.load(f);
			map = project.getMap(0);
			p = new Player();
		} catch (Exception e2) {
			System.err.println("System exited (Wrong file chosen)");
			System.exit(ERROR);
		}
		
		p.setFacingDirection(Direction.DOWN);
		
		screen = new GameScreen(map, p);
		screen.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(screen);
		screen.setLayout(null);
		
		Events.initialize();
		new Tiles().initialize();
		
		screen.initialized();
		screen.repaint();
		
		addKeyListener(new KeyListener());
		
	}

	public static void addEventListener(EventListener listener){
		eventListeners.add(listener);
	}
	
	public static void callEvent(Event e){
		for(EventListener listener : eventListeners)
			listener.onEvent(e);
	}

	public static Project getProject() {
		return project;
	}
	
	/**
	 * Open a JfileChooser in order to choose the game file
	 * @param Message box type ("save" or "load")
	 * @return File which has been chosen (or null)
	 */
	public File showFileChooser(){
		
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Game (.pkrg)", new String[]{"pkrg"});
		chooser.setFileFilter(filter);
		int response;
		response = chooser.showOpenDialog(null);
		File f = chooser.getSelectedFile();
		if(response == JFileChooser.APPROVE_OPTION)
			return f;
		return null;
		
	}
	
}
