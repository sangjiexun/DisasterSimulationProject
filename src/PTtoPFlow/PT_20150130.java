package PTtoPFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.beachstone.JWposChange;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;
import Tools.GetNearestNode;
import Tools.NewPTAllocation;

/* 
 * Written by T.Yabe 2015/01/30
 * from NoMagfac to Perfect PT data
 */

public class PT_20150130 {

	public static void main(String args[]) throws SQLException{

//		File nomf = new File("c:/users/yabec_000/desktop/Fujisawa_noMagFac_Allinfo.csv");
//		File out  = new File("c:/users/yabec_000/desktop/Fujisawa_noMagFac_rightzone.csv");
//		getMeshZone(nomf,out);

		//集計して、（zone-mf2のファイルを作成する）...もうmfを変えてしまう。

		File notyetmf = new File("c:/users/yabec_000/desktop/Fujisawa_noMagFac_rightzone.csv"); 
		File zones = new File("c:/users/yabec_000/desktop/Fujisawa_Zones.csv");
		File newPT = new File("c:/users/yabec_000/desktop/Fujisawa_PT_2015.csv");
		
		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20150107FujisawaSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(notyetmf));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newPT));
			String line = br.readLine();
			
			int counter = 1;
			while((line=br.readLine()) != null){
				String[] t = line.split(",");
				String pid = t[0];
				Integer mf = Integer.valueOf(t[1]);
				String sex = t[2];
				String age = t[3];
				Integer address = Integer.valueOf(t[4]);
				Integer goalzone = Integer.valueOf(t[5]);
				String purpose = t[6];
				String trans = t[7];
				LonLat now = new LonLat(Double.parseDouble(t[8]),Double.parseDouble(t[9]));
				Integer zonecode = Integer.valueOf(t[10]);
				
				for (int i=1; i<=mf; i++){
					if(counter % 8 == 0){
						Integer nownode = getNowNode(zonecode,zones,now);
						LonLat nowpoint = getNowPoint(zonecode,zones,now);
						double nowxy[] = convert(now.getLon(),now.getLat());
						Double nowx = nowxy[0];
						Double nowy = nowxy[1];
						
						LonLat homepoint    = getHomeNode(address,3,zones);
						double homexy[] = convert(homepoint.getLon(),homepoint.getLat());
						Double homex = homexy[0];
						Double homey = homexy[1];
						Integer homenode = GetNearestNode.getNearestNode(con, homepoint);
						
						Integer goal    = getGoalNode(goalzone,1,zones);
						Integer shop    = getShopNode(now);
						
						bw.write(counter + "," + pid + "," + mf + "," + sex + "," + age + "," + 
						purpose + "," + trans + "," + nowpoint.getLon() + "," + nowpoint.getLat() + "," + 
						nownode + "," + homenode + "," + goal + "," + shop + "," + zonecode
						 + "," + nowx + "," + nowy + "," + homex + "," + homey);
						bw.newLine();
						System.out.println(counter);
					}
					counter++;
				}
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

	static File shapedir = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/zone_bounds_fujisawa_shape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	// get meshcode from nowpoint, then zonecode from meshcenter
	public static File getMeshZone(File nomf, File out){
		try{
			BufferedReader br = new BufferedReader(new FileReader(nomf));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = br.readLine();
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Double lon = Double.parseDouble(tokens[8]);
				Double lat = Double.parseDouble(tokens[9]);
				Mesh mesh = new Mesh(5,lon,lat);

				Integer zonecode = null;
				List<String> zonecodeList = 
						gchecker.listOverlaps("zonecode",mesh.getCenter().getLon(),mesh.getCenter().getLat());
				if( zonecodeList == null || zonecodeList.isEmpty() ) 
				{zonecode = 0;}
				else{
					zonecode = Integer.valueOf(zonecodeList.get(0));
				}
				bw.write(tokens[0] 				//id
						+ "," + tokens[1]		//mf
						+ "," + tokens[2]		//sex		
						+ "," + tokens[3]		//age
						+ "," + tokens[4]		//address
						+ "," + tokens[5]		//goal
																+ "," + tokens[6]		//purpose
																		+ "," + tokens[7]		//transport
																				+ "," + tokens[8]		//lon
																						+ "," + tokens[9]		//lat	
																								+ "," + zonecode);
				bw.newLine();
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
		return out;
	}

	//get meshcode from zone, and then allocate within mesh to point
	public static Integer getNowNode(Integer zonecode, File zones, LonLat point) throws SQLException{
		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20150113TokyoSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();
		
		if(PTver5.getZones(zones).contains(zonecode)){
			String zc = String.valueOf(zonecode);
//			System.out.println(zc);
			String meshcode = NewPTAllocation.getMeshcode(zc);
//			System.out.println(meshcode);
			Mesh mesh = new Mesh(meshcode);
			LonLat nowpoint = NewPTAllocation.allocateWithinMesh(meshcode, 3);
			if(nowpoint == null){
				nowpoint = mesh.getCenter();
				System.out.println("couldnt allocate in mesh");
			}
			Integer nownode = GetNearestNode.getNodeInMesh(con, nowpoint, mesh);
			if(nownode==null){
				nownode = GetNearestNode.getNearestNode(con, nowpoint);
				System.out.println("no node in mesh");
			}
			con.close();
//			System.out.println("1.1 nownode yes");
			return nownode;
		}
		else{	
			Integer nownode = GetNearestNode.getNearestNode(con, point);
//			System.out.println("1.2 nownode no");
			con.close();
			return nownode;
		}
	}
	
	public static LonLat getNowPoint(Integer zonecode, File zones, LonLat point) throws SQLException{
		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20150113TokyoSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();
		
		if(PTver5.getZones(zones).contains(zonecode)){
			String zc = String.valueOf(zonecode);
//			System.out.println(zc);
			String meshcode = NewPTAllocation.getMeshcode(zc);
//			System.out.println(meshcode);
			Mesh mesh = new Mesh(meshcode);
			LonLat nowpoint = mesh.getCenter();
			con.close();
//			System.out.println("1.1 nownode yes");
			return nowpoint;
		}
		else{	
//			System.out.println("1.2 nownode no");
			return point;
		}
	}
	
	
	//for Allocation...goes to PT_Locations.java
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
	
	//get homepoint by allocation and nearest node
	public static LonLat getHomeNode(Integer zonecode, int purpose, File zones){	
		LonLat point;
		if(PTver5.getZones(zones).contains(zonecode)){
			String newzc = String.format("%05d", zonecode);
//			System.out.println("zc " + zc);
			point    = allocator.allocate(newzc, purpose);
//			System.out.println("3.1: " + point);
			return point;
		}
		else{	
			LonLat point1 = PTZoneUtils.getZoneRepresentative(zonecode);
//			System.out.println("3.2: " + point1);
			if(point1 == null){
				System.out.println("3.3: no zonecode in table for home/goal alloc. of " + zonecode);
				return null;
			}
			else{
//				System.out.println("3.4: " + point1);
				return point1;
			}
		}
	}
	
	//get goalpoint by allocation and nearest node
	public static Integer getGoalNode(Integer zonecode, int purpose, File zones) throws SQLException{
		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20150107FujisawaSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();
		
		LonLat point;
		if(PTver5.getZones(zones).contains(zonecode)){
			String newzc = String.format("%05d", zonecode);
//			System.out.println("zc " + zc);
			point    = allocator.allocate(newzc, purpose);
//			System.out.println("3.1: " + point);
			Integer node = GetNearestNode.getNearestNode(con, point);
			con.close();
			return node;
		}
		else{	
			LonLat point1 = PTZoneUtils.getZoneRepresentative(zonecode);
//			System.out.println("3.2: " + point1);
			if(point1 == null){
				System.out.println("3.3: no zonecode in table for home/goal alloc. of " + zonecode);
				con.close();
				return null;
			}
			else{
//				System.out.println("3.4: " + point1);
				Integer node = GetNearestNode.getNearestNode(con, point1);
				con.close();
				return node;
			}
		}
	}

	//get shoppoint
	public static Integer getShopNode(LonLat nowpoint) throws SQLException{
		DBCPLoader.initPgSQLConnection(
				"localhost",
				5432,
				"postgres",
				"Taka0505",
				"20150107FujisawaSimulation",
				"UTF8");
		Connection con = DBCPLoader.getPgSQLConnection();

		List<String> zonecodeList = gchecker.listOverlaps("zonecode", nowpoint.getLon(), nowpoint.getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {
			LonLat tachiyori = GetTachiyori.getTatemono(con,nowpoint);
			if (tachiyori == null){
				tachiyori = nowpoint;
			}
			Integer tachi = GetTachiyori.getNodeOutside(con,tachiyori);
			con.close();
			return tachi;
		}
		else{
			LonLat tatemono = GetTachiyori.getTatemono(con,nowpoint);
			Integer tachiyori = GetNearestNode.getNearestNode(con,tatemono);
			con.close();
			return tachiyori;
		}	
	}
	
	public static double[] convert(double lon,double lat) {
		JWposChange converter = new JWposChange(lat,lon,9);
		converter.LatLongtoXYW();
		return new double[]{converter.getX(),converter.getY()};
	}

}
