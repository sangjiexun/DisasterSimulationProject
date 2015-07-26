package PTtoPFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class PT_TripAnalyzer {

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static void main(String[] args) throws Exception {
		File ptoriginal = new File("c:/users/yabetaka/Desktop/chiyoda-shinjuku-bunkyo.txt"); 
		File newdir = new File("c:/users/yabetaka/Desktop/PTAnalyze");
		newdir.mkdir();

		Map<String,Map<String,List<MidtermData>>> alldata = loadMidtermData(ptoriginal);
		System.out.println("loaded data!");

		List<String> pids = new ArrayList<String>(alldata.keySet());

		HashMap<Integer,ArrayList<Integer>> hour_people = new HashMap<Integer,ArrayList<Integer>>();

		for(String pid : pids) {
			Map<String,List<MidtermData>> trips    = alldata.get(pid);
			List<List<MidtermData>>       tripList = new ArrayList<List<MidtermData>>(trips.values());

			int	magfac = tripList.get(0).get(0).getExfactor1();

			for(List<MidtermData> subtrips : tripList) {
				for(MidtermData subtrip : subtrips) {

					if((subtrip.getPurpose() == 1)||(subtrip.getPurpose() == 2)){  //‹A‘î
						Date time = subtrip.getDepPoint().getTimeStamp();
						int hour = time.getHours();

						if(hour_people.containsKey(hour)){
							hour_people.get(hour).add(magfac);
						}
						else{
							ArrayList<Integer> list = new ArrayList<Integer>();
							list.add(magfac);
							hour_people.put(hour, list);
						}
					}
				}
			}		
		}
		for(Integer hour : hour_people.keySet()){
			Integer sum = getSum(hour_people.get(hour));
			System.out.println(hour + "," + sum);
		}
	}
	
	public static Integer getSum(ArrayList<Integer> mfs){
		Integer sum = 0;
		for(Integer mf : mfs){
			sum = sum + mf;
		}
		return sum;
	}

	public static Map<String,Map<String,List<MidtermData>>> loadMidtermData(File infile) {
		Map<String,Map<String,List<MidtermData>>> result = new TreeMap<String,Map<String,List<MidtermData>>>();
		if( !infile.exists() ) { return result; }

		BufferedReader br = null;
		try {
			// start loading ////////////////////
			br = new BufferedReader(new FileReader(infile));
			String line = null;
			while( (line=br.readLine()) != null ) {
				MidtermData data = MidtermData.parse(line);	// sub-trip
				String pid    = data.getPID();

				if( !result.containsKey(pid) ) {
					result.put(pid,new TreeMap<String,List<MidtermData>>());
				}

				Map<String,List<MidtermData>> trips = result.get(pid);
				String tripid = data.getTripNo();
				if( !trips.containsKey(tripid) ) {
					trips.put(tripid,new ArrayList<MidtermData>());
				}
				trips.get(tripid).add(data);
			}
		}
		catch(IOException exp) {    exp.printStackTrace(); }
		finally {
			try { if( br != null ) { br.close(); } }
			catch(IOException exp) { exp.printStackTrace(); }
		}
		return result;
	}
}
