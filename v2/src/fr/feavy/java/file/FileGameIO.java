package fr.feavy.java.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

import fr.feavy.java.data.Direction;
import fr.feavy.java.events.Warp;
import fr.feavy.java.events.entity.MovementType;
import fr.feavy.java.events.entity.NPC;
import fr.feavy.java.map.Coordonate;
import fr.feavy.java.map.Map;

public class FileGameIO {

	private File project;
	private int scriptsIndex = -1;
	private int mapsIndex = -1;
	private int trainersIndex = -1;
	
	private int mapCount = 0;
	private int scriptCount = 0;
	private int trainerCount = 0;
	
	/**
	 * Initialize the File Game Input/Output
	 * @param decompiledProject Temp file where uncompressed data is stocked
	 * @throws Exception
	 */
	protected FileGameIO(File decompiledProject) throws Exception{
		
		String currentStep = "";
		
		this.project = decompiledProject;
		String line = null;
		int lineNb = 0;
		BufferedReader reader = new BufferedReader(new FileReader(decompiledProject));
		
		while((line = reader.readLine()) != null){
			
			if(line.equals("Maps:")){
				mapsIndex = lineNb+1;
				currentStep = "maps";
			}else if(line.equals("Scripts:")){
				scriptsIndex = lineNb+1;
				currentStep = "scripts";
			}else if(line.equals("Trainers:")){
				trainersIndex = lineNb+1;
				currentStep = "trainers";
			}else{
				if(currentStep.equals("maps"))
					mapCount++;
				else if(currentStep.equals("scripts"))
					scriptCount++;
				else if(currentStep.equals("trainers"))
					trainerCount++;
			}
			lineNb++;
		}
		
		reader.close();
		
	}
	
	/**
	 * @return Amount of map available in the project
	 */
	protected int getMapCount(){
		return mapCount;
	}
	
	/**
	 * @return String array which contains the name of all the maps
	 */
	protected String[] getMapsName(){
		String[] response = new String[mapCount];
		
		for(int i = 0; i < mapCount; i++){
			
			try {
				response[i] = readLine(i+mapsIndex).split(";")[0];
			} catch (Exception e) {
				response[i] = "";
			}
		}
		return response;
		
	}
	
	/**
	 * Read a specified line of the project
	 * @param line Line position
	 * @return Line data
	 * @throws Exception
	 */
	private String readLine(int line) throws Exception{
		
		String response = "";
		int lineNb = 0;
		BufferedReader reader = new BufferedReader(new FileReader(project));
		
		while(lineNb < line){
			reader.readLine();
			lineNb++;
		}
		
		response = reader.readLine();
		reader.close();
		return response;
		
	}
	
	/**
	 * Replace a line by another
	 * @param line Line position
	 * @param data New data which will be set
	 * @param add if true: data is written before the specified line
	 * @throws Exception
	 */
	private void setLine(int line, String data, boolean add) throws Exception{
		
		int lineNb = 0;
		String str = "";
		
		BufferedReader reader = new BufferedReader(new FileReader(project));
		StringBuilder builder = new StringBuilder();
		
		while((str = reader.readLine()) != null){
			if(lineNb == line){
				builder.append(data+"\n");
				if(add)
					builder.append(str+"\n");
			}else
				builder.append(str+"\n");
			lineNb++;
		}
		
		if(lineNb < line)
			builder.append(data+"\n");
		
		reader.close();
		
		PrintWriter writer = new PrintWriter(project);
		
		writer.write(builder.toString());
		writer.flush();
		writer.close();
		
	}
	
	/**
	 * Save the project
	 * @param f File in which the data will be saved
	 * @throws IOException
	 */
	protected void save(File f) throws IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(project));
		
		StringBuilder builder = new StringBuilder();
		
		String str = "";
		
		System.out.println("Saving...");
		
		while((str = reader.readLine()) != null){
			System.out.println(str);
			builder.append(str+"\n");
		}
		
		reader.close();
		
		DeflaterOutputStream dos = new DeflaterOutputStream(new FileOutputStream(f));
		dos.write(builder.toString().getBytes());
		dos.flush();
		dos.close();
			
	}
	
	/**
	 * Get a map by id
	 * @param id Id of the map
	 * @return The map itself
	 * @throws Exception
	 */
	protected Map getMap(int id) throws Exception{
		
		if(id >= mapCount)
			return null;
		
		String stringMap = readLine(mapsIndex+id);
		
		if(stringMap.length() < 5)
			return null;
		String[] mapData = stringMap.split(";");
		int width = Integer.parseInt(mapData[1]);
		int height = Integer.parseInt(mapData[2]);
		
		String cases = mapData[8];
		
		int[][] tiles = new int[width][height];
		int[][] movements = new int[width][height];

		int k = 0;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				tiles[i][j] = Integer.parseInt(cases.substring(4 * k, 4 * k + 3), 16);
				movements[i][j] = Integer.parseInt(cases.substring(4 * k + 3, 4 * k + 4));
				k++;
			}
		}
		
		//GET MAP WARPS
		
		Map m = new Map(id, mapData[0], width, height, Integer.parseInt(mapData[4]), Integer.parseInt(mapData[5]), Integer.parseInt(mapData[6]), Integer.parseInt(mapData[7]), tiles, movements, Integer.parseInt(mapData[3], 16));

		HashMap<Coordonate, List<Warp>> warps = new HashMap<Coordonate, List<Warp>>();
		String warpsStr = mapData[9];
		
		if(warpsStr.length() >= 5){
			
			String[] warp = warpsStr.split(":");
			
			for(String w : warp){
				String[] d = w.split(",");
				m.addWarp(Integer.parseInt(d[0]), Integer.parseInt(d[1]), new Warp(Integer.parseInt(d[2]), Integer.parseInt(d[3]), Integer.parseInt(d[4])));
			}
			
		}
		
		try{
			m.addNPC(10, 9, new NPC(m, new int[]{10, 9}, "prof_oak", Direction.LEFT, MovementType.MOVE_RANDOMLY, false, 0));
			m.addNPC(11, 10, new NPC(m, new int[]{11, 10}, "player", Direction.DOWN, MovementType.MOVE_UP_DOWN, false, 0));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return m;
		
	}
	
}
