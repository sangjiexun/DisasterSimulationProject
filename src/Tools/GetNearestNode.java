package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

import com.beachstone.JWposChange;

public class GetNearestNode {

	public static void main(String args[]){
		File infile = new File ("c:/Users/yabec_000/Desktop/StationsforSimulation.csv");
		File outfile = new File ("c:/Users/yabec_000/Desktop/StationsforSimulation_nodes.csv"); 

		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20150113TokyoSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();

		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

			int counter = 1;
			int outside = 1;
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				
				/*for station nodes*/
//				Double lon = Double.parseDouble(tokens[1]);
//				Double lat = Double.parseDouble(tokens[2]);
//				LonLat point = new LonLat(lon,lat);
//				Integer node = getNearestNode(con, point);
//				System.out.println(node);
//				bw.write(node);
//				bw.newLine();
				
				/*original*/
				String pid = tokens[0];
				String id = tokens[1];
				String mf = tokens[2];
				String sex = tokens[3];
				String age = tokens[4];
				String purpose = tokens[7];
				String transport = tokens[8];
				Double nowlon = Double.parseDouble(tokens[9]);
				Double nowlat = Double.parseDouble(tokens[10]);
				Double homelon = Double.parseDouble(tokens[11]);
				Double homelat = Double.parseDouble(tokens[12]);
				Double goallon = Double.parseDouble(tokens[13]);
				Double goallat = Double.parseDouble(tokens[14]);

				LonLat now = new LonLat(nowlon, nowlat);
				LonLat home = new LonLat(homelon, homelat);
				LonLat goal = new LonLat(goallon, goallat);

				Mesh nowmesh = new Mesh(5,now.getLon(),now.getLat());
				Mesh homemesh = new Mesh(5,home.getLon(),home.getLat());
				Mesh goalmesh = new Mesh(5,goal.getLon(),goal.getLat());
				
				
				Integer nownode = getNodeInMesh(con, now, nowmesh);
				if (nownode == null){
					nownode = getNearestNode(con, now);
					System.out.println("outside: " + outside);
					outside++;
				}
				Integer homenode = getNodeInMesh(con, home, homemesh);
				if (homenode == null){
					homenode = getNearestNode(con, home);
				}
				Integer goalnode = getNodeInMesh(con, goal, goalmesh);
				if (goalnode == null){
					goalnode = getNearestNode(con, goal);
				}

				Double nowx = convert(nowlon, nowlat)[0];
				Double nowy = convert(nowlon, nowlat)[1];
				Double homex = convert(homelon, homelat)[0];
				Double homey = convert(homelon, homelat)[1];	
				Double goalx = convert(goallon, goallat)[0];
				Double goaly = convert(goallon, goallat)[1];

				bw.write(pid + "," + id + "," + mf + "," + sex + "," + age + "," +
						purpose + "," + transport + "," + nownode + "," + homenode + "," + goalnode + "," +
						now.getLon() + "," + now.getLat() + "," +nowx + "," + nowy + "," +homex + "," + homey + "," +goalx + "," +goaly);
//				bw.write(pid + "," + id + "," + mf + "," + sex + "," + age + "," +
//						purpose + "," + transport + "," + 
//						now.getLon() + "," + now.getLat() + "," +nowx + "," + nowy + "," +homex + "," + homey + "," +goalx + "," +goaly);
				bw.newLine();
				System.out.println("count: " + counter);
				counter++;
				
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}

	}

	private static String generateSqlInside(LonLat in, Mesh mesh) {
		// TODO BBOX size should be modifiable ////////////
		double minx  =  (mesh.getCenter().getLon()-Mesh.LNG_WIDTH_MESH5.doubleValue()/2);
		double miny  =  (mesh.getCenter().getLat()-Mesh.LAT_HEIGHT_MESH5.doubleValue()/2);
		double maxx  =  (mesh.getCenter().getLon()+Mesh.LNG_WIDTH_MESH5.doubleValue()/2);
		double maxy  =  (mesh.getCenter().getLat()+Mesh.LAT_HEIGHT_MESH5.doubleValue()/2);
		String point =  String.format("ST_SetSRID(ST_MakePoint(%f,%f),4326)",in.getLon(),in.getLat());
		String bbox  =  String.format("ST_SetSRID(ST_MakeBox2D(ST_MakePoint(%f,%f),ST_MakePoint(%f,%f)),4326) ",minx,miny,maxx,maxy);
		String sql   =  String.format("SELECT id, ST_Distance_Sphere(the_geom,%s) as dist ",point) +
				String.format("FROM public.drm_node ") +
				String.format("WHERE ST_Intersects(the_geom,%s) ",bbox) +
				String.format("ORDER BY dist LIMIT 1;");
		return sql;
	}
	
	public static Integer getNodeInMesh(Connection con, LonLat point, Mesh mesh) {
		Statement stmt = null;
		ResultSet res  = null;
		Integer node = null;
		try {
			con.setAutoCommit(true);
			stmt = con.createStatement();
			String sql = generateSqlInside(point, mesh);
			//			System.out.println(sql);	// for debug
			res        = stmt.executeQuery(sql);
			if( res.next() ) {
				node = res.getInt("id");
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
	
	private static String generateSqlOutside(LonLat in, double Buffer) {
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

	public static Integer getNearestNode(Connection con, LonLat point) {
		Statement stmt = null;
		ResultSet res  = null;
		Integer node = null;
		try {
			con.setAutoCommit(true);
			stmt = con.createStatement();
			String sql = generateSqlOutside(point, Mesh.LAT_HEIGHT_MESH6.doubleValue());
			//			System.out.println(sql);	// for debug
			res        = stmt.executeQuery(sql);
			if( res.next() ) {
				node = res.getInt("id");
			}
			else{
				res.close();
				String sql2 = generateSqlOutside(point,(Mesh.LAT_HEIGHT_MESH5.doubleValue()));
				res = stmt.executeQuery(sql2);
				if(res.next()){
					node = res.getInt("id");
				}
				else{
					res.close();
					String sql3 = generateSqlOutside(point,(Mesh.LAT_HEIGHT_MESH2.doubleValue()/2));
					res = stmt.executeQuery(sql3);
					if(res.next()){
						node = res.getInt("id");
					}
					else{
						res.close();
						String sql5 = generateSqlOutside(point,(Mesh.LAT_HEIGHT_MESH2.doubleValue()*2));
						res = stmt.executeQuery(sql5);
						if(res.next()){
							node = res.getInt("id");
							//System.out.println("mesh 2*2");
						}
						else{
							res.close();
							String sql4 = generateSqlOutside(point,(Mesh.LAT_HEIGHT_MESH1.doubleValue()*5));
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


	public static double[] convert(double lon,double lat) {
		JWposChange converter = new JWposChange(lat,lon,9);
		converter.LatLongtoXYW();
		return new double[]{converter.getX(),converter.getY()};
	}

}
