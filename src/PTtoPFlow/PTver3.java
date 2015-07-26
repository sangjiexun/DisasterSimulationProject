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

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.geom.STPoint;
import jp.ac.ut.csis.pflow.geom.TrajectoryUtils;
import jp.ac.ut.csis.pflow.routing.pgr.PgRouting;
import jp.ac.ut.csis.pflow.routing.res.Node;
import jp.ac.ut.csis.pflow.routing.res.Route;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;
import Tools.NewPTAllocation;


public class PTver3 {

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static void main(String[] args) throws Exception {
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname
		File ptoriginal = new File("C:/Users/yabec_000/Desktop/chiyoda-shinjuku-bunkyo.txt");
		File outfile = new File ("c:/Users/yabec_000/Desktop/Tokyo_PTver8_forSim.csv");
		File mffile = new File ("c:/Users/yabec_000/Desktop/Last_Pid-Zone-Magfac.csv");
		File zones = new File ("c:/Users/yabec_000/Desktop/zonesin3wards.csv");

		Date targetdate = SDF_TS.parse("2008-10-01 14:47:00");
		PgRouting routing = new PgRouting(PgRouting.LOCALROAD_V2);
		PgRouting railwayrouting = new PgRouting(PgRouting.LOCALROAD_V2);

		ArrayList<String> errorMesh = new ArrayList<String>();
		Map<String,Map<String,List<MidtermData>>> alldata = loadMidtermData(ptoriginal);	// read all data

		List<String> pids = new ArrayList<String>(alldata.keySet());	// all pids
		int counter = 1;
		int stay = 0;
		int railway = 0;
		int others = 0;
		int yes = 1;
		int school = 0;
		int outside = 0;
		int inside = 0;
		int nobuil = 0;
		int nozone = 0;

		System.out.println("zones: " + getZones(zones));

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));

		for(String pid : pids ) {
			Map<String,List<MidtermData>> trips    = alldata.get(pid);
			List<List<MidtermData>>       tripList = new ArrayList<List<MidtermData>>(trips.values());
			Integer purpose  = tripList.get(0).get(0).getPurpose();

			Integer id = Integer.valueOf(pid);
			int magfac;
			if (getMF(mffile).containsKey(id)){
				magfac = getMF(mffile).get(id);
				System.out.println("magfac OK!");
			}
			else{
				magfac = tripList.get(0).get(0).getExfactor1();
				System.out.println("magfac not properly taken");
			}			

			for(int i=0;i<magfac;i++) {

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

								LonLat alloDep = allocate(dep, purpose);
								LonLat alloArr = allocate(arr, purpose);
								LonLat home    = allocatewithRisk(subtrip.getAddress().substring(3, 8), 3, zones);
							//	System.out.println(home);
								LonLat goal    = allocatewithRisk(subtrip.getGoalloc().substring(3, 8), 1, zones);
							//	System.out.println(goal);
								LonLat now;

								if (purpose.equals("2")){
									List<Route> routes = routing.getRoutes(con,
											alloDep.getLon(),
											alloDep.getLat(),
											alloArr.getLon(),
											alloArr.getLat(),
											false);
									Route r = null;
									if (routes == null){
										r = new Route();
										r.add(new Node("dep",alloDep.getLon(),alloDep.getLat()),0);
										r.add(new Node("arr",alloArr.getLon(),alloArr.getLat()),0);
									}
									else {
										r = routes.get(0);
									}

									List<STPoint> r2 = TrajectoryUtils.interpolateUnitTime(r.listNodes(),
											subtrip.getDepPoint().getTimeStamp(),
											subtrip.getArrPoint().getTimeStamp());

									STPoint targetpoint = null;
									for(STPoint p:r2) {
										if ( targetdate.equals(p.getTimeStamp())){
											targetpoint = p;
											break;
										}
									}

									List<String> zonecodeList = gchecker.listOverlaps("zonecode",targetpoint.getLon(),targetpoint.getLat());
									if( zonecodeList == null || zonecodeList.isEmpty() ) {
										now = new LonLat(targetpoint.getLon(), targetpoint.getLat());
									}
									else{
										String zonecode = String.valueOf(Integer.valueOf(zonecodeList.get(0)));
										now = allocator.allocate(zonecode, 2);
										if (now == null){
											now = new LonLat(targetpoint.getLon(), targetpoint.getLat());
										}
									}
									bw.write(counter
											+ "," + id
											+ "," + magfac
											+ "," + subtrip.getSex()
											+ "," + subtrip.getAge()
											+ "," + subtrip.getTransport()
											+ "," + subtrip.getPurpose()
											+ "," + now.getLon()
											+ "," + now.getLat()
											+ "," + home.getLon()
											+ "," + home.getLat()
											+ "," + goal.getLon()
											+ "," + goal.getLat()
											);
									bw.newLine();
									school = school + 1;
									System.out.println(school);
								}
								else{
									if (subtrip.isStay()){
										List<String> zonecodeList = gchecker.listOverlaps("zonecode",alloDep.getLon(),alloDep.getLat());
										if( zonecodeList == null || zonecodeList.isEmpty() ) { //if he is outside 3 wards
											now = new LonLat(alloDep.getLon(),alloDep.getLat());
											outside = outside + 1;
										}
										else{ // he is in the 3 wards
											String zonecode = String.valueOf(Integer.valueOf(zonecodeList.get(0))); //get zonecode
											if(getZones(zones).contains(zonecode)){
												inside = inside + 1;
												now = NewPTAllocation.allocateWithinMesh(NewPTAllocation.getMeshcode(zonecode), subtrip.getPurpose());
												if (now == null){
													Mesh mesh = new Mesh(NewPTAllocation.getMeshcode(zonecode));
													now = mesh.getCenter(); // get the freaking center of the mesh as point
													nobuil = nobuil + 1;
												}
											}
											else{ //if there are no meshes in that zone
												LonLat point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(zonecode)); 
												Mesh mesh = new Mesh(5, point.getLon(), point.getLat());
												now = mesh.getCenter(); //get the rep point of zone.
												nozone = nozone + 1;
											}
										}

										bw.write(counter
												+ "," + id
												+ "," + magfac
												+ "," + subtrip.getSex()
												+ "," + subtrip.getAge()
												+ "," + subtrip.getTransport()
												+ "," + subtrip.getPurpose()
												+ "," + now.getLon()
												+ "," + now.getLat()
												+ "," + home.getLon()
												+ "," + home.getLat()
												+ "," + goal.getLon()
												+ "," + goal.getLat()
												);
										bw.newLine();
										stay = stay + 1;
									}

									else if (subtrip.useRailway()){
										List<Route> routes = railwayrouting.getRoutes(con,
												alloDep.getLon(),
												alloDep.getLat(),
												alloArr.getLon(),
												alloArr.getLat(),
												false);
										Route r = null;
										if (routes == null){
											r = new Route();
											r.add(new Node("dep",alloDep.getLon(),alloDep.getLat()),0);
											r.add(new Node("arr",alloArr.getLon(),alloArr.getLat()),0);
										}
										else {
											r = routes.get(0);
										}

										List<STPoint> r2 = TrajectoryUtils.interpolateUnitTime(r.listNodes(),
												subtrip.getDepPoint().getTimeStamp(),
												subtrip.getArrPoint().getTimeStamp());

										STPoint targetpoint = null;
										for(STPoint p:r2) {
											if ( targetdate.equals(p.getTimeStamp())){
												targetpoint = p;
												break;
											}
										}

										List<String> zonecodeList = gchecker.listOverlaps("zonecode",targetpoint.getLon(),targetpoint.getLat());
										if( zonecodeList == null || zonecodeList.isEmpty() ) {
											now = new LonLat(targetpoint.getLon(), targetpoint.getLat()); 
											outside = outside + 1;
										}
										else{
											String zonecode = String.valueOf(Integer.valueOf(zonecodeList.get(0))); //get zonecode
											if(getZones(zones).contains(zonecode)){
												inside = inside + 1;
												now = NewPTAllocation.allocateWithinMesh(NewPTAllocation.getMeshcode(zonecode), subtrip.getPurpose());
												if (now == null){
													Mesh mesh = new Mesh(NewPTAllocation.getMeshcode(zonecode));
													now = mesh.getCenter(); // get the freaking center of the mesh as point
													nobuil = nobuil+ 1;
												}
											}
											else{ //if there are no meshes in that zone
												LonLat point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(zonecode)); 
												Mesh mesh = new Mesh(5, point.getLon(), point.getLat());
												now = mesh.getCenter(); //get the rep point of zone.
												nozone = nozone + 1;
											}
										}
										bw.write(counter
												+ "," + id
												+ "," + magfac
												+ "," + subtrip.getSex()
												+ "," + subtrip.getAge()
												+ "," + subtrip.getTransport()
												+ "," + subtrip.getPurpose()
												+ "," + now.getLon()
												+ "," + now.getLat()
												+ "," + home.getLon()
												+ "," + home.getLat()
												+ "," + goal.getLon()
												+ "," + goal.getLat()
												);
										bw.newLine();
										railway = railway + 1;
									}

									else {
										List<Route> routes = routing.getRoutes(con,
												alloDep.getLon(),
												alloDep.getLat(),
												alloArr.getLon(),
												alloArr.getLat(),
												false);
										Route r = null;
										if (routes == null){
											r = new Route();
											r.add(new Node("dep",alloDep.getLon(),alloDep.getLat()),0);
											r.add(new Node("arr",alloArr.getLon(),alloArr.getLat()),0);
										}
										else {
											r = routes.get(0);
										}

										List<STPoint> r2 = TrajectoryUtils.interpolateUnitTime(r.listNodes(),
												subtrip.getDepPoint().getTimeStamp(),
												subtrip.getArrPoint().getTimeStamp());

										STPoint targetpoint = null;
										for(STPoint p:r2) {
											if ( targetdate.equals(p.getTimeStamp())){
												targetpoint = p;
												break;
											}
										}

										List<String> zonecodeList = gchecker.listOverlaps("zonecode",targetpoint.getLon(),targetpoint.getLat());
										if( zonecodeList == null || zonecodeList.isEmpty() ) {
											now = new LonLat(targetpoint.getLon(), targetpoint.getLat());
											outside = outside + 1;
										}
										else{
											String zonecode = String.valueOf(Integer.valueOf(zonecodeList.get(0))); //get zonecode
											if(getZones(zones).contains(zonecode)){
												inside = inside + 1;
												now = NewPTAllocation.allocateWithinMesh(NewPTAllocation.getMeshcode(zonecode), subtrip.getPurpose());
												if (now == null){
													Mesh mesh = new Mesh(NewPTAllocation.getMeshcode(zonecode));
													now = mesh.getCenter(); // get the freaking center of the mesh as point
													nobuil = nobuil + 1;
												}
											}
											else{ //if there are no meshes in that zone
												LonLat point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(zonecode)); 
												Mesh mesh = new Mesh(5, point.getLon(), point.getLat());
												now = mesh.getCenter(); //get the rep point of zone.
												nozone = nozone + 1;
											}
										}
										bw.write(counter
												+ "," + id
												+ "," + magfac
												+ "," + subtrip.getSex()
												+ "," + subtrip.getAge()
												+ "," + subtrip.getTransport()
												+ "," + subtrip.getPurpose()
												+ "," + now.getLon()
												+ "," + now.getLat()
												+ "," + home.getLon()
												+ "," + home.getLat()
												+ "," + goal.getLon()
												+ "," + goal.getLat()
												);
										bw.newLine();
										others = others + 1;
									}
								}
								System.out.println("chosen: " + yes + ", school: " + school + ", outside: "+ outside + ", nobuil: " + nobuil + ", nozone: " + nozone);
								counter = counter + 1;
								yes = yes + 1;
							}
						}
					}

					con.close();
				}
				else{
					//					System.out.println(counter + "," + yes);
					counter = counter + 1;
				}
			}
		}
		bw.close();
		System.out.println(counter +","+ stay +","+ railway +","+ others);
		System.out.println(errorMesh);
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

	public static ArrayList<String> getZones(File infile){
		ArrayList<String> list = new ArrayList<String>();
		try{
			BufferedReader zonereader = new BufferedReader(new FileReader(infile));
			String line = null;
			while ((line = zonereader.readLine()) != null){
				String[] tokens = line.split(",");
				String zones = tokens[0];
				list.add(zones);
			}
			zonereader.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return list;
	}

	static File shapedir = new File("C:/Users/yabec_000/Desktop/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static LonLat allocate(LonLat point, int purpose) {
		List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {return  point; }
		String       zonecode     = zonecodeList.get(0);
		return allocator.allocate(zonecode, purpose) ;
	}

	/**
	 * 荳ｭ髢薙ョ繝ｼ繧ｿ縺ｮ繝ｭ繝ｼ繝�
	 * @param infile 繝�繝ｼ繧ｿ繝輔ぃ繧､繝ｫ
	 * @return 蜈ｨ繝�繝ｼ繧ｿ(key=PersonID, value=trip data(key=trip id, value=sub-trips))
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

	public static LonLat allocatewithRisk(String zonecode, int purpose, File zones){
		LonLat point;
		if(getZones(zones).contains(String.valueOf(Integer.parseInt(zonecode)))){
			point    = allocator.allocate(zonecode, purpose);
//			System.out.println("up");
		}
		else{	
//			System.out.println(zonecode);
			point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(zonecode));
			if(point == null){
				point = new LonLat(0,0);
			}
		}
		return point;
	}	
}
