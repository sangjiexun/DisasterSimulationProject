package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import jp.ac.ut.csis.pflow.geom.Mesh;
import KsymSimulation.Simulation_ver2;

public class StationChooser {

	public static void main(String args[]){
		File input1 = new File ("c:/Users/yabec_000/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_5_14.csv"); //mesh file 1
		File input2 = new File ("c:/Users/yabec_000/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_5_22.csv"); //mesh file 1
		File meshes = new File ("c:/Users/yabec_000/Desktop/Tokyo3Wards_meshcodes_5.csv"); //file of meshcodes

		File alleki = new File ("c:/Users/yabec_000/Desktop/AllStations2003.csv");
		File output = new File ("c:/Users/yabec_000/Desktop/StationsforSimulation.csv");

		ArrayList<String> meshlist = getMeshcodes(input1,input2,meshes);
		chooseStations(alleki, output, meshlist);

	}

	public static ArrayList<String> getMeshcodes(File in1, File in2, File meshes){
		Map<String, Double> ptmap = Simulation_ver2.intomap(in1);
		Map<String, Double> zdcmap = Simulation_ver2.intomap2(in2);
		ArrayList<String> allmesh = Simulation_ver2.getMeshlist(meshes);
		//		System.out.println("meshes: " + meshes);
		ArrayList<String> meshlist = new ArrayList<String>();
		for(String mc:allmesh){
			double countpt = 0d;
			double countds = 0d;
			if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
			if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}
			double diff = (countpt - countds);
			if(diff < -500){
				meshlist.add(mc);
			}
		}
		System.out.println("yay");
		return meshlist;
	}

	public static File chooseStations(File eki, File out ,ArrayList<String> meshlist){
		try{
			BufferedReader br = new BufferedReader(new FileReader(eki));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Double lon = Double.parseDouble(tokens[2]);
				Double lat = Double.parseDouble(tokens[3]);
				Mesh mesh = new Mesh(5, lon, lat);
				if(meshlist.contains(mesh.getCode())){
					bw.write(line);
					bw.newLine();
				}
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return out;
	}

}
