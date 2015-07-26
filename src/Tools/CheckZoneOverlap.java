package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;

public class CheckZoneOverlap {

	public static void main(String args[]) throws ParseException{
		File ptfile = new File ("c:/Users/yabec_000/Desktop/GPSdisagg/Tokyo_gpsdisagg_14.csv");
		File newptfile = new File ("c:/Users/yabec_000/Desktop/GPSdisagg/Tokyo_gpsdisagg_14_311.csv");
		
//		File ptfile = new File ("c:/Users/yabec_000/Desktop/GPSdisagg/Fujisawa_gpsdisagg_14.csv");
//		File newptfile = new File ("c:/Users/yabec_000/Desktop/GPSdisagg/Fujisawa_gpsdisagg_14_311.csv");
		
		checkTimeOverlap(ptfile,newptfile);
	}

	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
//	static File shapedir = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/zone_bounds_fujisawa_shape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);
	
	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format
	
	public static File checkOverlap(File ptfile, File newpt){

		try{
			BufferedReader br = new BufferedReader(new FileReader(ptfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newpt));
			String line = null;
			while((line = br.readLine()) != null){
				String tokens[] = line.split(",");
				Double lon = Double.parseDouble(tokens[3]);
				Double lat = Double.parseDouble(tokens[2]);
				System.out.println(lon);

				List<String> zonecodeList = gchecker.listOverlaps("zonecode",lon,lat);
				if( zonecodeList == null || zonecodeList.isEmpty() ) 
				{continue;}
				else{
					bw.write(line);
					bw.newLine();
				}
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return newpt;
	}
	
	public static File checkTimeOverlap(File ptfile, File newpt) throws ParseException{

		Date targetdate1 = SDF_TS.parse("2011-03-11 00:00:00");
		Date targetdate2 = SDF_TS.parse("2011-03-11 23:00:00");
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(ptfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newpt));
			String line = null;
			while((line = br.readLine()) != null){
				String tokens[] = line.split(",");
				String time = tokens[1];
				Date targetdate = SDF_TS.parse(time);

				System.out.println(targetdate);

				if(targetdate.before(targetdate2)&&targetdate.after(targetdate1)) {
					bw.write(line);
					bw.newLine();
				}
				else{
					continue;
				}
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return newpt;
	}
	
}
