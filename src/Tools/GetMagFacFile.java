package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;

public class GetMagFacFile {

	public static void main(String args[]){
		File noMagFac = new File ("C:/Users/yabec_000/Desktop/WakoMFProject/Tokyo_noMagFac.csv");
		File wakoMF   = new File ("C:/Users/yabec_000/Desktop/WakoMFProject/Tokyo_newMagFac.csv");
		File result   = new File ("C:/Users/yabec_000/Desktop/Pid-Magfac-Zone.csv");
		
		getMagFacandZone(noMagFac, wakoMF, result);
		
	}

	public static HashMap<Integer, Integer> lookinWakoMF(File mffile){
		HashMap<Integer, Integer> mf = new HashMap<Integer, Integer>();
		try{
			BufferedReader mfreader = new BufferedReader(new FileReader(mffile));
			String line = null;
			while ((line = mfreader.readLine()) != null){
				String[] tokens = line.split(",");
				Integer mfid = Integer.valueOf(tokens[0]);
				Integer exfac = Integer.valueOf(tokens[1]);
				mf.put(mfid, exfac);
			}
			mfreader.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return mf;
	}

	public static File getMagFacandZone(File infile, File wakofile, File outfile){
		int counter = 1;
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = br.readLine();
			while((line = br.readLine()) != null){
//				System.out.println(line);
				String[] tokens = line.split(",");
				String pid = tokens[0];
				Integer id = Integer.valueOf(pid);
//				System.out.println(id);
				int magfac;
				if (lookinWakoMF(wakofile).containsKey(id)){
					magfac = lookinWakoMF(wakofile).get(id);
				}
				else{
					magfac = Integer.valueOf(tokens[1]);
				}	
//				System.out.println(magfac);
				//get zone from lonlat
				Double lon = Double.parseDouble(tokens[5]);
				Double lat = Double.parseDouble(tokens[6]);
				LonLat point = new LonLat(lon, lat);
				
				bw.write(pid + "," + getZonecode(point) + "," + magfac);
				bw.newLine();
				System.out.println(counter);
				counter = counter + 1;
			}
			br.close();
			bw.close();

		}
		catch(FileNotFoundException xx) {
			System.out.println("sorry, couldnt find the fucking file");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return outfile;
	}
	
	static File shapedir = new File("C:/Users/yabec_000/Desktop/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static String getZonecode(LonLat point) {
		List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {return  "0"; }
		String       zonecode     = zonecodeList.get(0);
		return zonecode;
	}
}
