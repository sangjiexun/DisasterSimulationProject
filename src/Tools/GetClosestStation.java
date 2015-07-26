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
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

public class GetClosestStation {

	public static void main(String args[]){
		File infile = new File ("c:/Users/yabec_000/Desktop/Fujisawa_PT_Final_woStation.csv");
		File outfile = new File ("c:/Users/yabec_000/Desktop/Fujisawa_PT_Final.csv"); 

		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
			
			DBCPLoader.initPgSQLConnection(
					"localhost",
					5432,
					"postgres",
					"Taka0505",
					"20150107FujisawaSimulation",
					"UTF8");
			Connection con = DBCPLoader.getPgSQLConnection();

			int counter = 1;
			int inside = 1;
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Double lon = Double.parseDouble(tokens[10]);
				Double lat = Double.parseDouble(tokens[11]);
				LonLat point = new LonLat(lon,lat);

				List<String> zonecodeList = gchecker.listOverlaps("zonecode", lon, lat);
				if( zonecodeList == null || zonecodeList.isEmpty() ) {
					counter++ ; 
					bw.write(line + "," + "0");
					bw.newLine();
				}
				else{
					Integer node = getNode(con,point);
					bw.write(line + "," + node);
					bw.newLine();
					inside++;
					counter++;
				}	

				System.out.println(inside + "/" + counter);
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

	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	//static File shapedir = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/zone_bounds_fujisawa_shape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	private static String generateSql(LonLat in, double Buffer) {
		// TODO BBOX size should be modifiable ////////////
		double minx  =  in.getLon()-Buffer;
		double miny  =  in.getLat()-Buffer;
		double maxx  =  in.getLon()+Buffer;
		double maxy  =  in.getLat()+Buffer;
		String point =  String.format("ST_SetSRID(ST_MakePoint(%f,%f),4326)",in.getLon(),in.getLat());
		String bbox  =  String.format("ST_SetSRID(ST_MakeBox2D(ST_MakePoint(%f,%f),ST_MakePoint(%f,%f)),4326) ",minx,miny,maxx,maxy);
		String sql   =  String.format("SELECT id, the_geom, ST_Distance_Sphere(the_geom,%s) as dist ",point) +
				String.format("FROM public.tokyostations ") +
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
						}
						else{
							res.close();
							String sql4 = generateSql(point,(Mesh.LAT_HEIGHT_MESH1.doubleValue()*5));
							res = stmt.executeQuery(sql4);
							if(res.next()){
								node = res.getInt("id");
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
