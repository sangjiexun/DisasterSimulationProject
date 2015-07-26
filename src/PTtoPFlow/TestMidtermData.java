package PTtoPFlow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.STPoint;
import jp.ac.ut.csis.pflow.pt.interpolation.MidtermData;
import jp.ac.ut.csis.pflow.pt.parser.PTLocations;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

public class TestMidtermData {

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// Initialize DB configuration ////////////////////
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname

		File infile = new File("C:/Users/yabec_000/Desktop/fujisawa.txt");

		// load shape data of Fujisawa zone ///////////////
		File shapedir = new File("C:/Users/yabec_000/Desktop/realloc_fujisawa/zone_bounds_fujisawa_shape");
		GeometryChecker gchecker = new GeometryChecker(shapedir);

		// instantiate zone reallocation instance with Tokyo PT purpose
		PTLocations allocator = new PTLocations() {
			protected FacilityType getTargetFacility(int purpose) {
				switch(purpose) {
					// for business =======================
					case  1: case  4: case  5: case  6: case  7: case 8:
					case 10: case 11: case 12: case 13: case 14:
						return FacilityType.BUSINESS;
					// for school =========================
					case  2:
						return FacilityType.SCHOOL;
					// for home ===========================
					case  3:
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

		BufferedReader br  = null;
		try {
			br = new BufferedReader(new FileReader(infile));
			String line    = null;
			MidtermData previous=null;
			while( (line=br.readLine()) != null ) {
				MidtermData data = MidtermData.parse(line);





				if (Integer.parseInt(data.getSubTripNo())==1){
					STPoint sample = data.getDepPoint();

					for(int i=0;i<data.getExfactor1();i++) { //40回繰り返す

						// サブトリップ番号１の出発をallocate、その前（最後のサブトリップ）の到着もallocateというコード

						List<String> zonecodeList = gchecker.listOverlaps("zonecode",sample.getLon(),sample.getLat());
						if( zonecodeList == null || zonecodeList.isEmpty() ) { continue; }
						String       zonecode     = zonecodeList.get(0);
						System.out.println(allocator.allocate(zonecode,1) );	// 1=>commuting purpose
						// 「zonecodeの中でこういう目的で配分しました」
					}
				}
				previous = data;

				// ID,属性、散らした後のOとD、その間のODをprintln
				System.out.println(	data.getPID() + "\t" +
									data.getSex() +"\t" +
									data.getAge() + "\t" +
									data.getDepPoint() + " >> " + data.getArrPoint());
			}
		}

		catch(IOException exp) { exp.printStackTrace(); }
		finally {
			try { if( br != null ) { br.close(); } }
			catch(IOException exp) { exp.printStackTrace(); }
		}

		// cleanup DB connection //////////////////////////
		DBCPLoader.closePgSQLConnection();
	}
}
