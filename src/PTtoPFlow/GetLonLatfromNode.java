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

//import com.beachstone.JWposChange;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

public class GetLonLatfromNode {

	public static void main(String args[]) throws SQLException{
		File newPT = new File("c:/users/yabec_000/desktop/Tokyo_PT_20150131.csv");
		File newPT2 = new File("c:/users/yabec_000/desktop/Tokyo_PT_20150201.csv");
		File zones = new File("c:/users/yabec_000/desktop/zonesin3wards.csv");

		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20150113TokyoSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(newPT));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newPT2));
			String line = null;
			int counter = 1;
			while((line=br.readLine()) != null){
				String[] t = line.split(",");
				LonLat now = new LonLat(Double.parseDouble(t[7]),Double.parseDouble(t[8]));
//				String node = t[9];
				Integer zonecode = Integer.valueOf(t[13]);
//				if(node.equals("null")){
//					node = String.valueOf(PT_20150130.getNowNode(zonecode,zones,now));
//				}
//				LonLat point = getLonLatfromNode(con,node);
				
				Integer nownode = PT_20150130.getNowNode(zonecode,zones,now);
				LonLat nowpoint = PT_20150130.getNowPoint(zonecode,zones,now);
//				double nowxy[] = /*convert(now.getLon(),now.getLat());*/ ;
				Double nowx = /*nowxy[0];*/ 0d;
				Double nowy = /*nowxy[1];*/ 0d;
				
				bw.write(line + "," + nowpoint.getLon() + "," + nowpoint.getLat() + "," + nownode
						 + "," + nowx + "," + nowy);
				bw.newLine();
				System.out.println(counter);
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


	private static String generateSql(String node) {
		String sql   =  String.format("SELECT id, st_x(the_geom) as lon, st_y(the_geom) as lat ") +
				String.format("FROM public.drm_node ") +
				String.format("where id =%s;", node);
		return sql;
	}

	public static LonLat getLonLatfromNode(Connection con, String node) {
		Statement stmt = null;
		ResultSet res  = null;
		LonLat point = null;
		try {
			con.setAutoCommit(true);
			stmt = con.createStatement();
			String sql = generateSql(node);
			res        = stmt.executeQuery(sql);
			if( res.next() ) {
				Double lon = Double.parseDouble(res.getString("lon"));
				Double lat = Double.parseDouble(res.getString("lat"));
				point = new LonLat(lon,lat);
			}
			res.close();
		}
		catch(SQLException exp) { exp.printStackTrace(); }
		finally {
			try { if( stmt != null ) { stmt.close(); } }
			catch(SQLException exp) { exp.printStackTrace(); }
		}
		return point;
	}


//	public static double[] convert(double lon,double lat) {
//		JWposChange converter = new JWposChange(lat,lon,9);
//		converter.LatLongtoXYW();
//		return new double[]{converter.getX(),converter.getY()};
//	}

	
}

