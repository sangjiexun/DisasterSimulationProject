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
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;

public class ZDCDisaggChooser {

	public static void main(String args[]) throws ParseException{
		File zdc = new File("c:/users/yabec_000/Desktop/Snow_Tokyo.csv");
		File out = new File("c:/users/yabec_000/Desktop/Snow_Tokyo_12.csv");

		HashMap<Integer,LonLat> list = new HashMap<Integer,LonLat>();
		try{
			int counter = 1;
			BufferedReader br = new BufferedReader(new FileReader(zdc));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			while((line = br.readLine())!=null){
				if(ChoosebyTime(line, "12:00")==true){
					String[] tokens = line.split(",");
					Integer id = Integer.valueOf(tokens[0]);
					Double lon = Double.parseDouble(tokens[2]);
					Double lat = Double.parseDouble(tokens[3]);
					LonLat point = new LonLat(lon,lat);
					list.put(id, point);
				}
			}
			System.out.println("done choosing by time");
			br.close();
			for(Integer key:list.keySet()){
				if(gchecker.checkOverlap(list.get(key).getLon(),  list.get(key).getLat())){
					bw.write(key + "," + list.get(key).getLon() + "," + list.get(key).getLat());
					bw.newLine();
					if(counter%1000==0){
						System.out.println(counter);
					}
					counter++;
				}
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

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static boolean ChoosebyTime(String line, String time) throws ParseException{
		String[] tokens = line.split(",");
		Date beforedate = SDF_TS.parse("2013-01-14 00:00:00");
		Date afterdate = SDF_TS.parse("2013-01-14 " + time + ":00");

		Date date = SDF_TS.parse(tokens[1]);
		if(date.before(afterdate)&&date.after(beforedate)){
			//			System.out.println(afterdate + "," + date + ", true");
			return true;
		}
		else{
			//			System.out.println(afterdate + "," + date + ", false");
			return false;
		}
	}

}
