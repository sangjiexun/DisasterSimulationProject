package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.tools.DBCPLoader;

public abstract class ReallocationtableModifier {

	/*
	 * class for modifying reallocation table
	 * 1. put in meshcode column of building
	 * 2. normalize probabilities within meshcodes, not zones
	 * 3. Create new table
	 * 
	 */

	public static void main(String args[]) throws IOException{

//				DBCPTLoader.initPgSQLConnection(
//						"localhost",
//						5432,
//						"postgres",
//						"Taka0505",
//						"pflowdrm",
//						"UTF-8");
//
//		File in  = new File ("c:/Users/yabec_000/Desktop/Fujisawabuildingdata.csv");
//		File out = new File ("c:/Users/yabec_000/Desktop/Fujisawa_BuildingData_seikika.csv");
//		getMeshcode(in);
//		normalize(in,out);
	}

	public static File normalize(File infile, File outfile){

		HashMap<Integer, Double> hsub = new HashMap<Integer, Double>();
		HashMap<Integer, Double> bsub = new HashMap<Integer, Double>();
		HashMap<Integer, Double> ssub = new HashMap<Integer, Double>();
		HashSet<String> meshcodes = new HashSet<String>();

		HashMap<String, HashMap<Integer, Double>> hmap = new HashMap<String, HashMap<Integer, Double>>();
		HashMap<String, HashMap<Integer, Double>> bmap = new HashMap<String, HashMap<Integer, Double>>();
		HashMap<String, HashMap<Integer, Double>> smap = new HashMap<String, HashMap<Integer, Double>>();

		HashMap<String, Double> hres = new HashMap<String, Double>();
		HashMap<String, Double> bres = new HashMap<String, Double>();
		HashMap<String, Double> sres = new HashMap<String, Double>();

		int counter = 1;

		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String line = null;
			while((line=br.readLine()) != null){
				String[] tokens = line.split(",");
				String mesh = tokens[1];
				meshcodes.add(mesh);
			}
			br.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}

		try{
			for(String m : meshcodes){
				BufferedReader br1 = new BufferedReader(new FileReader(infile));
				BufferedReader br2 = new BufferedReader(new FileReader(infile));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
				String line2 = null;
				while((line2= br1.readLine()) != null){
					String[] tokens = line2.split(",");
					String mesh = tokens[1];
					if(mesh.matches(m)){
						Integer gid = Integer.valueOf(tokens[0]);
						Double h = Double.parseDouble(tokens[2]);
						Double b = Double.parseDouble(tokens[3]);
						Double s = Double.parseDouble(tokens[4]);
						hsub.put(gid, h); 
						hmap.put(mesh, hsub); 
						bsub.put(gid, b);
						bmap.put(mesh, bsub);
						ssub.put(gid, s);
						smap.put(mesh, ssub);
						//						System.out.println("yes");
					}
					else{continue;}
				}

				//				System.out.println(smap);
				//				System.out.println("done sorting it out" + counter);
				for(String h1: hmap.keySet()){
					Double hsum = 0d;
					for(Integer gid :hmap.get(h1).keySet()){
						hsum = hsum + hmap.get(h1).get(gid);
					}
					hres.put(h1, hsum);
				}
				for(String b1: bmap.keySet()){
					Double bsum = 0d;
					for(Integer gid :bmap.get(b1).keySet()){
						bsum = bsum + bmap.get(b1).get(gid);
					}
					bres.put(b1, bsum);
				}
				for(String s1: smap.keySet()){
					Double ssum = 0d;
					for(Integer gid :smap.get(s1).keySet()){
						ssum = ssum + smap.get(s1).get(gid);
					}
					sres.put(s1, ssum);
				}

				String line1 = null;
				while((line1 = br2.readLine()) != null){
					String[] tokens = line1.split(",");
					Integer gid = Integer.valueOf(tokens[0]);
					String mesh = tokens[1];
					if(mesh.matches(m)){
						Double hh = (Double.parseDouble(tokens[2])/hres.get(mesh));
						Double bb = (Double.parseDouble(tokens[3])/bres.get(mesh));
						Double ss = (Double.parseDouble(tokens[4])/sres.get(mesh));
						String geom = tokens[5];
						bw.write(gid + "," + mesh + "," + hh + "," + bb + "," + ss + "," + geom);
						bw.newLine();
					}
					else{continue;}
				}
				hsub.clear();
				bsub.clear();
				ssub.clear();
				hmap.clear();
				bmap.clear();
				smap.clear();
				hres.clear();
				bres.clear();
				sres.clear();
				System.out.println(counter);
				counter = counter + 1;		
				br1.close();			
				br2.close();			
				bw.close();
			}
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return outfile;
	}

