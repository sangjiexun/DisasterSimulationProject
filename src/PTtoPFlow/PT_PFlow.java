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
import jp.ac.ut.csis.pflow.routing.pgr.PgRouting;
import jp.ac.ut.csis.pflow.routing.res.Node;
import jp.ac.ut.csis.pflow.routing.res.Route;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

import com.beachstone.JWposChange;

public class PT_PFlow { //no magfac version

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static double[] convert(double lon,double lat) {
		JWposChange converter = new JWposChange(lat,lon,9);
		converter.LatLongtoXYW();
		return new double[]{converter.getX(),converter.getY()};
	}

	public static void main(String[] args) throws Exception {
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname

		File infile = new File("C:/Users/yabec_000/Desktop/chiyoda-shinjuku-bunkyo.txt");
		String filepath= "c:/Users/yabec_000/Desktop/Tokyo_nomagfac.csv";

		Date targetdate = SDF_TS.parse("2008-10-01 14:47:00");
		BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));

		PgRouting routing = new PgRouting(PgRouting.LOCALROAD_VEL_V2);
		PgRouting railwayrouting = new PgRouting(PgRouting.ALLROAD_V1);

		Map<String,Map<String,List<MidtermData>>> alldata = loadMidtermData(infile);	// read all data
		List<String> pids = new ArrayList<String>(alldata.keySet());	// all pids

		int zonecodeerror = 0;
