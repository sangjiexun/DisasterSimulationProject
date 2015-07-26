package PTtoPFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.STPoint;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;


public class PTver4HomeGoal {

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static void main(String[] args) throws Exception {
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname
		File ptoriginal = new File("C:/Users/yabec_000/Desktop/chiyoda-shinjuku-bunkyo.txt");
		File outfile = new File ("c:/Users/yabec_000/Desktop/Tokyo_PTver7_HomeGoal.csv");
		File mffile = new File ("c:/Users/yabec_000/Desktop/Final_Pid-Zone-Magfac.csv");
		File zones = new File ("c:/Users/yabec_000/Desktop/zonesin3wards.csv");

		Date targetdate = SDF_TS.parse("2008-10-01 14:47:00");

		Map<String,Map<String,List<MidtermData>>> alldata = loadMidtermData(ptoriginal);	// read all data

		List<String> pids = new ArrayList<String>(alldata.keySet());	// all pids
		int counter = 1;
		int nomf = 0;

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));

		for(String pid : pids ) {
			Map<String,List<MidtermData>> trips    = alldata.get(pid);
			List<List<MidtermData>>       tripList = new ArrayList<List<MidtermData>>(trips.values());

			Integer id = Integer.valueOf(pid);
			int magfac;
			if (getMF(mffile).containsKey(id)){
				magfac = getMF(mffile).get(id);
//				System.out.println("magfac OK!");
			}
			else{
				magfac = tripList.get(0).get(0).getExfactor1();
				nomf = nomf + 1;
			}			

			for(int i=0;i<=magfac;i++) {

				if(counter % 50 == 0){
					// routing
					Connection con = DBCPLoader.getPgSQLConnection();

					for(List<MidtermData> subtrips : tripList) {
						for(MidtermData subtrip : subtrips) {
							STPoint dep = subtrip.getDepPoint();
							STPoint arr = subtrip.getArrPoint();

							if ((dep.getTimeStamp().before(targetdate)
									&&arr.getTimeStamp().after(targetdate))
									||dep.getTimeStamp().equals(targetdate)
									||arr.getTimeStamp().equals(targetdate)){

								LonLat home    = allocatewithRisk(subtrip.getAddress().substring(3, 8), 3);
//								System.out.println("home " + home);
								LonLat goal    = allocatewithRisk(subtrip.getGoalloc().substring(3, 8), 1);
//								System.out.println("goal " + goal);

								bw.write(counter
										+ "," + id
										+ "," + magfac
										+ "," + subtrip.getSex()
										+ "," + subtrip.getAge()
										+ "," + subtrip.getTransport()
										+ "," + subtrip.getPurpose()
										+ "," + home.getLon()
										+ "," + home.getLat()
										+ "," + goal.getLon()
										+ "," + goal.getLat()
										);
								bw.newLine();
								System.out.println(counter);
								counter = counter + 1;
							}
						}
					}
					con.close();
				}
				else{
					counter = counter + 1;
				}
			}
		}
		System.out.println("magfac not properly taken: " + nomf);
		System.out.println("number of people: " + counter);
		bw.close();
		// cleanup DB connection //////////////////////////
		DBCPLoader.closePgSQLConnection();
	}


	static PT_Locations allocator = new PT_Locations() {
		protected FacilityType getTargetFacility(int purpose) {
			switch(purpose) {
			// for business =======================
			case  1: case  4: case  5: case  6: case  7: case 8:
			case 10: case 11: case 12: case 13: case 14: case 99:
				return FacilityType.BUSINESS;
				// for school =========================
			case  2:
				return FacilityType.SCHOOL;
				// for home ===========================
			case  3: case 9:
				return FacilityType.HOME;
				// error ==============================
			default :
				return null;
			}
		}
		public int getPurposeOfGoingHome() {
			return 3;
		}
	};

	public static HashMap<Integer, Integer> getMF(File infile){
		HashMap<Integer, Integer> mf = new HashMap<Integer, Integer>();
		try{
			BufferedReader mfreader = new BufferedReader(new FileReader(infile));
			String line = null;
			while ((line = mfreader.readLine()) != null){
				String[] tokens = line.split(",");
				Integer mfid = Integer.valueOf(tokens[0]);
				Integer exfac = (int)Math.floor(Double.parseDouble(tokens[2]));
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

	/**
	 * 中間データのロード
	 * @param infile データファイル
	 * @return 全データ(key=PersonID, value=trip data(key=trip id, value=sub-trips))
	 */
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

	public static LonLat allocatewithRisk(String zonecode, int purpose){
		LonLat point    = allocator.allocate(zonecode, purpose);
		if (point != null){
			System.out.println("1: " + point);
			return point;
		}
		else{	
			LonLat point2 = PTZoneUtils.getZoneRepresentative(Integer.valueOf(zonecode));
			System.out.println("2: " + zonecode);
			System.out.println("2: " + point2);
			if (point2 == null){
				LonLat point3 = new LonLat(0,0);
				System.out.println("3: " + point3);
				return point3;
			}
			return point2;
		}
	}	
}
