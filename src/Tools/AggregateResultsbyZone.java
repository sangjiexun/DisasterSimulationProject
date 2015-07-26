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
import java.util.HashSet;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;

public class AggregateResultsbyZone {

	public static void main(String args[]){
		File meshcodes = new File ("C:/Users/yabec_000/Desktop/Tokyo3wards_meshcodes_5.csv");
		File outfile = new File ("C:/Users/yabec_000/Desktop/meshcode-zonecode_in3wards.csv");

//		aggregatebyZone(rewriteMeshbyZone(infile, temp, meshcodes),outfile);

//		aggregatebyZone(infile,outfile);
		
		getZonefromMesh(meshcodes, outfile);
		
	}

	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static File getZonefromMesh(File infile, File outfile){
//		HashMap<String, String> MeshZone = new HashMap<String,String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				String meshcode = tokens[0];
				Mesh mesh = new Mesh(meshcode);
				System.out.println(meshcode);
				LonLat point = mesh.getCenter();
				List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());

				if( zonecodeList == null || zonecodeList.isEmpty() )
				{continue;}
				else{
					String zonecode     = zonecodeList.get(0);			
//					MeshZone.put(meshcode, zonecode);
					bw.write(line + "," + zonecode);
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
		return outfile;
	}

//	public static File rewriteMeshbyZone(File infile, File outfile, File meshfile){
//		try{
//			BufferedReader br = new BufferedReader(new FileReader(infile));
//			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
//			String line = null;
//			while((line = br.readLine()) != null){
//				String[] tokens = line.split(",");
//				String zone = getZonefromMesh(meshfile).get(tokens[0]);
//				String pt = tokens[1];
//				String zdc = tokens[2];
//				bw.write(zone + "," + pt + "," + zdc);
//				bw.newLine();
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
//		return outfile;
//	}

	public static File aggregatebyZone(File infile, File outfile){
		HashSet<Integer> zones = new HashSet<Integer>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedReader br1 = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

			String line = null;
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				int zone = Integer.parseInt(tokens[1]);
				zones.add(zone);
			}				
			System.out.println(zones);
			
			ArrayList<Double> pt = new ArrayList<Double>();
			int counter = 0;
			for(Integer z:zones){
//				ArrayList<Double> zdc = new ArrayList<Double>();

				double sumpt = 0d;
//				double sumzdc= 0d;

				String line1 = null;
				while((line1 = br1.readLine()) != null){
					String[] tokens = line1.split(",");
					Integer zone = Integer.parseInt(tokens[1]);
					if(zone.intValue() == (z.intValue())){
						pt.add(Double.parseDouble(tokens[2]));
//						zdc.add(Double.parseDouble(tokens[2]));
					}
				}
				for(double p : pt){
					sumpt = sumpt + p;
				}
//				for(double zd : zdc){
//					sumzdc = sumzdc + zd;
//				}
				bw.write(z + "," + sumpt);
				bw.newLine();

				counter = counter + 1;
				pt.clear();
			}
			bw.close();
			br.close();
			br1.close();
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
