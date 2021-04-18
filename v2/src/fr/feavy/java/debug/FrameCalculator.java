package fr.feavy.java.debug;

/**
 * Debug tool - Frame mesurer
 * @author Feavy
 *
 */
public class FrameCalculator implements Runnable{

	private int frames = 0;
	private long time;
	
	public FrameCalculator(){
		time = System.currentTimeMillis();
	}
	
	public void addFrame(){
		frames++;
	}
	
	public void run() {
		while(true){
			if((System.currentTimeMillis() - time) >= 1000){
				System.out.println("FPS: "+frames);
				frames = 0;
				time = System.currentTimeMillis();
			}
		}
	}

}
