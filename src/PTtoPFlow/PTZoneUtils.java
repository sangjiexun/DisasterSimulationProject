package PTtoPFlow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import jp.ac.ut.csis.pflow.geom.LonLat;

/*
 * Written by H.Kanasugi
 * Edited by T.Yabe
 * @ 12/12/2014
 */

public class PTZoneUtils {

	// class initialization start /////////////////////////
	public static final File ZONE_REPRESENTATIVE_FILE = new File("C:/Users/yabec_000/Documents/AllMyWork/SekimotoLab/DocumentsIMade/FundamentalData/New-Zone-Point.csv");
	public static Map<Integer,LonLat> ZONE_POINTS      = new Hashtable<Integer,LonLat>();
	static {
		if( ZONE_REPRESENTATIVE_FILE.exists() ) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(ZONE_REPRESENTATIVE_FILE));
				String line = null;
				while((line=br.readLine())!=null) {
					String tokens[] = line.split(",");
					Integer zonecode = Integer.valueOf(tokens[0]);
					double lon      = Double.parseDouble(tokens[1]);
					double lat      = Double.parseDouble(tokens[2]);
					ZONE_POINTS.put(zonecode,new LonLat(lon,lat));
				}
			}
			catch(IOException exp) { exp.printStackTrace(); }
			finally {
				try{ if( br != null ) { br.close(); } }
				catch(IOException exp) { exp.printStackTrace(); }
			}
		}
		else {
			System.err.println(ZONE_REPRESENTATIVE_FILE + " does not exist");
		}
	}

	public static LonLat getZoneRepresentative(Integer zonecode) {
		return ZONE_POINTS.get(zonecode);	// returns null if zonecode does not exist
	}

}