package PTtoPFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;
import Tools.NewPTAllocation;

public class PT_changeNowpoint {

	public static void main(String args[]){
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname

		File oldpt = new File("c:/users/yabec_000/Desktop/pflowTokyo.csv");
		File newpt = new File("c:/users/yabec_000/Desktop/pflowTokyoNEW.csv");
		File zones = new File("c:/users/yabec_000/Desktop/zonesin3wards.csv");
		File mf2   = new File("c:/users/yabec_000/Desktop/zone_mf2.csv");

		try{
			BufferedReader br = new BufferedReader(new FileReader(oldpt));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newpt));
			String line = br.readLine();
			int counter = 1;
			while((line = br.readLine()) != null){
				String[] tokens = line.split(";");
				Double lon = Double.parseDouble(tokens[10]);
				Double lat = Double.parseDouble(tokens[11]);
				LonLat point = new LonLat(lon,lat);
				LonLat allopoint = null;
				double num = Math.random();

				List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
				Integer zonecode;
				if( zonecodeList == null || zonecodeList.isEmpty() ){
					zonecode = 0;
				}
				else{
					zonecode = Integer.valueOf(zonecodeList.get(0));
				}
				//				System.out.println(zonecode);

				if(getZones(zones).contains(zonecode)) {

					double dig = getmf2(mf2,zonecode);

					if (dig < 0 && num<Math.abs(dig)){
						continue;
					}

					else if (dig > 0 && num<Math.abs(dig)){
						allopoint = insideAllocation(String.valueOf(zonecode), point);
						bw.write(tokens[0] + "," +tokens[1] + "," +
								tokens[2] + "," +tokens[3] + "," +tokens[4] + "," + 
								tokens[5] + "," +tokens[6] + "," +tokens[7] + "," +
								tokens[8] + "," +tokens[9] + "," + allopoint.getLon() + "," + allopoint.getLat() + "," +
								tokens[12] + "," +tokens[13] + "," +tokens[14] + "," +tokens[15] + "," +tokens[16]
										+ "," +tokens[17] + "," +tokens[18] + "," +tokens[19] + "," +tokens[20]);
						bw.newLine();
						allopoint = insideAllocation(String.valueOf(zonecode), point);
						bw.write(tokens[0] + "," +tokens[1] + "," +
								tokens[2] + "," +tokens[3] + "," +tokens[4] + "," + 
								tokens[5] + "," +tokens[6] + "," +tokens[7] + "," +
								tokens[8] + "," +tokens[9] + "," + allopoint.getLon() + "," + allopoint.getLat() + "," +
								tokens[12] + "," +tokens[13] + "," +tokens[14] + "," +tokens[15] + "," +tokens[16]
										+ "," +tokens[17] + "," +tokens[18] + "," +tokens[19] + "," +tokens[20]);
						bw.newLine();
					}
					else {
						allopoint = insideAllocation(String.valueOf(zonecode), point);
						bw.write(tokens[0] + "," +tokens[1] + "," +
								tokens[2] + "," +tokens[3] + "," +tokens[4] + "," + 
								tokens[5] + "," +tokens[6] + "," +tokens[7] + "," +
								tokens[8] + "," +tokens[9] + "," + allopoint.getLon() + "," + allopoint.getLat() + "," +
								tokens[12] + "," +tokens[13] + "," +tokens[14] + "," +tokens[15] + "," +tokens[16]
										+ "," +tokens[17] + "," +tokens[18] + "," +tokens[19] + "," +tokens[20]);
						bw.newLine();
					}
				}

				else{
					allopoint = outsideAllocation(point);
					bw.write(tokens[0] + "," +tokens[1] + "," +
							tokens[2] + "," +tokens[3] + "," +tokens[4] + "," + 
							tokens[5] + "," +tokens[6] + "," +tokens[7] + "," +
							tokens[8] + "," +tokens[9] + "," + allopoint.getLon() + "," + allopoint.getLat() + "," +
							tokens[12] + "," +tokens[13] + "," +tokens[14] + "," +tokens[15] + "," +tokens[16]
									+ "," +tokens[17] + "," +tokens[18] + "," +tokens[19] + "," +tokens[20]);
					bw.newLine();
				}
				counter++;

				if(counter % 1000 == 0){
					System.out.println(counter);
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

	public static ArrayList<Integer> getZones(File infile){
		ArrayList<Integer> list = new ArrayList<Integer>();
		try{
			BufferedReader zonereader = new BufferedReader(new FileReader(infile));
			String line = null;
			while ((line = zonereader.readLine()) != null){
				String[] tokens = line.split(",");
				Integer zones = Integer.parseInt(tokens[0]);
				list.add(zones);
			}
			zonereader.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return list;
	}

	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static LonLat outsideAllocation(LonLat point){
		List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {
			//			System.out.println("2.3: " + point);
			return point; 
		}
		else{
			String zonecode = String.valueOf(Integer.parseInt(zonecodeList.get(0)));
			//			System.out.println("2.1: " + zonecode);
			LonLat newpoint = allocator.allocate(zonecode,1);
			//			System.out.println("2.2: " + newpoint);
			if(newpoint == null){
				return point;
			}
			else{
				return newpoint;
			}
		}
	}

	public static LonLat insideAllocation(String zonecode, LonLat point){
//		System.out.println(zonecode);
		String meshcode = NewPTAllocation.getMeshcode(zonecode);
		//		System.out.println("1.1: " + meshcode);
		LonLat newpoint = NewPTAllocation.allocateWithinMesh(meshcode, 1);
		//	System.out.println("1.2: " + newpoint);
		if (newpoint == null){
			Mesh mesh = new Mesh(meshcode);
			LonLat point2 = mesh.getCenter();
			//			System.out.println("1.3: " + point2);
			return point2;
		}
		else{
			return newpoint;
		}
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

	public static double getmf2(File zonemf2, Integer zone){
		HashMap<Integer,Double> mf2 = new HashMap<Integer,Double>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(zonemf2));
			String line = br.readLine();
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Integer zonecode = Integer.valueOf(tokens[0]);
				Double mf = Double.parseDouble(tokens[1]);
				mf2.put(zonecode, mf);
			}
			br.close();
			//			System.out.println(mf2);
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		double mff = mf2.get(zone);
		if(mff == 0d){
			return 0d;
		}
		return mff;
	}

}
