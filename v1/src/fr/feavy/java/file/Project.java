package fr.feavy.java.file;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.zip.InflaterInputStream;

import fr.feavy.java.constants.Direction;
import fr.feavy.java.map.Map;
import fr.feavy.java.map.events.Warp;

public class Project {

	private HashMap<Integer, Map> savedMaps = new HashMap<Integer, Map>();
	private FileGameIO fgIO;

	private boolean loaded = false;

	private File projectFile;

	public Project() {

		

	}

	/**
	 * Load a project file
	 * @param f Project file itself
	 * @throws Exception
	 */
	public void load(File f) throws Exception {

		this.projectFile = f;

		fgIO = getFileGameIO(f);
		
		loaded = true;

	}

	/**
	 * Return an instance of the GameFileIO which correspond to the project
	 * @param f Project File
	 * @return The FileGameIO itself
	 * @throws Exception
	 */
	private FileGameIO getFileGameIO(File f) throws Exception{
		
		File tempFile = File.createTempFile("pmg1", "decompiledGame");
		tempFile.deleteOnExit();

		FileOutputStream fos = new FileOutputStream(tempFile);

		FileInputStream fis = new FileInputStream(f);
		InflaterInputStream iis = new InflaterInputStream(fis);

		byte[] b = new byte[2048];
		int length = 0;

		while ((length = iis.read(b)) >= 0)
			fos.write(b, 0, length);

		iis.close();
		fos.flush();
		fos.close();

		return new FileGameIO(tempFile);
		
	}
	
	/**
	 * @return Amount of map (1 if nothing project is loaded)
	 */
	public int getMapCount() {
		return (loaded) ? fgIO.getMapCount() : 1;
	}

	/**
	 * @return String array which contains the name of all the maps
	 */
	protected String[] getMapsName(){
		String[] response = new String[fgIO.getMapCount()];
		
		for(int i = 0; i < fgIO.getMapCount(); i++){
			try {
				response[i] = fgIO.readMapLine(i).split(";")[0];
			} catch (Exception e) {
				response[i] = "";
			}
		}
		return response;
		
	}
	
	/**
	 * Verify if a map exist
	 * @param id Id of the map to search for
	 * @return true if exists, false if not
	 * @throws Exception
	 */
	public boolean isMapSet(int id) throws Exception{
		if(id < fgIO.getMapCount()){
			return (getMap(id) == null);
		}else{
			return false;
		}
	}
	
	/**
	 * Get instance of a map by its id
	 * @param id Id of the map
	 * @return
	 * @throws Exception 
	 */
	public Map getMap(int id) throws Exception {

		if(id >= fgIO.getMapCount())
			return null;
		
		String stringMap = fgIO.readMapLine(id);
		
		if(stringMap.length() < 5)
			return null;
		String[] mapData = stringMap.split(";");
		int width = Integer.parseInt(mapData[1]);
		int height = Integer.parseInt(mapData[2]);
		
		String cases = mapData[8];
		
		// Get map tiles and movements
		
		int[][][] tiles = new int[width][height][2];

		int k = 0;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				tiles[i][j][0] = Integer.parseInt(cases.substring(4 * k, 4 * k + 3), 16);
				tiles[i][j][1] = Integer.parseInt(cases.substring(4 * k + 3, 4 * k + 4));
				k++;
			}
		}
		
		// Get map warps
		
		HashMap<Point, Warp> warps = new HashMap<Point, Warp>();
		String warpsStr = mapData[9];
		
		if(warpsStr.length() >= 5){
			
			String[] warp = warpsStr.split(":");
			
			for(String w : warp){
				String[] d = w.split(",");
				warps.put(new Point(Integer.parseInt(d[0]), Integer.parseInt(d[1])), new Warp(Integer.parseInt(d[2]), Integer.parseInt(d[3]), Integer.parseInt(d[4])));
			}
			
		}
		
		// Create the map
		
		Map m = new Map(
				id,
				mapData[0],
				width,
				height,
				new int[] {Integer.parseInt(mapData[4]), Integer.parseInt(mapData[5]), Integer.parseInt(mapData[6]), Integer.parseInt(mapData[7])},
				Integer.parseInt(mapData[3], 16),
				tiles,
				warps
				);
		
		/*try{
			m.addNPC(10, 9, new NPC(m, new int[]{10, 9}, "prof_oak", Direction.LEFT, MovementType.MOVE_RANDOMLY, false, 0));
			m.addNPC(11, 10, new NPC(m, new int[]{11, 10}, "player", Direction.DOWN, MovementType.MOVE_UP_DOWN, false, 0));
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		return m;

	}
	
	/**
	 * @return true if a project is loaded, false if not
	 */
	public boolean isLoaded() {
		return loaded;
	}

}