//		int counter = 1;
		int person = 1;
		for(String pid : pids ) {
			Map<String,List<MidtermData>> trips    = alldata.get(pid);
			List<List<MidtermData>>       tripList = new ArrayList<List<MidtermData>>(trips.values());
//			int                           magfac   = tripList.get(0).get(0).getExfactor1();

//			for(int i=0;i<magfac;i++) {
				MidtermData prevDest = null;
				LonLat staypoint = null;
				int purpose = 1;

				for(List<MidtermData> subtrips : tripList) {	// check sub-trips in a trip
					MidtermData data           = subtrips.get(0);
					LonLat      allocatedPoint = staypoint == null?allocate(data.getDepPoint(), purpose):staypoint ;// reallocation
					if (allocatedPoint == null){
						allocatedPoint = allocate(data.getDepPoint(), 3);}

					data.getDepPoint().setLocation(allocatedPoint.getLon(),allocatedPoint.getLat());	// update dep position with allocated point

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
				Connection con = DBCPLoader.getPgSQLConnection();

				for(List<MidtermData> subtrips : tripList) {
					for(MidtermData subtrip : subtrips) {

						STPoint dep = subtrip.getDepPoint(); STPoint arr = subtrip.getArrPoint();

						//stay routing
						if ((dep.getTimeStamp().before(targetdate)
								&&arr.getTimeStamp().after(targetdate))
								||dep.getTimeStamp().equals(targetdate)
								||arr.getTimeStamp().equals(targetdate)){

							if (subtrip.isStay()){
								double convertedpoint[]= convert(dep.getLon(), dep.getLat());
								bw.write(pid 
//										+ i
										+ "," + subtrip.getSex()
										+ "," + subtrip.getAge()
										+ "," + subtrip.getAddress()
										+ "," + subtrip.getWorkplace()
										+ "," + subtrip.getPurpose()
										+ "," + subtrip.getTransport()
										+ "," + convertedpoint[0]
										+ "," + convertedpoint[1]);

								String homecode = subtrip.getAddress().substring(3,8);
								LonLat      allocatedHome  = allocator.allocate (homecode,3);//reallocate home;
								if (allocatedHome != null){
									double convertedhome[] = convert(allocatedHome.getLon(), allocatedHome.getLat());
									bw.write("," + convertedhome[0]+ "," + convertedhome[1]);}
								else{
									LonLat rep_point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(homecode));
									if (rep_point != null){
										double lat = rep_point.getLat();
										double lon = rep_point.getLon();
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);}
									else {
										double lat = 35.7;
										double lon = 139.7;
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);}
									zonecodeerror = zonecodeerror + 1;
								}



								String goalcode = subtrip.getGoalloc().substring(3, 8);
								LonLat      allocatedGoal  = allocator.allocate(goalcode,1);//reallocate goal
								if (allocatedGoal != null){
									double convertedgoal[] = convert(allocatedGoal.getLon(), allocatedGoal.getLat());
									bw.write("," + convertedgoal[0]+ "," + convertedgoal[1]);}
								else{
									LonLat rep_point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(goalcode));
									if (rep_point != null){
										double lat = rep_point.getLat();
										double lon = rep_point.getLon();
										double cp[]= convert(lon, lat);
										bw.write("," + cp[0] +","+ cp[1]);
									}
									else {
										double lat = 35.7;
										double lon = 139.7;
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);
										zonecodeerror = zonecodeerror + 1;
									}
								}
								bw.newLine();
							}

							//railway routing
							else if (subtrip.useRailway()){
								List<Route> routes = railwayrouting.getRoutes(con,
										subtrip.getDepPoint().getLon(),
										subtrip.getDepPoint().getLat(),
										subtrip.getArrPoint().getLon(),
										subtrip.getArrPoint().getLat(),
										true);
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

								STPoint targetpoint = null;
								for(STPoint p:r2) {
									if ( targetdate.equals(p.getTimeStamp())){
										targetpoint = p;
										break;
									}
								}
								double railwaypoint[]= convert(targetpoint.getLon(), targetpoint.getLat());

								bw.write(pid 
//										+ i
										+ "," + subtrip.getSex()
										+ "," + subtrip.getAge()
										+ "," + subtrip.getAddress()
										+ "," + subtrip.getWorkplace()
										+ "," + subtrip.getPurpose()
										+ "," + subtrip.getTransport()
										+ "," + railwaypoint[0]
												+ "," + railwaypoint[1]);

								String homecode = subtrip.getAddress().substring(3,8);
								LonLat      allocatedHome  = allocator.allocate (homecode,3);//reallocate home;
								if (allocatedHome != null){
									double convertedhome[] = convert(allocatedHome.getLon(), allocatedHome.getLat());
									bw.write("," + convertedhome[0]+ "," + convertedhome[1]);}
								else{
									LonLat rep_point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(homecode));
									if (rep_point != null){
										double lat = rep_point.getLat();
										double lon = rep_point.getLon();
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);}
									else {
										double lat = 35.7;
										double lon = 139.7;
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);
										zonecodeerror = zonecodeerror + 1;
									}
								}

								String goalcode = subtrip.getGoalloc().substring(3, 8);
								LonLat      allocatedGoal  = allocator.allocate(goalcode,1);//reallocate goal
								if (allocatedGoal != null){
									double convertedgoal[] = convert(allocatedGoal.getLon(), allocatedGoal.getLat());
									bw.write("," + convertedgoal[0]+ "," + convertedgoal[1]);
								}
								else{
									LonLat rep_point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(goalcode));
									if (rep_point != null){
										double lat = rep_point.getLat();
										double lon = rep_point.getLon();
										double cp[]= convert(lon, lat);
										bw.write("," + cp[0] +","+ cp[1]);
									}
									else {
										double lat = 35.7;
										double lon = 139.7;
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);
										zonecodeerror = zonecodeerror + 1;
									}
								}
								bw.newLine();
							}

							//walk&car routing
							else {
								List<Route> routes = routing.getRoutes(con,
										subtrip.getDepPoint().getLon(),
										subtrip.getDepPoint().getLat(),
										subtrip.getArrPoint().getLon(),
										subtrip.getArrPoint().getLat(),
										true);
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

								STPoint targetpoint = null;
								for(STPoint p:r2) {
									if ( targetdate.equals(p.getTimeStamp())){
										targetpoint = p;
										break;
									}
								}
								double point[]= convert(targetpoint.getLon(), targetpoint.getLat());

								bw.write(pid 
//										+ i
										+ "," + subtrip.getSex()
										+ "," + subtrip.getAge()
										+ "," + subtrip.getAddress()
										+ "," + subtrip.getWorkplace()
										+ "," + subtrip.getPurpose()
										+ "," + subtrip.getTransport()
										+ "," + point[0]
												+ "," + point[1]);

								String homecode = subtrip.getAddress().substring(3,8);
								LonLat      allocatedHome  = allocator.allocate (homecode,3);//reallocate home;
								if (allocatedHome != null){
									double convertedhome[] = convert(allocatedHome.getLon(), allocatedHome.getLat());
									bw.write("," + convertedhome[0]+ "," + convertedhome[1]);}
								else{
									LonLat rep_point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(homecode));
									if (rep_point != null){
										double lat = rep_point.getLat();
										double lon = rep_point.getLon();
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);}
									else {
										double lat = 35.7;
										double lon = 139.7;
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);
										zonecodeerror = zonecodeerror + 1;
									}
								}


								String goalcode = subtrip.getGoalloc().substring(3, 8);
								LonLat      allocatedGoal  = allocator.allocate(goalcode,1);//reallocate goal
								if (allocatedGoal != null){
									double convertedgoal[] = convert(allocatedGoal.getLon(), allocatedGoal.getLat());
									bw.write("," + convertedgoal[0]+ "," + convertedgoal[1]);}
								else{
									LonLat rep_point = PTZoneUtils.getZoneRepresentative(Integer.valueOf(goalcode));
									if (rep_point != null){
										double lat = rep_point.getLat();
										double lon = rep_point.getLon();
										double cp[]= convert(lon, lat);
										bw.write("," + cp[0] +","+ cp[1]);}
									else {
										double lat = 35.7;
										double lon = 139.7;
										double cp[]= convert(lon,lat);
										bw.write("," + cp[0] +","+ cp[1]);
										zonecodeerror = zonecodeerror + 1;
									}
								}
								bw.newLine();
							}
						}
					}
				}
				con.close();
//				System.out.println(counter);
//				counter = counter + 1;
//			}
			person = person + 1;
			System.out.println(person);
			System.out.println("zonecode errors : " +zonecodeerror);
		}
		bw.close();
		// cleanup DB connection ////////////////////////
		DBCPLoader.closePgSQLConnection();
	}


	static PT_Locations allocator = new PT_Locations() {
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

	static File shapedir = new File("C:/Users/yabec_000/Desktop/tokyo11area-zoneshape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static LonLat allocate(STPoint point, int purpose) {
		List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {return  point; }
		String       zonecode     = zonecodeList.get(0);
		return allocator.allocate(zonecode, purpose) ;
		// return reallocated points
	}

	/**
	 * 闕ｳ�ｽｭ鬮｢阮吶Ι郢晢ｽｼ郢ｧ�ｽｿ邵ｺ�ｽｮ郢晢ｽｭ郢晢ｽｼ郢晢ｿｽ
	 * @param infile 郢晢ｿｽ�ｿｽ郢ｧ�ｽｿ郢晁ｼ斐＜郢ｧ�ｽ､郢晢ｽｫ
	 * @return 陷茨ｽｨ郢晢ｿｽ�ｿｽ郢ｧ�ｽｿ(key=PersonID, value=trip data(key=trip id, value=sub-trips))
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
