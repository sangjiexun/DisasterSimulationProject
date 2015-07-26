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


/**
 *
 * @author H.Kanasugi@EDITORIA. UTokyo.
 * @since 2014/06/03
 * @version 0.0.0.1
 */

public class Test {

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	public static double[] convert(double lon,double lat) {
		JWposChange converter = new JWposChange(lat,lon,9);
		converter.LatLongtoXYW();
		return new double[]{converter.getX(),converter.getY()};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname

		File infile = new File("C:/Users/yabec_000/Desktop/H20Tokyo-Fujisawa.txt");

		Date targetdate = SDF_TS.parse("2008-10-01 14:47:00");

		Map<String,Map<String,List<MidtermData>>> alldata = loadMidtermData(infile);	// read all data

		String filepath= "c:/Users/yabec_000/Desktop/fujisawa1447.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));

		PgRouting routing = new PgRouting(PgRouting.LOCALROAD_VEL_V2);
		PgRouting railwayrouting = new PgRouting(PgRouting.LOCALROAD_VEL_V2);

		List<String> pids = new ArrayList<String>(alldata.keySet());	// all pids
		for(String pid : pids ) {
			Map<String,List<MidtermData>> trips    = alldata.get(pid);
			List<List<MidtermData>>       tripList = new ArrayList<List<MidtermData>>(trips.values());
			int                           magfac   = tripList.get(0).get(0).getExfactor1();
			int                           purpose  = tripList.get(0).get(0).getPurpose();

			// loop for reallocation by magfac times

			for(int i=0;i<magfac;i++) {
				MidtermData prevDest = null;

				for(List<MidtermData> subtrips : tripList) {	// check sub-trips in a trip
					MidtermData data           = subtrips.get(0);
					LonLat      allocatedPoint = allocate( data.getDepPoint(),purpose) ;	// reallocation
					data.getDepPoint().setLocation(
							allocatedPoint.getLon(),allocatedPoint.getLat());	// update dep position with allocated point
					if( prevDest != null ) {
						prevDest.getArrPoint().setLocation(allocatedPoint.getLon(),allocatedPoint.getLat());	// update previous arrival position with the same allocated point
					}
					prevDest = subtrips.get(subtrips.size()-1);	// update previous destination
				}
				LonLat  allocatedPoint = allocate( prevDest.getArrPoint(), purpose) ;	// reallocation
				prevDest.getArrPoint().setLocation(allocatedPoint.getLon(),allocatedPoint.getLat());


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

								bw.write(pid + " " + i
										+ "," + subtrip.getSex()
										+ "," + subtrip.getAge()
										+ "," + subtrip.getAddress()
										+ "," + subtrip.getWorkplace()
										+ "," + subtrip.getPurpose()
										+ "," + subtrip.getTransport()
										+ "," + convertedpoint[0]
										+ "," + convertedpoint[1]);
								bw.newLine();

								//								bw.write(pid + " " + i
								//										+ "," + subtrip.getSex()
								//										+ "," + subtrip.getAge()
								//										+ "," + SDF_TS.format(subtrip.getArrPoint().getTimeStamp())
								//										+ "," + subtrip.getArrPoint().getLon()
								//										+ "," + subtrip.getArrPoint().getLat());
								//								bw.newLine();

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

									STPoint targetpoint = null;
									for(STPoint p:r2) {
										if ( targetdate.equals(p.getTimeStamp())){
											targetpoint = p;
											break;
										}
									}

									double convertedpoint[]= convert(dep.getLon(), dep.getLat());

									bw.write(pid + " " + i
											+ "," + subtrip.getSex()
											+ "," + subtrip.getAge()
											+ "," + subtrip.getAddress()
											+ "," + subtrip.getWorkplace()
											+ "," + subtrip.getPurpose()
											+ "," + subtrip.getTransport()
											+ "," + convertedpoint[0]
											+ "," + convertedpoint[1]);
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

									STPoint targetpoint = null;
									for(STPoint p:r2) {
										if ( targetdate.equals(p.getTimeStamp())){
											targetpoint = p;
											break;
										}
									}

									double convertedpoint[]= convert(dep.getLon(), dep.getLat());

									bw.write(pid + " " + i
											+ "," + subtrip.getSex()
											+ "," + subtrip.getAge()
											+ "," + subtrip.getAddress()
											+ "," + subtrip.getWorkplace()
											+ "," + subtrip.getPurpose()
											+ "," + subtrip.getTransport()
											+ "," + convertedpoint[0]
											+ "," + convertedpoint[1]);
											bw.newLine();
								}
							}
						}
					}
				}

				con.close();

			}
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

	public static LonLat allocate(STPoint point , int purpose) {

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