	/* ==============================================================
	 * enumeration classes
	 * ============================================================== */
	/** 譁ｽ險ｭ遞ｮ蛻･縺ｮ蛻玲嫌繧ｯ繝ｩ繧ｹ */
	public enum FacilityType {
		/* constants ---------------------------- */
		/** 菴丞ｮ�		*/	HOME    ("h_ratio"),
		/** 莠区･ｭ謇�	*/	BUSINESS("b_ratio"),
		/** 蟄ｦ譬｡		*/	SCHOOL  ("s_ratio"),
		/** 蜈ｱ騾�		*/	COMMON  ("ratio");
		/* instance fields ---------------------- */
		/** column name */private String __column;
		/* constructors ------------------------- */
		/**
		 * 蛻晄悄蛹�
		 * @param column 繧ｫ繝ｩ繝�蜷�
		 */
		private FacilityType(String column) {
			__column = column;
		}
		/* instance methods --------------------- */
		/**
		 * 繧ｫ繝ｩ繝�蜷阪�ｮ蜿門ｾ�
		 * @return 繧ｫ繝ｩ繝�蜷�
		 */
		protected String getColumn() {
			return __column;
		}
	}

	/** reallocation table name	*/	protected static final String REALLOC_TABLE = "fujisawa.reallocationtable";

	public static File getMeshcode(File outfile) throws IOException {
		// start resolving reallocation  //////////////////
		Connection con    = null;
		Statement  stmt   = null;
		ResultSet  res    = null;
		LonLat     result = null;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			// DB connection ==============================
			if( (con = DBCPLoader.getPgSQLConnection()) == null ) {
				System.err.println("fail to make DB connection");
				return null;
			}
			// compose SQL command: get tatemono id and (x,y)
			String sql = String.format
					("SELECT gid AS idx,"
							+ "ST_X(geom) AS x,ST_Y(geom) AS y, "
							+ "h_ratio, b_ratio, s_ratio, geom FROM %s", REALLOC_TABLE);
			stmt = con.createStatement();
			res  = stmt.executeQuery(sql);

			// get meshcode from (x,y)
			while(res.next()){
				result = new LonLat(res.getDouble("x"), res.getDouble("y"));
				Mesh mesh = new Mesh(5, result.getLon(), result.getLat());
				String meshcode = mesh.getCode();

				bw.write(res.getInt("idx") + "," + meshcode + "," + res.getDouble("h_ratio") 
						+ "," + res.getDouble("b_ratio") + "," + res.getDouble("s_ratio")
						+ "," + res.getString("geom"));
				bw.newLine();
			}
			bw.close();
		}
		catch(SQLException exp) { exp.printStackTrace(); }
		finally {
			try { if( res != null ) { res.close(); }
			if( stmt!= null ) { stmt.close();}
			if( con != null ) { con.close(); } }
			catch(SQLException exp) { exp.printStackTrace(); }
		}
		return outfile;
	}

	/**
	 * 遘ｻ蜍慕岼逧�繧ｳ繝ｼ繝峨↓豐ｿ縺｣縺ｦ譁ｽ險ｭ遞ｮ蛻･繧定ｿ斐☆縲りｪｿ譟ｻ縺斐→縺ｫ菴懈�舌☆繧句ｿ�隕√′縺ゅｋ
	 * @param purpose 遘ｻ蜍慕岼逧�繧ｳ繝ｼ繝�
	 * @return 譁ｽ險ｭ遞ｮ蛻･
	 */
	protected abstract FacilityType getTargetFacility(int purpose);
}
