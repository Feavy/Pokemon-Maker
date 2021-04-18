package fr.feavy.java.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.DeflaterOutputStream;

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
	
	protected String readMapLine(int id) throws Exception{
		return readLine(mapsIndex+id);
	}
	
	protected String readTrainerLine(int id) throws Exception{
		return readLine(trainersIndex+id);
	}
	
	protected String readScriptLine(int id) throws Exception{
		return readLine(scriptsIndex+id);
	}
	
}
