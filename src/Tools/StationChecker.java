package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;

public class StationChecker {

	public static void main(String args[]){
		File in = new File ("c:/users/yabec_000/desktop/AllStations2003.csv");
		File out = new File ("c:/users/yabec_000/desktop/StationsinFujisawa.csv");
		stationCheck(in,out);
	}


	//	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	static File shapedir = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/zone_bounds_fujisawa_shape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);


	public static File stationCheck(File infile, File outfile){
		int j = 0;
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				String id = tokens[0];
				String name = tokens[1];
				Double lon = Double.parseDouble(tokens[2]);
				Double lat = Double.parseDouble(tokens[3]);
				//				LonLat point = new LonLat(lon, lat);

				int len = name.length();

				for (int i = 0; i < len; i++) {
					char c = name.charAt(i);
					System.out.println(c);
				}

				List<String> zonecodeList = gchecker.listOverlaps("zonecode", lon, lat);
				if( zonecodeList == null || zonecodeList.isEmpty() ) {
					j++ ; 
					//					System.out.println(j);
				}
				else{
					bw.write(id + "," + name + "," + lon + "," + lat);
					bw.newLine();
					j++;
					System.out.println(j + "," + name);
				}	
			}
			br.close();
			bw.close();
			System.out.println("final: " + j);
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
