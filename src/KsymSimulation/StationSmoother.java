package KsymSimulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class StationSmoother {

	public static void main(String args[]){

		File def = new File ("");
		File[] scenarioresults = new File[144];
		for(int i= 0; i<=143 ; i++){
			scenarioresults[i] = new File ("C:/Users/yabec_000/Desktop/output1/mesh_" + i + ".csv");
			modifyFile(scenarioresults[i], i, def);
		}
	}

	public static File modifyFile(File infile, int i, File def){
		File out = new File("C:/Users/yabec_000/Desktop/output1/mesh_" + i + "smth.csv");
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(out,true));
			
			Double shinjuku = (intoMap(infile).get("mesh")-intoMap2(infile).get("mesh2"))/5; //5 is number of meshes to give away to
			Double tokyo = (intoMap(infile).get("mesh1")-intoMap2(infile).get("mesh2"))/5; // same as above
			Double akiba = (intoMap(infile).get("mesh1")-intoMap2(infile).get("mesh2"))/2;

			Double pop = 0d;
			
			int shin = 0;
			int tok  = 0;
			int akib = 0;
			int count = 0;
			
			for(String mesh : intoMap(infile).keySet()){
				//if shinjuku ... dont forget to put own mesh into if()
				if(mesh.equals("mesh1")||mesh.equals("mesh1")||mesh.equals("mesh1")||mesh.equals("mesh1")||mesh.equals("mesh1")){
					pop = intoMap(infile).get(mesh) + shinjuku;
					bw.write(mesh + "," + pop);
					bw.newLine();
					shin++;
				}
				//if tokyo
				else if(mesh.equals("mesh1")||mesh.equals("mesh1")||mesh.equals("mesh1")||mesh.equals("mesh1")||mesh.equals("mesh1")){
					pop = intoMap(infile).get(mesh) + tokyo;
					bw.write(mesh + "," + pop);
					bw.newLine();
					tok++;
					System.out.println(tok);
				}
				//if akiba
				else if(mesh.equals("mesh1")||mesh.equals("mesh1")){
					pop = intoMap(infile).get(mesh) + akiba;
					bw.write(mesh + "," + pop);
					bw.newLine();
					akib++;
					System.out.println(akib);
				}
				else {
					pop = intoMap(infile).get(mesh);
					bw.write(mesh + "," + pop);
					bw.newLine();
				}
				count++;
			}
			bw.close();
			System.out.println("file num: " + i +  "all : " + count + "shinjuku: "+ shin +", tokyo: " + tok + ", akiba " + akib);
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return out;
	}

	//simulated pop data
	public static HashMap<String,Double> intoMap(File in){
		HashMap<String,Double> popmap = new HashMap<String, Double>();
		try{
			BufferedReader br3 = new BufferedReader(new FileReader(in));
			String line = null;
			while( (line = br3.readLine()) != null ) {
				String[] tokens = line.split(",");
				String meshcode = tokens[0];
				Double count   = (Double.valueOf(tokens[1]));
				popmap.put(meshcode, count);
			}
			br3.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return popmap;
	}

	//default pop data (混雑統計データ14時version)
	public static HashMap<String,Double> intoMap2(File in){
		HashMap<String,Double> popmap = new HashMap<String, Double>();
		try{
			BufferedReader br3 = new BufferedReader(new FileReader(in));
			String line = null;
			while( (line = br3.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				Double count   = (Double.valueOf(tokens[1]));
				popmap.put(meshcode, count);
			}
			br3.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return popmap;
	}

}
