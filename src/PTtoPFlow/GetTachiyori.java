package PTtoPFlow;

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

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

public class GetTachiyori {

	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static void main(String args[]){ 

		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20150107FujisawaSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();

		File in = new File ("c:/users/yabec_000/Desktop/Fujisawa_PT_Final.csv");
		File out = new File ("c:/users/yabec_000/Desktop/Fujisawa_PT_Final2.csv");

		int counter = 1;

		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				String nownode = tokens[7];
				LonLat point = new LonLat(Double.parseDouble(tokens[10]),Double.parseDouble(tokens[11]));
				if(gchecker.checkOverlap(Double.parseDouble(tokens[10]),Double.parseDouble(tokens[11]))==true){
					LonLat tatemono = getTatemono(con,point);
					if (tatemono == null){
						tatemono = point;
					}
					//					System.out.println(tatemono);
					Integer tachiyori = getNodeOutside(con,tatemono);
					//					System.out.println(tachiyori);
					bw.write(line + "," + tachiyori);
					bw.newLine();
				}
				else{
					bw.write(line + "," + nownode);
					bw.newLine();
				}
				if(counter % 1000 == 0){
					System.out.println(counter);
				}
				counter++;
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}

	private static String generateSql(LonLat point, Double buffer) {
		// TODO BBOX size should be modifiable ////////////
		double minx  =  (point.getLon()-buffer);
		double miny  =  (point.getLat()-buffer);
		double maxx  =  (point.getLon()+buffer);
		double maxy  =  (point.getLat()+buffer);
		String bbox  =  String.format("ST_SetSRID(ST_MakeBox2D(ST_MakePoint(%f,%f),ST_MakePoint(%f,%f)),4326) ",minx,miny,maxx,maxy);
		String sql   =  String.format("SELECT gid, geom, b_ratio, ST_X(geom) as x, ST_Y(geom) as y ") +
				String.format("FROM public.reallocationtable ") +
				String.format("WHERE ST_Intersects(geom,%s) ",bbox) +
				String.format("AND b_ratio>0 ");
		String.format("ORDER BY RAND() LIMIT 1;");
		return sql;
	}

	public static LonLat getTatemono(Connection con, LonLat point) {
		Statement stmt = null;
		ResultSet res  = null;
		LonLat target = null;
		try {
			con.setAutoCommit(true);
			stmt = con.createStatement();
			String sql = generateSql(point,Mesh.LAT_HEIGHT_MESH5.doubleValue()*1.5);
			//			System.out.println(sql);	// for debug
			res        = stmt.executeQuery(sql);
			if( res.next() ) {
				Double lon = res.getDouble("x");
				Double lat = res.getDouble("y");
				target = new LonLat(lon,lat);
			}
			res.close();
		}
		catch(SQLException exp) { exp.printStackTrace(); }
		finally {
			try { if( stmt != null ) { stmt.close(); } }
			catch(SQLException exp) { exp.printStackTrace(); }
		}
		return target;
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

	public static Integer getNodeOutside(Connection con, LonLat point) {
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


}
