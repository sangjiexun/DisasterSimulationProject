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


public class PTnoMagfacNoRelocation {

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//change time format

	static File shapedir = new File("c:/users/yabetaka/Desktop/pt08tky.zoneshape"); 
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static void main(String[] args) throws Exception {
		DBCPLoader.initPgSQLConnection("postgres","task4TH","AllKanto"); // id,pw,dbname
		File ptoriginal = new File("c:/users/yabetaka/Desktop/chiyoda-shinjuku-bunkyo.txt"); 
		File newdir = new File("c:/users/yabetaka/Desktop/testKantoPT");
		newdir.mkdir();

		Map<String,Map<String,List<MidtermData>>> alldata = loadMidtermData(ptoriginal);
		System.out.println("loaded data!");

		List<String> pids = new ArrayList<String>(alldata.keySet());
		BufferedWriter bw = null;

		for (int i = 0; i<=23; i++){

			System.out.println("start hour: " + i );
			Integer counter = 0;
			Connection con = DBCPLoader.getPgSQLConnection();

			File outfile = new File ("c:/users/yabetaka/Desktop/testKantoPT/KantoPT_"+i+".csv");
			bw = new BufferedWriter(new FileWriter(outfile, true));
			bw.write("id" + "," + "MagFac" + "," + "time" + "," + "sex" + "," + 
					"age" + "," + "add Code" + "," + "goal code" + "," + "purpose" + "," +
					"transport" + "," + "Lon" + "," + "Lat" + "," + "zonecode");
			bw.newLine();

			for(String pid : pids) {
				Map<String,List<MidtermData>> trips    = alldata.get(pid);
				List<List<MidtermData>>       tripList = new ArrayList<List<MidtermData>>(trips.values());

				Integer id = Integer.valueOf(pid);
				int magfac;
				magfac = tripList.get(0).get(0).getExfactor1();

				for(List<MidtermData> subtrips : tripList) {
					for(MidtermData subtrip : subtrips) {
						STPoint dep = subtrip.getDepPoint();
						STPoint arr = subtrip.getArrPoint();

						String hour = String.format("%02d", i);
						Date targetdate = SDF_TS.parse("2008-10-01 "+ hour + ":00:00");

						if ((dep.getTimeStamp().before(targetdate)
								&&arr.getTimeStamp().after(targetdate))
								||dep.getTimeStamp().equals(targetdate)
								||arr.getTimeStamp().equals(targetdate)){

							String zonecode = null;
							List<String> zonecodeList = gchecker.listOverlaps("ZONECODE",dep.getLon(),dep.getLat());
							//						System.out.println(dep.getLon()+","+dep.getLat());
							if( zonecodeList == null || zonecodeList.isEmpty() ) 
							{zonecode = "0";}
							else{
								zonecode = zonecodeList.get(0);
							}

							//							LonLat point = targetPoint(zonecode,dep,arr,subtrip,con,targetdate); //MAIN PART
							LonLat point = new LonLat(0,0); // for test

							for(int j=1; j<=magfac;j++){
								bw.write(id 
										+ "," + magfac
										+ "," + targetdate
										+ "," + subtrip.getSex()
										+ "," + subtrip.getAge()
										+ "," + subtrip.getAddress()
										+ "," + subtrip.getGoalloc()
										+ "," + subtrip.getPurpose()
										+ "," + subtrip.getTransport()
										+ "," + point.getLon() 
										+ "," + point.getLat()
										+ "," + Integer.valueOf(zonecode));
								bw.newLine();
								counter++;
								if(counter % 100000 == 0){
									System.out.println(i + "," + counter);
								}
							}
						}
					}
				}		
			}
			con.close();
			bw.close();
		}

		DBCPLoader.closePgSQLConnection();
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

	public static LonLat targetPoint(String zonecode, LonLat dep, LonLat arr,MidtermData subtrip, Connection con, Date targetdate){

		PgRouting routing = new PgRouting(PgRouting.LOCALROAD_V2);
		PgRouting railwayrouting = new PgRouting(PgRouting.ALLROAD_V1);

		if (subtrip.isStay()){
			return dep;
		}

		else if (subtrip.useRailway()){
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
				}
			}
			return targetpoint;
		}

		else{
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
				}
			}
			return targetpoint;
		}
	}
}
