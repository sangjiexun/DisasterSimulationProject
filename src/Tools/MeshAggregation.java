package Tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.obs.aggre.ATrafficVolume;
import jp.ac.ut.csis.pflow.obs.aggre.MeshTrafficVolume;

import com.beachstone.JWposChange;

/**
 * 繝｡繝�繧ｷ繝･莠､騾夐㍼髮�險医け繝ｩ繧ｹ
 *
 * <dl>
 * <dt><b>螟夜Κ繝ｩ繧､繝悶Λ繝ｪ</b></dt>
 * <dd>commons-lang-xxxx.jar</dd>
 * </dl>
 *
 * @author H.Kanasugi@CSIS. UT.
 * @since 2013/07/02
 */
public class MeshAggregation extends ATrafficVolume {
	/* ==============================================================
	 * static methods
	 * ============================================================== */

	public static void main(String[] args) throws IOException {
		// create instance ////////////////////////////////
		MeshTrafficVolume volume = new MeshTrafficVolume(3);	 // mesh level=5

		// load files and aggregate traffic counts ////////
		String in = "/home/t-tyabe/Data/"+args[0]+"_raw_onlyshutoken";
		String out = in+"_mesh";
		File indir = new File(in+".csv");

		try{
			BufferedReader br = new BufferedReader(new FileReader(indir));
			String line = null;
			int i = 1;
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split("\t");
//				String pid = tokens[0];
				//				int tripno = 0;
				//				int exfactor=0;
				//				int transport=0;

				double lon = Double.parseDouble(tokens[1]);
				double lat = Double.parseDouble(tokens[2]);
				LonLat pos = new LonLat(lon, lat);
//				System.out.println(pid);
				
				volume.aggregate(String.valueOf(i),0,pos,1,1);
				i++;
				if (i % 10000 == 0){
					System.out.println(i);
				}
			}
			br.close();
		}

		catch(FileNotFoundException e) {
			System.out.println("File not found:");
		}
		catch(IOException e) {
			System.out.println(e);
		}

		// file export ////////////////////////////////////
		volume.export(new File(out+".csv"));
	}


	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** mesh level	*/	private int _meshLevel;


	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * 蛻晄悄蛹�
	 * @param meshLevel 繝｡繝�繧ｷ繝･繝ｬ繝吶Ν
	 */
	public MeshAggregation(int meshLevel) {
		super();
		_meshLevel = meshLevel;
	}


	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ut.csis.yok.pflow.ptconv.eval.ATrafficVolume#acceptTransport(int) */
	@Override
	protected boolean acceptTransport(int transport) {
		//			01SZK2
		//			return transport < 5; // 01SZK2; walk, bicycle, bikes
		//			return 5 <= transport && transport <= 12;	// 01SZK2; vehicle
		return true;
	}

	/* @see jp.ut.csis.yok.pflow.ptconv.eval.ATrafficVolume#export(java.io.File) */
	@Override
	public void export(File outfile) {
		BufferedWriter bw   = null;
		try {
			// open output file ///////////////////////////
			bw = new BufferedWriter(new FileWriter(outfile));
//			bw.write("meshcode" + "\t" + "count" + "\t" + "wkt");
//			bw.newLine();
			// export aggregated data /////////////////////
			for(Entry<String,Integer> entry:getTrafficCount().entrySet()) {
				String meshcode = entry.getKey();
				int    count    = entry.getValue();
//				Mesh  mesh     = new Mesh(meshcode);
//				Rectangle2D.Double rect = mesh.getRect();
//				String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",
//						rect.getMinX(),rect.getMinY(),
//						rect.getMinX(),rect.getMaxY(),
//						rect.getMaxX(),rect.getMaxY(),
//						rect.getMaxX(),rect.getMinY(),
//						rect.getMinX(),rect.getMinY());
				// File out ///////////////////////////////
				bw.write(meshcode+","+count);
				bw.newLine();
			}
		}
		catch(IOException exp) { exp.printStackTrace(); }
		finally {
			try { if( bw != null ) { bw.close(); } }
			catch(IOException exp) { exp.printStackTrace(); }
		}
	}

	/**
	 * 謖�螳壹＠縺溷ｺｧ讓吶°繧峨Γ繝�繧ｷ繝･繧ｳ繝ｼ繝峨ｒ蜿門ｾ励☆繧�
	 * @param point 蠎ｧ讓�
	 * @return 繝｡繝�繧ｷ繝･繧ｳ繝ｼ繝�
	 */
	protected String getAggregationUnit(LonLat point) {
		return new Mesh(_meshLevel,point.getLon(),point.getLat()).getCode();
	}

	/**
	 * 髮�險医☆繧九Γ繝�繧ｷ繝･繝ｬ繝吶Ν繧貞叙蠕�
	 * @return 繝｡繝�繧ｷ繝･繝ｬ繝吶Ν
	 */
	public int getMeshLevel() {
		return _meshLevel;
	}

	/**
	 * 髮�險医☆繧九Γ繝�繧ｷ繝･繝ｬ繝吶Ν繧定ｨｭ螳�
	 * @param meshlevel 繝｡繝�繧ｷ繝･繝ｬ繝吶Ν
	 */
	public void setMeshLevel(int meshlevel) {
		_meshLevel = meshlevel;
	}
	
	public static LonLat xy2lonlat(double x,double y) {
		JWposChange converter = new JWposChange(x,y,9);
		converter.XYtoLatLongJ();
		return new LonLat(converter.getY(), converter.getX());
	}
}


