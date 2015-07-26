package PTtoPFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.STPoint;
import jp.ac.ut.csis.pflow.geom.TrajectoryUtils;
//import jp.ac.ut.csis.pflow.pt.interpolation.MidtermData;
import jp.ac.ut.csis.pflow.pt.parser.PTLocations;
import jp.ac.ut.csis.pflow.routing.pgr.PgRouting;
import jp.ac.ut.csis.pflow.routing.res.Node;
import jp.ac.ut.csis.pflow.routing.res.Route;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

import com.beachstone.JWposChange;


public class Home_1447_Goal_Location {

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static double[] convert(double lon,double lat) {
		JWposChange converter = new JWposChange(lat,lon,9);
		converter.LatLongtoXYW();
		return new double[]{converter.getX(),converter.getY()};
	}


	public static void main(String[] args) throws Exception {
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname
		File infile = new File("C:/Users/yabec_000/Desktop/testfile.txt");
		String outfile= "c:/Users/yabec_000/Desktop/home1447goallocation_ver2.txt";
		Date targetdate = SDF_TS.parse("2008-10-01 14:47:00");
		PgRouting routing = new PgRouting(PgRouting.LOCALROAD_VEL_V2);
		PgRouting railwayrouting = new PgRouting(PgRouting.ALLROAD_V1);

		Map<String,Map<String,List<MidtermData>>> alldata = loadMidtermData(infile);	// read all data

		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

		List<String> pids = new ArrayList<String>(alldata.keySet());	// all pids
		for(String pid : pids ) {
			Map<String,List<MidtermData>> trips    = alldata.get(pid);
			List<List<MidtermData>>       tripList = new ArrayList<List<MidtermData>>(trips.values());
			


			// loop for reallocation by magfac times

			//			for(int i=0;i<magfac;i++) { //id

			MidtermData prevDest = null;
			LonLat staypoint = null;
			int purpose = 3;

			for(List<MidtermData> subtrips : tripList) {	// check sub-trips in a trip
				MidtermData data           = subtrips.get(0);
				LonLat      allocatedPoint = staypoint == null?allocate( data.getDepPoint(), purpose):staypoint ;	// reallocation

				data.getDepPoint().setLocation(
						allocatedPoint.getLon(),allocatedPoint.getLat());	// update dep position with allocated point
				if( prevDest != null ) {
					prevDest.getArrPoint().setLocation(allocatedPoint.getLon(),allocatedPoint.getLat());	// update previous arrival position with the same allocated point
				}
				prevDest = subtrips.get(subtrips.size()-1);	// update previous destination

				if (data.isStay()){
					prevDest.getArrPoint().setLocation(allocatedPoint.getLon(), allocatedPoint.getLat());
					staypoint = allocatedPoint;
				}
				else {
					staypoint = null;
					purpose = prevDest.getPurpose();
				}
			}

			// routing
			Connection con=DBCPLoader.getPgSQLConnection();

			for(List<MidtermData> subtrips : tripList) {
				for(MidtermData subtrip : subtrips) {

					STPoint dep = subtrip.getDepPoint();
					STPoint arr = subtrip.getArrPoint();

					if ((dep.getTimeStamp().before(targetdate)
							&&arr.getTimeStamp().after(targetdate))
							||dep.getTimeStamp().equals(targetdate)
							||arr.getTimeStamp().equals(targetdate)){

						if (subtrip.isStay()){

							double convertedpoint[]= convert(dep.getLon(), dep.getLat());

							bw.write(pid + " " + subtrip.getExfactor1()
									+ "," + subtrip.getSex()
									+ "," + subtrip.getAge()
									+ "," + subtrip.getAddress()
									+ "," + subtrip.getGoalloc()
									+ "," + subtrip.getPurpose()
									+ "," + subtrip.getTransport()
									+ "," + convertedpoint[0]
											+ "," + convertedpoint[1]);

							String homecode = subtrip.getAddress().substring(3,8);
							LonLat      allocatedHome  = allocator.allocate (homecode,3);//reallocate home;
							if (allocatedHome != null){
								double convertedhome[] = convert(allocatedHome.getLon(), allocatedHome.getLat());
								bw.write("," + convertedhome[0]+ "," + convertedhome[1]);}
							else{bw.write(" ,Home outside Fujisawa Area ");};

							String goalcode = subtrip.getGoalloc().substring(3, 8);
							LonLat      allocatedGoal  = allocator.allocate(goalcode,1);//reallocate goal
							if (allocatedGoal != null){
								double convertedgoal[] = convert(allocatedGoal.getLon(), allocatedGoal.getLat());
								bw.write("," + convertedgoal[0]+ "," + convertedgoal[1]);}
							else{bw.write(" ,Goal outside Fujisawa Area ");}

							bw.newLine();
						}


						else {
							if (subtrip.useRailway()){
								List<Route> routes = railwayrouting.getRoutes(con,
										subtrip.getDepPoint().getLon(),
										subtrip.getDepPoint().getLat(),
										subtrip.getArrPoint().getLon(),
										subtrip.getArrPoint().getLat(),
										false);
								Route r = null;
								if (routes == null){
									r = new Route();
									r.add(new Node("dep",dep.getLon(),dep.getLat()),0);
									r.add(new Node("arr",arr.getLon(),dep.getLat()),0);
								}
								else {
									r = routes.get(0);
								}

								List<STPoint> r2 = TrajectoryUtils.interpolateUnitTime(r.listNodes(),
										subtrip.getDepPoint().getTimeStamp(),
										subtrip.getArrPoint().getTimeStamp());

								for(STPoint p:r2) {
									if ( targetdate.equals(p.getTimeStamp())){
										break;
									}
								}


								double convertedpoint[]= convert(dep.getLon(), dep.getLat());

								bw.write(pid + " " + subtrip.getExfactor1()
										+ "," + subtrip.getSex()
										+ "," + subtrip.getAge()
										+ "," + subtrip.getAddress()
										+ "," + subtrip.getGoalloc()
										+ "," + subtrip.getPurpose()
										+ "," + subtrip.getTransport()
										+ "," + convertedpoint[0]
												+ "," + convertedpoint[1]);

								String homecode = subtrip.getAddress().substring(3,8);
								LonLat      allocatedHome  = allocator.allocate (homecode,3);//reallocate home;
								if (allocatedHome != null){
									double convertedhome[] = convert(allocatedHome.getLon(), allocatedHome.getLat());
									bw.write("," + convertedhome[0]+ "," + convertedhome[1]);}
								else{bw.write(" ,Home outside Fujisawa Area ");};

								String goalcode = subtrip.getGoalloc().substring(3, 8);
								LonLat      allocatedGoal  = allocator.allocate(goalcode,1);//reallocate goal
								if (allocatedGoal != null){
									double convertedgoal[] = convert(allocatedGoal.getLon(), allocatedGoal.getLat());
									bw.write("," + convertedgoal[0]+ "," + convertedgoal[1]);}
								else{bw.write(" ,Goal outside Fujisawa Area ");}

								bw.newLine();
							}


							else {
								List<Route> routes = routing.getRoutes(con,
										subtrip.getDepPoint().getLon(),
										subtrip.getDepPoint().getLat(),
										subtrip.getArrPoint().getLon(),
										subtrip.getArrPoint().getLat(),
										false);
								Route r = null;
								if (routes == null){
									r = new Route();
									r.add(new Node("dep",dep.getLon(),dep.getLat()),0);
									r.add(new Node("arr",arr.getLon(),dep.getLat()),0);
								}
								else {
									r = routes.get(0);
								}

								List<STPoint> r2 = TrajectoryUtils.interpolateUnitTime(r.listNodes(),
										subtrip.getDepPoint().getTimeStamp(),
										subtrip.getArrPoint().getTimeStamp());

								for(STPoint p:r2) {
									if ( targetdate.equals(p.getTimeStamp())){
										break;
									}
								}

								//								double convertedpoint[]= convert(dep.getLon(), dep.getLat());

								bw.write(pid + " " + subtrip.getExfactor1()
										+ "," + subtrip.getSex()
										+ "," + subtrip.getAge()
										+ "," + subtrip.getAddress()
										+ "," + subtrip.getGoalloc()
										+ "," + subtrip.getPurpose()
										+ "," + subtrip.getTransport()
										+ "," + dep.getLon()
										+ "," + dep.getLat());

								String homecode = subtrip.getAddress().substring(3,8);
								LonLat      allocatedHome  = allocator.allocate (homecode,3);//reallocate home;
								if (allocatedHome != null){
									double convertedhome[] = convert(allocatedHome.getLon(), allocatedHome.getLat());
									bw.write("," + convertedhome[0]+ "," + convertedhome[1]);}
								else{bw.write(" ,Home outside Fujisawa Area ");};

								String goalcode = subtrip.getGoalloc().substring(3, 8);
								LonLat      allocatedGoal  = allocator.allocate(goalcode,1);//reallocate goal
								if (allocatedGoal != null){
									double convertedgoal[] = convert(allocatedGoal.getLon(), allocatedGoal.getLat());
									bw.write("," + convertedgoal[0]+ "," + convertedgoal[1]);}
								else{bw.write(" ,Goal outside Fujisawa Area ");}
								bw.newLine();
							}
						}
					}
				}
			}


			con.close();
			//		}
		}

		bw.close();
		// cleanup DB connection //////////////////////////
		DBCPLoader.closePgSQLConnection();
	}

	static PTLocations allocator = new PTLocations() {
		protected FacilityType getTargetFacility(int purpose) {
			switch(purpose) {
			// for business =======================
			case  1: case  4: case  5: case  6: case  7: case 8:
			case 10: case 11: case 12: case 13: case 14:
				return FacilityType.BUSINESS;
				// for school =========================
			case  2:
				return FacilityType.SCHOOL;
				// for home ===========================
			case  3:
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

	static File shapedir = new File("C:/Users/yabec_000/Desktop/realloc_fujisawa/zone_bounds_fujisawa_shape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static LonLat allocate(STPoint point, int purpose) {

		List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {return  point; }
		String       zonecode     = zonecodeList.get(0);
		return allocator.allocate(zonecode,1) ;
		// return realloated points
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
}
