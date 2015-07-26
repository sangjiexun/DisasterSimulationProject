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
import java.util.Random;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.obs.aggre.MeshTrafficVolume;
import KsymSimulation.Simulation_ver2;

public class ZDCErrorSimulator {

	public static void main(String args[]){
		File in = new File ("C:/Users/yabetaka/Desktop/shinchi.csv"); 
		File in_mesh = new File ("C:/Users/yabetaka/Desktop/shinchiMesh.csv"); 
		File results = new File ("C:/Users/yabetaka/Desktop/results.csv");

		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(results));	
			for (int i=0; i<=1000; i++){
				aggregate(getBiasedSample(in, i),i);
				File temp = new File ("C:/Users/yabetaka/Desktop/temp/mesh" + i + ".csv");
				File meshes = new File ("C:/Users/yabetaka/Desktop/Tokyo3Wards_meshcodes_5.csv");
				bw.write(i + "," + getRMSE(temp,in_mesh,meshes,i));
				System.out.println(i + "," + getDifference(temp,in_mesh,meshes,i));
				bw.newLine();
			}
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
	}


	public static File getBiasedSample(File infile, int i){
		File dir = new File("C:/Users/yabetaka/Desktop/temp");
		dir.mkdir();
		
		File outfile = new File ("C:/Users/yabetaka/Desktop/temp/" + i +".csv");
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				Random ran = new Random();
				double dig = ran.nextDouble();
				if(dig <= 0.005){

					String[] tokens = line.split(",");
					Double newlon = Double.parseDouble(tokens[1]);
					Double newlat = Double.parseDouble(tokens[2]);

					/* 
					 * if there is distance bias
					 */
					//					double dig1 = ran.nextDouble();
					//					double dig2 = ran.nextDouble();
					//					double errdis = (dig1*600-300)/111120; 
					//					double errdis2 = (dig2*600-300)/111120; 
					//					Double newlon = Double.parseDouble(tokens[1])+errdis;
					//					Double newlat = Double.parseDouble(tokens[2])+errdis2;
					
					bw.write(newlon + "," + newlat);
					bw.newLine();
				}
			}
			bw.close();
			br.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return outfile;
	}

	public static void aggregate(File in, int i){
		MeshTrafficVolume volume = new MeshTrafficVolume(5);	 // mesh level=5
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			String line = null;
			int j = 1;
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split(",");
				//				String pid = tokens[0];
				double lon = Double.parseDouble(tokens[0]);
				double lat = Double.parseDouble(tokens[1]);
				LonLat pos = new LonLat(lon, lat);
				volume.aggregate(String.valueOf(j),0,pos,1,200);
				j++;
			}
			br.close();
		}

		catch(FileNotFoundException e) {
			System.out.println("File not found:");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		volume.export(new File("C:/Users/yabetaka/Desktop/temp/mesh" + i + ".csv"));	
	}

	public static Double getRMSE(File pt, File zdc, File meshlist, int i){ 
		Map<String, Double> ptmap = Simulation_ver2.intomap(pt);
		//		System.out.println("ptmap: " + ptmap);
		Map<String, Double> zdcmap = Simulation_ver2.intomap2(zdc);
		ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);
		File out = new File("C:/Users/yabetaka/Desktop/temp/out" +i+ ".csv");
		//		System.out.println("meshes: " + meshes);
		ArrayList<Double> templist = new ArrayList<Double>();

		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			for(String mc:meshes){
				double countpt = 0d;
				double countds = 0d;
				if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
				if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}
				double diff = (countpt - countds);
				double temp = Math.pow(diff,2);
				templist.add(temp);
				bw.write(mc + "," + countpt + "," + countds);
				bw.newLine();
			}
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		Double sum = 0d;
		for(Double num:templist){
			sum = sum + num;
		}
		Double RMSE = Math.pow(sum/templist.size(), 0.5);
		return RMSE;
	}

	public static Double getDifference(File pt, File zdc, File meshlist, int i){ 
		Map<String, Double> ptmap = Simulation_ver2.intomap(pt);
		//		System.out.println("ptmap: " + ptmap);
		Map<String, Double> zdcmap = Simulation_ver2.intomap2(zdc);
		ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);
		File out = new File("C:/Users/yabetaka/Desktop/temp/out" +i+ ".csv");
		//		System.out.println("meshes: " + meshes);
		ArrayList<Double> templist = new ArrayList<Double>();

		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			for(String mc:meshes){
				double countpt = 0d;
				double countds = 0d;
				if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
				if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}
				double diff = (countpt - countds);
				templist.add(diff);
				bw.write(mc + "," + countpt + "," + countds);
				bw.newLine();
			}
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		Double sum = 0d;
		for(Double num:templist){
			sum = sum + num;
		}
		Double difference = sum/templist.size();
		return difference;
	}

}
