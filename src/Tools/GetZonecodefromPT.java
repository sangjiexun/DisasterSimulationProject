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
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;

public class GetZonecodefromPT {
	
	public static void main(String args[]){
		File pt = new File("c:/users/yabec_000/desktop/pflowTokyo.csv");
		File out = new File("c:/users/yabec_000/desktop/pflowTokyoNEW.csv");
		getZonefromPT(pt,out);
	}

	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static File getZonefromPT(File ptfile, File outfile){

		try{
			BufferedReader br = new BufferedReader(new FileReader(ptfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = br.readLine();
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(";");
				Double lon = Double.parseDouble(tokens[10]);
				Double lat = Double.parseDouble(tokens[11]);
				Mesh mesh = new Mesh(5,lon,lat);
				LonLat center = mesh.getCenter();
				List<String> zonecodeList = gchecker.listOverlaps("zonecode",center.getLon(),center.getLat());

				if( zonecodeList == null || zonecodeList.isEmpty() ){
					bw.write(line + "," + "0");
					bw.newLine();
				}
				else{
					String zonecode     = zonecodeList.get(0);			
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


}
