package PTtoPFlow;

import java.io.File;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.pt.parser.PTLocations;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;


public class ZoneReallocation {

	/* ==============================================================
	 * static methods
	 * ============================================================== */
	/**
	 * ゾーンコードの空間配分テスト
	 * @param args not required
	 */
	public static void main(String[] args) {
		// Initialize DB configuration ////////////////////
		DBCPLoader.initPgSQLConnection("postgres","Taka0505","pflowdrm"); // id,pw,dbname

		// load shape data of Fujisawa zone ///////////////
		File shapedir = new File("C:/Users/yabec_000/Desktop/realloc_fujisawa/zone_bounds_fujisawa_shape/");
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

		// execute with samples ///////////////////////////
		LonLat samples[] = new LonLat[]{new LonLat(139.47847366,35.29901518),	// 江ノ島あたり
										new LonLat(139.4827652, 35.34145465)};	// 藤沢駅あたり

		for(LonLat sample:samples) {
			List<String> zonecodeList = gchecker.listOverlaps("zonecode",sample.getLon(),sample.getLat());
			if( zonecodeList == null || zonecodeList.isEmpty() ) { continue; }
			String       zonecode     = zonecodeList.get(0);
			System.out.println(allocator.allocate(zonecode,1));	// 1=>commuting purpose
		}

		// cleanup DB connection //////////////////////////
		DBCPLoader.closePgSQLConnection();
	}

}
