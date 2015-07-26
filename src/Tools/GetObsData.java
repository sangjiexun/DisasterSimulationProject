package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import KsymSimulation.Simulation_ver2;

public class GetObsData {

	public static void main(String args[]){

		for (int day = 10; day<=12; day++){
			for (int i = 0; i<=23; i++){
				File out = new File ("C:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/Fujisawa/Fujisawa_5_"+day+"_"+i+".csv");
				File obs = new File ("C:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_201103"+day+"_5_"+i+".csv");
				File meshcode = new File ("C:/Users/yabetaka/Desktop/Fujisawa_meshes_5.csv");

				ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshcode);
				System.out.println(meshes.size());
				getObsFile(obs, out, meshes);
				//				System.out.println(day + "," + i + "," + getTotalPopofZone(obs,meshcode));
			}
		}
	}

	//	static File shapedir = new File("C:/Users/yabetaka/Desktop/Tokyo3WardZone");
	//static File shapedir = new File("C:/Users/yabetaka/Desktop/zone_bounds_fujisawa_shape");

	//	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	//	public static File getObsData(File infile, File outfile, int i, int day){
	//
	//		double totalpop = 0d;
	//
	//		try{
	//			BufferedReader br = new BufferedReader(new FileReader(infile));
	//			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
	//			String line = null;
	//			while((line = br.readLine()) != null){
	//				String tokens[] = line.split("\t");
	//				String meshcode = tokens[0];
	//				Mesh mesh = new Mesh(meshcode);
	//				LonLat point = mesh.getCenter();
	//				Double pop = Double.parseDouble(tokens[1]);
	//				//				System.out.println(pop);
	//
	//				List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
	//				String zonecode;
	//				if( zonecodeList == null || zonecodeList.isEmpty() ){
	//					zonecode = "0";
	//				}
	//				else{
	//					zonecode = zonecodeList.get(0);
	//					System.out.println(zonecode);
	//					System.out.println(Integer.valueOf(zonecode));
	//					LonLat center = PTZoneUtils.getZoneRepresentative(Integer.valueOf(zonecode));
	//					bw.write(zonecode + "," + meshcode + "," + pop + "," + center.getLon() + "," + center.getLat());
	//					bw.newLine();
	//					totalpop = totalpop + pop;
	//				}
	//			}
	//			br.close();
	//			bw.close();
	//		}
	//		catch(FileNotFoundException xx) {
	//			System.out.println("File not found 1");
	//		}
	//		catch(IOException xxx) {
	//			System.out.println(xxx);
	//		}
	//		System.out.println(day + "," + i + "," + totalpop);
	//		return outfile;
	//	}

	public static Double getTotalPopofZone(File infile, File meshfile){
		HashSet<String> meshes = new HashSet<String>();
		ArrayList<Double> pops = new ArrayList<Double>();
		//		HashMap<Integer, Double> obs = new HashMap<Integer, Double>();
		Double sum = 0d;
		try{
			BufferedReader br = new BufferedReader(new FileReader(meshfile));
			String line = null;
			while((line = br.readLine()) != null){
				String tokens[] = line.split(",");
				String meshcode = tokens[0];
				meshes.add(meshcode);
				//				System.out.println(meshes);
			}
			br.close();

			for(String m:meshes){
				BufferedReader br1 = new BufferedReader(new FileReader(infile));
				String line1 = null;
				while((line1 = br1.readLine()) != null){
					String tokens[] = line1.split("\t");
					String meshcode = tokens[0];
					if(m.equals(meshcode)){
						pops.add(Double.parseDouble(tokens[1]));
					}
					else{continue;}
				}
				for(Double p : pops){
					sum = sum + p;
				}
				br1.close();
				//				obs.put(z, sum);
				pops.clear();
			}
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		//		System.out.println(sum);
		return sum;
	}

	public static File getObsFile(File infile, File outfile,ArrayList<String> meshes){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			int count = 0;
			while((line = br.readLine()) != null){
				String tokens[] = line.split("\t");
				String meshcode = tokens[0];
//				System.out.println(meshcode);
				if(meshes.contains(meshcode)){
					Double pop = Double.valueOf(tokens[1]);
					bw.write(meshcode + "," + pop);
					bw.newLine();
					count++;
				}
			}
			bw.close();
			br.close();
			System.out.println(count);
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return outfile;
	}

}
