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

public class GetNodeofStation {

	public static void main(String args[]){

		File infile = new File ("c:/Users/yabec_000/Desktop/StationsinTokyo.csv");
		File outfile = new File ("c:/Users/yabec_000/Desktop/TokyoStationsNode.csv"); 

		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20141230TokyoSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();

		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));

			int counter = 1;
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Double lon = Double.parseDouble(tokens[2]);
				Double lat = Double.parseDouble(tokens[3]);

				LonLat now = new LonLat(lon, lat);

				String geom = getNode(con, now);

				bw.write(line + "," + geom);
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

	private static String generateSql(LonLat in, double Buffer) {
		// TODO BBOX size should be modifiable ////////////
		double minx  =  in.getLon()-Buffer;
		double miny  =  in.getLat()-Buffer;
		double maxx  =  in.getLon()+Buffer;
		double maxy  =  in.getLat()+Buffer;
		String point =  String.format("ST_SetSRID(ST_MakePoint(%f,%f),4326)",in.getLon(),in.getLat());
		String bbox  =  String.format("ST_SetSRID(ST_MakeBox2D(ST_MakePoint(%f,%f),ST_MakePoint(%f,%f)),4326) ",minx,miny,maxx,maxy);
		String sql   =  String.format("SELECT id, the_geom, ST_Distance_Sphere(the_geom,%s) as dist ",point) +
				String.format("FROM public.drm_node ") +
				String.format("WHERE ST_Intersects(the_geom,%s) ",bbox) +
				String.format("ORDER BY dist LIMIT 1;");
		return sql;
	}

	public static String getNode(Connection con, LonLat point) {
		Statement stmt = null;
		ResultSet res  = null;
		Integer node = null;
		String geom = null;
		try {
			con.setAutoCommit(true);
			stmt = con.createStatement();
			String sql = generateSql(point, Mesh.LAT_HEIGHT_MESH6.doubleValue());
			//			System.out.println(sql);	// for debug
			res        = stmt.executeQuery(sql);
			if( res.next() ) {
				node = res.getInt("id");
				geom = res.getString("the_geom");
				System.out.println("mesh 6");
			}
			else{
				res.close();
				String sql2 = generateSql(point,(Mesh.LAT_HEIGHT_MESH5.doubleValue()));
				res = stmt.executeQuery(sql2);
				if(res.next()){
					node = res.getInt("id");
					geom = res.getString("the_geom");
					System.out.println("mesh 5");
				}
				else{
					res.close();
					String sql3 = generateSql(point,(Mesh.LAT_HEIGHT_MESH2.doubleValue()/2));
					res = stmt.executeQuery(sql3);
					if(res.next()){
						node = res.getInt("id");
						System.out.println("mesh 2/2");
					}
					else{
						res.close();
						String sql5 = generateSql(point,(Mesh.LAT_HEIGHT_MESH2.doubleValue()*2));
						res = stmt.executeQuery(sql5);
						if(res.next()){
							node = res.getInt("id");
							System.out.println("mesh 2*2");
						}
						else{
							res.close();
							String sql4 = generateSql(point,(Mesh.LAT_HEIGHT_MESH1.doubleValue()*5));
							res = stmt.executeQuery(sql4);
							if(res.next()){
								node = res.getInt("id");
								System.out.println("mesh 1*5");
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
		return geom;
	}
}
