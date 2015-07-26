package PTtoPFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;
import Tools.NewPTAllocation;

/*
 * Made by Taka Yabe 12.21.2014
 * Don't forget to get homelocation and goal location LonLats from PTver6!
 */

public class PTver5 {

	public static void main(String[] args){

		// Files and DB
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname
		File infile = new File("C:/Users/yabec_000/Desktop/Fujisawa_noMagFac_Allinfo.csv");
		File outfile = new File ("c:/Users/yabec_000/Desktop/Fujisawa_PT_forSim.csv");
		File zones = new File ("c:/Users/yabec_000/Desktop/Fujisawa_Zones.csv");

		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

			int counter = 1;

			String line = br.readLine();
			while((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				String id = tokens[0];
				Integer mf = Integer.valueOf(tokens[1]);
				String sex = tokens[2];
				String age = tokens[3];
				String address = tokens[4];
				String goalcode = tokens[5];
				String purpose = tokens[6];
				String transport = tokens[7];
				Double lon = Double.parseDouble(tokens[8]);
				Double lat = Double.parseDouble(tokens[9]);
				LonLat point = new LonLat(lon, lat);
				String zonecode = tokens[10];
				LonLat allopoint = null;

				for (int i=0; i<mf; i++){ // repeat for magfac times
					if (counter % 8 == 0){ // calculate if able to divide by 50

						LonLat home = allocatewithRisk(address, 3, zones, point);
						System.out.println("home: " +home);
						LonLat goal = allocatewithRisk(goalcode, 1, zones, point);
						System.out.println("goal: " +goal);

						if(getZones(zones).contains(zonecode)) {
							allopoint = insideAllocation(zonecode, point);
						}
						else{
							allopoint = outsideAllocation(point);
						}
						bw.write(counter
								+ "," + id
								+ "," + mf
								+ "," + sex
								+ "," + age
								+ "," + address
								+ "," + zonecode
								+ "," + purpose
								+ "," + transport
								+ "," + allopoint.getLon()
								+ "," + allopoint.getLat()
								+ "," + home.getLon()
								+ "," + home.getLat()
								+ "," + goal.getLon()
								+ "," + goal.getLat()
								);
						bw.newLine();
						System.out.println(counter);
						counter++;
					}
					else{
						counter++;
					}
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
				Integer zones = Integer.valueOf(tokens[0]);
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

	static File shapedir = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/zone_bounds_fujisawa_shape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static LonLat outsideAllocation(LonLat point){
		List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {
			System.out.println("2.3: " + point);
			return point; 
		}
		else{
			String zonecode = String.valueOf(Integer.parseInt(zonecodeList.get(0)));
			System.out.println("2.1: " + zonecode);
			LonLat newpoint = allocator.allocate(zonecode,1);
			System.out.println("2.2: " + newpoint);
			if(newpoint == null){
				return point;
			}
			else{
				return newpoint;
			}
		}
	}

	public static LonLat insideAllocation(String zonecode, LonLat point){
		System.out.println(zonecode);
		String meshcode = NewPTAllocation.getMeshcode(zonecode);
		System.out.println("1.1: " + meshcode);
		LonLat newpoint = NewPTAllocation.allocateWithinMesh(meshcode, 1);
		System.out.println("1.2: " + newpoint);
		if (newpoint == null){
			Mesh mesh = new Mesh(meshcode);
			LonLat point2 = mesh.getCenter();
			System.out.println("1.3: " + point2);
			return point2;
		}
		else{
			return newpoint;
		}
	}

	public static LonLat allocatewithRisk(String zonecode, int purpose, File zones, LonLat kari){
		LonLat point;
		if(getZones(zones).contains(zonecode)){
			Integer zc = Integer.valueOf(zonecode);
			String newzc = String.format("%05d", zc);
//			System.out.println("zc " + zc);
			point    = allocator.allocate(newzc, purpose);
//			System.out.println("3.1: " + point);
			return point;
		}
		else{	
			LonLat point1 = PTZoneUtils.getZoneRepresentative(Integer.valueOf(zonecode));
//			System.out.println("3.2: " + point1);
			if(point1 == null){
//				System.out.println("3.3: " + kari);
				return kari;
			}
			else{
//				System.out.println("3.4: " + point1);
				return point1;
			}
		}
	}

}
