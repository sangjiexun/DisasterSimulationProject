//package VRISimulation;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.ArrayList;
//
//import jp.ac.ut.csis.pflow.geom.LonLat;
//import jp.ac.ut.csis.pflow.mapmatching.MMScore;
//import jp.ac.ut.csis.pflow.mapmatching.PgMapMatching;
//import jp.ac.ut.csis.pflow.tools.DBCPLoader;
//
//import com.beachstone.JWposChange;
//
//public class Gen_Point_Maker {
//
//	public static LonLat xy2lonlat(double x,double y) {
//		JWposChange converter = new JWposChange(x,y,9);
//		converter.XYtoLatLongJ();
//		return new LonLat(converter.getY(), converter.getX());
//	}
//
//	public static void main(String[] args) throws SQLException{
//		File infile = new File ("C:/Users/yabec_000/Desktop/Fujisawa_PT_animation.csv");
//		File outfile= new File ("C:/Users/yabec_000/Desktop/Fujisawa_anime_genpoint.csv");
//
//		DBCPLoader.initPgSQLConnection(
//				"localhost",
//				5432,
//				"postgres",
//				"Taka0505",
//				"pflowdrm",
//				"UTF8");
//		int num = 0;
//		long time0 = System.currentTimeMillis();
//		int error = 0;
//		System.out.println("yeah");
//		ArrayList<String> errors = new ArrayList<String>();
//
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(infile));
//			BufferedWriter bw = new BufferedWriter (new FileWriter(outfile));
//			String line = null;
//			Connection con = DBCPLoader.getPgSQLConnection();
//			while ( (line = br.readLine()) != null){
//
//				String[] tokens = line.split(",");
//				String PID = tokens[0];
//				Double locx = Double.parseDouble(tokens[8]);
//				Double locy = Double.parseDouble(tokens[7]);
//				//(x,y) -> (Lon,Lat)
//				LonLat point = xy2lonlat(locx, locy);
//				System.out.println(point);
//
//				PgMapMatching matching = new PgMapMatching();
//				MMScore score = matching.runMatching(con,point);
//				if (score.equals(null)){
//					String linkID = "0";
//					double linkx = 0;
//					double linky = 0;
//					bw.write(PID + "," + locx +","+ "0"+  "," + locy +","+ "-1" +","+ linkID +","+ linkx + "," +"0"+ "," + linky );
//					error = error + 1;
//					System.out.println(num);
//					errors.add(PID);
//				}
//				else{
//					String linkID = score.getLinkId();
//					//				System.out.println(linkID);
//					LonLat linkpoint  = score.getOutputPoint();
//					double linklon = linkpoint.getLon();
//					double linklat = linkpoint.getLat();
//					//convert lonlat --> xy
//					double link[]= convert(linklon, linklat);
//					bw.write(PID + "," + locx +","+ "0"+  "," + locy +","+ "-1" +","+ linkID +","+ link[0] + "," +"0"+ "," + link[1] );
//				}
//
//				bw.newLine();
//				num = num + 1;
//				System.out.println(num + "," + error);
//			}
//			con.close();
//			DBCPLoader.closePgSQLConnection();
//			br.close();
//			bw.close();
//
//			long time1 = System.currentTimeMillis();
//			System.out.println((time1-time0)/1000);
//			System.out.println(errors);
//		}
//		catch(FileNotFoundException a) {
//			System.out.println("File not found");
//		}
//		catch(IOException e) {
//			System.out.println(e);
//		}
//	}
//	public static double[] convert(double lon,double lat) {
//		JWposChange converter = new JWposChange(lat,lon,9);
//		converter.LatLongtoXYW();
//		return new double[]{converter.getX(),converter.getY()};
//	}
//}