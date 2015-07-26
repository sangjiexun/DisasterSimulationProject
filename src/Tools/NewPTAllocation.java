package Tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

import org.apache.commons.lang.math.RandomUtils;

import PTtoPFlow.PTZoneUtils;

public abstract class NewPTAllocation {

	/** zonetomesh table name	*/	protected static final String ZONEtoMESH_TABLE = "zonetomeshcode6";
	/** reallocationinMesh table name	*/	protected static final String REALLOC_MESH_TABLE = "reallocationbymesh";

	public static String getMeshcode(String zonecode){
		String column = ("z"+ zonecode);
//				System.out.println("column is " + column);
		Connection con    = null;
		Statement  stmt   = null;
		ResultSet  res    = null;
		String     result = null;
		try {
			// DB connection ==============================
			if( (con = DBCPLoader.getPgSQLConnection()) == null ) {
				System.err.println("fail to make DB connection");
				return null;
			}
			// compose SQL command: get distribution probabilities of the specified zone
			String sql = String.format
					("SELECT meshcode,%s FROM %s WHERE %s >0 ORDER by %s DESC", column, ZONEtoMESH_TABLE, column, column);
			stmt = con.createStatement();
			res  = stmt.executeQuery(sql);
//			System.out.println(res.next());
			// allocating zone code to point ==============
			double ratio   = 0;
			for(double rand=RandomUtils.nextDouble();res.next();) {
				ratio += res.getDouble(column);
				if( rand < ratio ) {
					result = res.getString("meshcode");
	//									System.out.println("res : " + result);
					break;
				}
			}
		}
		catch(SQLException exp) { 
			return null;
		}
		finally {
			try { if( res != null ) { res.close(); }
			if( stmt!= null ) { stmt.close();}
			if( con != null ) { con.close(); } }
			catch(SQLException exp) { exp.printStackTrace(); }
		}
		return result;
	}

	public static LonLat allocateWithinMesh(String meshcode, int purpose) {
		// check purpose //////////////////////////////////
		FacilityType ftype = getTargetFacility(purpose);
		if( ftype == null ) { 
			System.out.println("shit");
			return null; 
			}
		// start resolving reallocation  //////////////////
		Connection con    = null;
		Statement  stmt   = null;
		ResultSet  res    = null;
		LonLat     result = null;
		try {
			// DB connection ==============================
			if( (con = DBCPLoader.getPgSQLConnection()) == null ) {
				System.err.println("fail to make DB connection");
				return null;
			}
			// compose SQL command: get distribution probabilities of the specified zone
			String sql = String.format("SELECT gid AS idx,ST_X(geom) AS x,ST_Y(geom) AS y,%s FROM %s WHERE meshcode='%s' ORDER by %s DESC",
					ftype.getColumn(),REALLOC_MESH_TABLE,meshcode,ftype.getColumn());
			stmt = con.createStatement();
			res  = stmt.executeQuery(sql);
			// allocating zone code to point ==============
			double ratio   = 0;
			String colname = ftype.getColumn();
			for(double rand=RandomUtils.nextDouble();res.next();) {
				ratio += res.getDouble(colname);
				if( rand < ratio ) {
					//					String idx = res.getString("idx");
					result = new LonLat(res.getDouble("x"),res.getDouble("y"));
			//							System.out.printf("%s >> (%s)\n",meshcode,result);
					break;
				}
			}
		}
		catch(SQLException exp) { exp.printStackTrace(); }
		finally {
			try { if( res != null ) { res.close(); }
			if( stmt!= null ) { stmt.close();}
			if( con != null ) { con.close(); } }
			catch(SQLException exp) { exp.printStackTrace(); }
		}
		return result;
	}

	protected static FacilityType getTargetFacility(int purpose) {
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

	public enum FacilityType {
		/* constants ---------------------------- */
		/** 住宅		*/	HOME    ("h_ratio"),
		/** 事業所	*/	BUSINESS("b_ratio"),
		/** 学校		*/	SCHOOL  ("s_ratio"),
		/** 共通		*/	COMMON  ("ratio");
		/* instance fields ---------------------- */
		/** column name */private String __column;
		/* constructors ------------------------- */
		/**
		 * 初期化
		 * @param column カラム名
		 */
		private FacilityType(String column) {
			__column = column;
		}
		/* instance methods --------------------- */
		/**
		 * カラム名の取得
		 * @return カラム名
		 */
		protected String getColumn() {
			return __column;
		}
	}

}
