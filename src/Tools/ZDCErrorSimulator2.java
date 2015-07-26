package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.obs.aggre.MeshTrafficVolume;

public class ZDCErrorSimulator2 {

	public static void main(String args[]){
		File in = new File ("C:/Users/yabetaka/Desktop/shinchi.csv"); 
		File in_mesh = new File ("C:/Users/yabetaka/Desktop/shinchiMesh_4.csv"); 

		for (int i=0; i<=10; i++){
			aggregate(getBiasedSample(in, i),i);
			System.out.println(i);
		}
		System.out.println("done making scenarios");

		File meshes = new File ("C:/Users/yabetaka/Desktop/Tokyo3Wards_meshcodes_4.csv");
		ArrayList<String> meshlist = new ArrayList<String>();

		try{
			BufferedReader brm = new BufferedReader(new FileReader(meshes));
			String linemesh = null;
			while((linemesh = brm.readLine()) != null){
				String[] tokens = linemesh.split("\t");
				meshlist.add(tokens[0]);
			}
			brm.close();
		}
		catch(FileNotFoundException z) {System.out.println("File not found 1");}
		catch(IOException e) {System.out.println(e);}

		Map<String,Double> ShinchiMap = new HashMap<String, Double>();
		try{
			BufferedReader brm = new BufferedReader(new FileReader(in_mesh));
			String line1 = null;
			while((line1 = brm.readLine()) != null){
				String[] tokens = line1.split(",");
				ShinchiMap.put(tokens[0],Double.parseDouble(tokens[1]));
			}
			brm.close();
		}
		catch(FileNotFoundException z) {System.out.println("File not found 2");}
		catch(IOException e) {System.out.println(e);}

		//		System.out.println(ShinchiMap);

		for (String mesh: meshlist){
			if(ShinchiMap.get(mesh)==null){
				getDifferencebyMesh(mesh,0);
			}
			else{
				getDifferencebyMesh(mesh, ShinchiMap.get(mesh));	
			}
		}
	}

	public static File getBiasedSample(File infile, int i){
		File dir = new File("C:/Users/yabetaka/Desktop/temp");
		dir.mkdir();

		File outfile = new File ("C:/Users/yabetaka/Desktop/temp/"+ i +".csv");
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				Random ran = new Random();
				double dig = ran.nextDouble();
				if(dig <= 0.005){

					String[] tokens = line.split(",");
					//					Double newlon = Double.parseDouble(tokens[1]);
					//					Double newlat = Double.parseDouble(tokens[2]);

					/* 
					 * if there is distance bias
					 */
					double dig1 = ran.nextDouble();
					double dig2 = ran.nextDouble();
					double errdis = (dig1*600-300)/111120; 
					double errdis2 = (dig2*600-300)/111120; 
					Double newlon = Double.parseDouble(tokens[1])+errdis;
					Double newlat = Double.parseDouble(tokens[2])+errdis2;

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
		MeshTrafficVolume volume = new MeshTrafficVolume(4);	 // mesh level=5
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

	public static void getDifferencebyMesh(String meshcode, double shinchi){
		ArrayList<Double> list = new ArrayList<Double>();
		try{
			for (int i=0; i<=10; i++){
				File in = new File ("C:/Users/yabetaka/Desktop/temp/mesh" + i + ".csv");
				BufferedReader br = new BufferedReader(new FileReader(in));
				String line = null;
				while((line = br.readLine()) != null){
					String[] tokens = line.split(",");
					String mesh = tokens[0];
					if(mesh.equals(meshcode)){
						list.add(Double.parseDouble(tokens[1])-shinchi); //list niha gosa ga haitteru
					}
				}
				br.close();
			}
			double sum = 0;
			for (double a:list){
				sum = sum + a;
			}
			double average = sum/list.size();	//gosa no heikin 
			System.out.println(meshcode + "," + shinchi + "," + getSigma(list,average)*Math.pow(shinchi, 0.5));
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found:");
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}

	public static double getSigma(ArrayList<Double> list, double avg){
		double bunsan = 0;
		double sum = 0;
		ArrayList<Double> temp = new ArrayList<Double>();
		for(double a:list){
			temp.add(Math.pow(a-avg, 2));
		}
		for(double b:temp){
			sum = sum + b;
		}
		bunsan = Math.pow(sum/list.size(),0.5);
		return bunsan;
	}


}
