package PTtoPFlow;
/* table configuration(PostgreSQL) --------------
create table reallocationtable (
  gid      serial primary key,
  zonecode varchar(8),
  h_ratio  float4,
  b_ratio  float4,
  s_ratio  float4
);
select addGeometryColumn('08tky','reallocationtable','geom',4326,'POINT',2);
create index idx_reallocationtable on reallocationtable using btree(zonecode);

** table configuration/jica pt ------------------
create table reallocationtable (
  gid      serial primary key,
  zonecode varchar(8),
  ratio    float8
);
select addGeometryColumn('','reallocationtable','geom',4326,'POINT',2);
create index idx_reallocationtable on reallocationtable using btree(zonecode);
 * ---------------------------------------------- */


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.pt.parser.PTNode;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

import org.apache.commons.lang.math.RandomUtils;

/**
 * ゾーンコード、駅コード、バス停コードなどの経緯度取得．ゾーンコードの空間配分など
 * @author H.Kanasugi@EDITORIA. UTokyo
 * @since 2012/03/14
 * 
 * ********************************edited by Yabe.
 */
public abstract class PT_Locations {
	/* ==============================================================
	 * enumeration classes
	 * ============================================================== */
	/** 施設種別の列挙クラス */
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


	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** reallocation table name	*/	protected static final String REALLOC_TABLE = "reallocationtable";
	/** zone code table name	*/	protected static final String ZONE_TABLE    = "pt08tky.zonecodetable";
	/** station code table name	*/	protected static final String STATION_TABLE = "data.stationcodetable";
	/** bus-stop code table name*/	protected static final String BUSSTOP_TABLE = "data.busstopcodetable";
	/** tollgate code table name*/	protected static final String TOLLGATE_TABLE= "data.tollgatecodetable";


	/* ==============================================================
	 * static methods
	 * ============================================================== */
	/*
	 * ゾーンコードの配分テスト
	 * @param args 0:SQLite　DBファイルパス
	 *
	public static void main(String[] args) {
		// Initialize DB configuration ////////////////////
		DBCPLoader.initPgSQLConnection();


		PTLocations allocator = new PTLocations() {

			@Override
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

			@Override
			protected int getPurposeOfGoingHome() {
				return 3;
			}
		};
		for(int i=0;i<10;i++) {
			System.out.println(allocator.allocate("24101",1));	// 1=>commuting purpose
		}

		// cleanup DB connection //////////////////////////
		DBCPLoader.closePgSQLConnection();
	}
	*/



	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * 指定したゾーンコードについて、移動目的に応じて配分処理した位置を取得
	 * @param zonecode ゾーンコード
	 * @param purpose 移動目的コード（負数を指定すると、住宅限定に設定）
	 * @return 配分した建物の経緯度。配分できなかった場合はnull）
	 */
	public LonLat allocate(String zonecode, int purpose) {
		// check purpose //////////////////////////////////
		FacilityType ftype = getTargetFacility(purpose);
		if( ftype == null ) { return null; }
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
			String sql = String.format("SELECT gid AS idx,ST_X(geom) AS x,ST_Y(geom) AS y,%s FROM %s WHERE zonecode='%s' ORDER by %s DESC",
										ftype.getColumn(),REALLOC_TABLE,zonecode,ftype.getColumn());
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
//					System.out.printf("%s >> (%s)\n",zonecode,result);
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

	/**
	 * 指定したゾーンコードからゾーン代表点経緯度を取得
	 * @param zonecode ゾーンコード
	 * @return ゾーン代表点経緯度
	 */
	public PTNode resolveZone(String zonecode) {
		return resolve(ZONE_TABLE,zonecode);
	}
	/**
	 * 指定した駅コードから駅位置座標を取得
	 * @param stationcode 駅コード
	 * @return 駅座標
	 */
	public PTNode resolveStation(String stationcode) {
		return resolve(STATION_TABLE,stationcode);
	}
	/**
	 * 指定したバス停コードからバス停位置情報を取得
	 * @param busstopcode バス停コード
	 * @return バス停位置
	 */
	public PTNode resolveBusStop(String busstopcode) {
		return resolve(BUSSTOP_TABLE,busstopcode);
//		if( node != null ) { node.setCode(null); }
//		return node;
	}

	/**
	 * 高速道路IC位置情報を取得
	 * @param iccode 高速道路ICコード
	 * @return IC位置
	 */
	public PTNode resolveTollgate(String iccode) {
		return resolve(TOLLGATE_TABLE,iccode);
	}

	/**
	 * 指定したコードから代表点経緯度を取得
	 * @param table テーブル(ゾーン、駅、バス停、ICなど）
	 * @param code コード
	 * @return 代表点座標
	 */
	private PTNode resolve(String table,String code) {
		// start resolving position ///////////////////////
		Connection con    = null;
		Statement  stmt   = null;
		ResultSet  res    = null;
		PTNode     result = null;
		try {
			// DB connection ==============================
			if( (con = DBCPLoader.getPgSQLConnection()) == null ) {
				throw new NullPointerException("fail to connect DB");
			}
			// extract target zone area ===================
			String sql = String.format("SELECT code,ST_X(geom) AS x,ST_Y(geom) AS y,flag FROM %s WHERE code='%s'",table,code);
			stmt = con.createStatement();
			res  = stmt.executeQuery(sql);
			// get zone position ==========================
			if( res.next() ) {
				int flag = res.getInt("flag");
				if( flag == 0 ) {
					result = new PTNode(res.getDouble("x"),res.getDouble("y"),res.getString("code"));
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

	/**
	 * 移動目的コードに沿って施設種別を返す。調査ごとに作成する必要がある
	 * @param purpose 移動目的コード
	 * @return 施設種別
	 */
	protected abstract FacilityType getTargetFacility(int purpose);

	/**
	 * 帰宅目的コードを取得。調査毎に作成する必要がある
	 * @return 帰宅目的コード
	 */
	public abstract int getPurposeOfGoingHome();

}
