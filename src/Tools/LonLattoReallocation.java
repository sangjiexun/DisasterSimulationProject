package Tools;

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

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;
import PTtoPFlow.PTver5;

public class LonLattoReallocation {

	public static void main(String args[]) throws SQLException{
		File in = new File ("c:/users/yabec_000/Desktop/id-node.csv");
		File out = new File ("c:/users/yabec_000/Desktop/id-newnode.csv");

		int counter = 1;

		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			while((line= br.readLine()) != null){
				String[] ts = line.split(",");
				String id = ts[0];
				Double lon = Double.parseDouble(ts[1]);
				Double lat = Double.parseDouble(ts[2]);
				LonLat point = new LonLat(lon,lat);
				Integer node = LonLat2Realloc(point);
				bw.write(id + "," + node);
				bw.newLine();
				if(counter % 500 == 0){
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


	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static Integer LonLat2Realloc(LonLat point) throws SQLException{

		File zones = new File("c:/users/yabec_000/desktop/zonesin3wards.csv");
		Integer node = null;
		Mesh mesh = new Mesh(5,point.getLon(),point.getLat());
		Integer zonecode = null;
		List<String> zonecodeList = gchecker.listOverlaps("zonecode",mesh.getCenter().getLon(),mesh.getCenter().getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {
			return node;
		}
		else{
			zonecode = Integer.valueOf(zonecodeList.get(0));
			node = getNowNode(zonecode,zones,mesh.getCenter());
			return node;
		}

	}

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
			return nownode;
		}
		else{	
			Integer nownode = GetNearestNode.getNearestNode(con, point);
			con.close();
			return nownode;
		}
	}


}
