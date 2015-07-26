package PTtoPFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;


public class GetMajorTransportation {

	public static void main(String[] args) throws Exception {
		
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","AllKanto"); // id,pw,dbname
		Connection con = DBCPLoader.getPgSQLConnection();
		File ptoriginal = new File("C:/Users/yabec_000/Desktop/2008-tokyo-pt-midterm-data/result.txt");
		File outfile = new File ("c:/Users/yabec_000/Desktop/AllPT-mode-address.txt");

		Map<String,Map<String,List<MidtermData>>> alldata = loadMidtermData(ptoriginal);	// read all data
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

		List<String> pids = new ArrayList<String>(alldata.keySet());	// all pids
		Integer counter = 0;
		int mode = 0;

		for(String pid : pids) {
			Map<String,List<MidtermData>> trips    = alldata.get(pid);
			List<List<MidtermData>>       tripList = new ArrayList<List<MidtermData>>(trips.values());
			HashSet<Integer> trans = new HashSet<Integer>();

			String home = null;
			Integer id = Integer.valueOf(pid);
					
			for(List<MidtermData> subtrips : tripList) {
				for(MidtermData subtrip : subtrips) {
					//					if(subtrip.getPurpose()==1)
					Integer transport = subtrip.getTransport();
					trans.add(transport);
					home = String.format("%05d", subtrip.getAddress());
//					System.out.println(home);
				}
			}
			if(trans.contains(12)){
				mode = 12;
			}
			else if(trans.contains(11)){
				mode = 11;
			}
			else if(trans.contains(10)){
				mode = 10;
			}
			else if(trans.contains(9)){
				mode = 9;
			}
			else if(trans.contains(6)){
				mode = 6;
			}
			else if(trans.contains(7)){
				mode = 7;
			}
			else if(trans.contains(5)){
				mode = 5;
			}
			else if(trans.contains(4)){
				mode = 4;
			}
			else if(trans.contains(3)){
				mode = 3;
			}
			else if(trans.contains(2)){
				mode = 2;
			}
			else {
				mode = 1;
			}
			
			LonLat homepoint = allocator.allocate(home, 3);
			getNode(con,homepoint);
			
			bw.write(id + "," + mode + "," + homepoint.getLon() + "," + homepoint.getLat());
			bw.newLine();
			
			counter++;
			System.out.println(counter);
		}
		bw.close();
		con.close();	
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
	
	private static String generateSql(LonLat in, double Buffer) {
		// TODO BBOX size should be modifiable  ///////
		double minx  =  in.getLon()-Buffer;
		double miny  =  in.getLat()-Buffer;
		double maxx  =  in.getLon()+Buffer;
		double maxy  =  in.getLat()+Buffer;
		String point =  String.format("ST_SetSRID(ST_MakePoint(%f,%f),4326)",in.getLon(),in.getLat());
		String bbox  =  String.format("ST_SetSRID(ST_MakeBox2D(ST_MakePoint(%f,%f),ST_MakePoint(%f,%f)),4326) ",minx,miny,maxx,maxy);
		String sql   =  String.format("SELECT id, ST_Distance_Sphere(the_geom,%s) as dist ",point) +
				String.format("FROM public.drm_node ") +
				String.format("WHERE ST_Intersects(the_geom,%s) ",bbox) +
				String.format("ORDER BY dist LIMIT 1;");
		return sql;
	}

	public static Integer getNode(Connection con, LonLat point) {
		Statement stmt = null;
		ResultSet res  = null;
		Integer node = null;
		try {
			con.setAutoCommit(true);
			stmt = con.createStatement();
			String sql = generateSql(point, Mesh.LAT_HEIGHT_MESH6.doubleValue());
			//			System.out.println(sql);	// for debug
			res        = stmt.executeQuery(sql);
			if( res.next() ) {
				node = res.getInt("id");
			}
			else{
				res.close();
				String sql2 = generateSql(point,(Mesh.LAT_HEIGHT_MESH5.doubleValue()));
				res = stmt.executeQuery(sql2);
				if(res.next()){
					node = res.getInt("id");
				}
				else{
					res.close();
					String sql3 = generateSql(point,(Mesh.LAT_HEIGHT_MESH2.doubleValue()/2));
					res = stmt.executeQuery(sql3);
					if(res.next()){
						node = res.getInt("id");
					}
					else{
						res.close();
						String sql5 = generateSql(point,(Mesh.LAT_HEIGHT_MESH2.doubleValue()*2));
						res = stmt.executeQuery(sql5);
						if(res.next()){
							node = res.getInt("id");
							//System.out.println("mesh 2*2");
						}
						else{
							res.close();
							String sql4 = generateSql(point,(Mesh.LAT_HEIGHT_MESH1.doubleValue()*5));
							res = stmt.executeQuery(sql4);
							if(res.next()){
								node = res.getInt("id");
								//System.out.println("mesh 1*5");
							}
						}
					}
				}
			}
			res.close();
		}
		catch(SQLException exp) { exp.printStackTrace(); }
		finally {
			try { if( stmt != null ) { stmt.close(); } }
			catch(SQLException exp) { exp.printStackTrace(); }
		}
		return node;
	}
	
}
