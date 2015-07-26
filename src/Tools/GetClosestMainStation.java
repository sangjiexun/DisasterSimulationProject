package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class GetClosestMainStation {

	public static void main(String args[]){
		File nodes = new File("C:/users/yabec_000/Desktop/allnodes.csv");
		File out   = new File("C:/users/yabec_000/Desktop/nodes_stationnode_final.csv");

		try{
			int counter = 1;
			int inside = 1;
			BufferedReader br = new BufferedReader(new FileReader(nodes));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = br.readLine();
			Integer stationnode = null;

			while((line = br.readLine())!=null){
				String[] tokens = line.split(";");
				Integer node0 = Integer.valueOf(tokens[0]);
				Integer node1 = Integer.valueOf(tokens[1]);
				stationnode = getfromMap(node0);
				if(stationnode == 0){
					bw.write(node0 + "," + node1);
					bw.newLine();
					//					System.out.println("outside: " + outside);
					//					outside++;
				}
				else{
					bw.write(node0 + "," + stationnode);
					bw.newLine();
					if(inside % 1000 == 0){
						System.out.println("inside: " + inside);
					}
					inside++;
				}
				if(counter%10000 == 0){
					System.out.println(counter);
				}
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

	public static Integer getfromMap(Integer node){
		File infile = new File("C:/users/yabec_000/Desktop/nodes_stationnode.csv");
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		Integer nodee = null;
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String line = null;
			while((line = br.readLine())!=null){
				String[] tokens = line.split(",");
				Integer node0 = Integer.valueOf(tokens[0]);
				Integer node1 = Integer.valueOf(tokens[1]);
				map.put(node0, node1);
			}
			br.close();

			if(map.containsKey(node)){

				nodee = map.get(node);
			}
			else{
				nodee = 0;
			}
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return nodee;
	}



//			DBCPLoader.initPgSQLConnection(
//					"localhost",
//					5432,
//					"postgres",
//					"Taka0505",
//					"20150113TokyoSimulation",
//					"UTF8");
//			Connection con = DBCPLoader.getPgSQLConnection();
//			
//			try{
//				int counter = 1;
//				int inside = 1;
//				int outside = 1;
//				BufferedReader br = new BufferedReader(new FileReader(nodes));
//				BufferedWriter bw = new BufferedWriter(new FileWriter(out));
//				String line = br.readLine();
//				Integer stationnode = null;
//				
//				while((line = br.readLine())!=null){
//					String[] tokens = line.split(";");
//					LonLat point = new LonLat(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
//					if(gchecker.checkOverlap(point.getLon(), point.getLat())==true){
//						stationnode = getNearestNode(con,point);
//						inside++;
//						bw.write(tokens[0] + "," + stationnode);
//						bw.newLine();
//					}
//					else{
//						stationnode = 0;
//	//					stationnode = getfromMain(con,Integer.valueOf(tokens[0]));
//	//					outside++;
//					}
//					if(counter % 1000 == 0){
//						System.out.println("all: " + counter);
//					}
//					if(inside % 1000 == 0){
//						System.out.println("inside: " + inside);
//					}
//					counter++;
//				}
//				br.close();
//				bw.close();
//			}
//			catch(FileNotFoundException xx) {
//				System.out.println("File not found 1");
//			}
//			catch(IOException xxx) {
//				System.out.println(xxx);
//			}		
//	}
//
//	
//		static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
//		static GeometryChecker gchecker = new GeometryChecker(shapedir);
//	
//		private static String generateSql(LonLat in, double Buffer) {
//			// TODO BBOX size should be modifiable  ///////
//			double minx  =  in.getLon()-Buffer;
//			double miny  =  in.getLat()-Buffer;
//			double maxx  =  in.getLon()+Buffer;
//			double maxy  =  in.getLat()+Buffer;
//			String point =  String.format("ST_SetSRID(ST_MakePoint(%f,%f),4326)",in.getLon(),in.getLat());
//			String bbox  =  String.format("ST_SetSRID(ST_MakeBox2D(ST_MakePoint(%f,%f),ST_MakePoint(%f,%f)),4326) ",minx,miny,maxx,maxy);
//			String sql   =  String.format("SELECT id, ST_Distance_Sphere(the_geom,%s) as dist ",point) +
//					String.format("FROM public.mainstations ") +
//					String.format("WHERE ST_Intersects(the_geom,%s) ",bbox) +
//					String.format("ORDER BY dist LIMIT 1;");
//			return sql;
//		}
//
//		public static Integer getNearestNode(Connection con, LonLat point) {
//			Statement stmt = null;
//			ResultSet res  = null;
//			Integer node = null;
//			try {
//				con.setAutoCommit(true);
//				stmt = con.createStatement();
//				String sql = generateSql(point, Mesh.LAT_HEIGHT_MESH6.doubleValue());
//				//			System.out.println(sql);	// for debug
//				res        = stmt.executeQuery(sql);
//				if( res.next() ) {
//					node = res.getInt("id");
//				}
//				else{
//					res.close();
//					String sql2 = generateSql(point,(Mesh.LAT_HEIGHT_MESH5.doubleValue()));
//					res = stmt.executeQuery(sql2);
//					if(res.next()){
//						node = res.getInt("id");
//					}
//					else{
//						res.close();
//						String sql3 = generateSql(point,(Mesh.LAT_HEIGHT_MESH2.doubleValue()/2));
//						res = stmt.executeQuery(sql3);
//						if(res.next()){
//							node = res.getInt("id");
//						}
//						else{
//							res.close();
//							String sql5 = generateSql(point,(Mesh.LAT_HEIGHT_MESH2.doubleValue()*2));
//							res = stmt.executeQuery(sql5);
//							if(res.next()){
//								node = res.getInt("id");
//								//System.out.println("mesh 2*2");
//							}
//							else{
//								res.close();
//								String sql4 = generateSql(point,(Mesh.LAT_HEIGHT_MESH1.doubleValue()*5));
//								res = stmt.executeQuery(sql4);
//								if(res.next()){
//									node = res.getInt("id");
//									//System.out.println("mesh 1*5");
//								}
//							}
//						}
//					}
//				}
//				res.close();
//			}
//			catch(SQLException exp) { exp.printStackTrace(); }
//			finally {
//				try { if( stmt != null ) { stmt.close(); } }
//				catch(SQLException exp) { exp.printStackTrace(); }
//			}
//			return node;
//		}
//
//		private static String generateSql2(Integer node) {
//			String sql   =  String.format("SELECT id0, id1 ") +
//					String.format("FROM public.node_station_map ") +
//					String.format("WHERE id0=%s ;",node);
//			return sql;
//		}
//
//		public static Integer getfromMain(Connection con, Integer node0){
//			Statement stmt = null;
//			ResultSet res  = null;
//			Integer node = null;
//			try {
//				con.setAutoCommit(true);
//				stmt = con.createStatement();
//				String sql = generateSql2(node0);
//				//			System.out.println(sql);	// for debug
//				res        = stmt.executeQuery(sql);
//				if( res.next() ) {
//					node = res.getInt("id1");
//				}
//				res.close();
//			}
//			catch(SQLException exp) { exp.printStackTrace(); }
//			finally {
//				try { if( stmt != null ) { stmt.close(); } }
//				catch(SQLException exp) { exp.printStackTrace(); }
//			}
//			return node;
//		}

}
