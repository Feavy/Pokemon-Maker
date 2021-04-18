package fr.feavy.java.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.zip.InflaterInputStream;

import fr.feavy.java.map.Map;

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
	 * Create a FileGameIO whith default data
	 * @return The FileGameIO just created
	 * @throws Exception
	 */
	private FileGameIO createFileGameIO() throws Exception{
		
		File tempFile = File.createTempFile("pmg1", "decompiledGame");
		tempFile.deleteOnExit();

		PrintWriter writer = new PrintWriter(tempFile);
		writer.println("Maps:");
		writer.println("Scripts:");
		writer.println("Trainers:");
		writer.flush();
		writer.close();

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
	public String[] getMapsName(){
		return fgIO.getMapsName();
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
	 */
	public Map getMap(int id) {

		try {
			return fgIO.getMap(id);
		} catch (Exception e) {
			return null;
		}

	}
	
	/**
	 * @return true if a project is loaded, false if not
	 */
	public boolean isLoaded() {
		return loaded;
	}

}
