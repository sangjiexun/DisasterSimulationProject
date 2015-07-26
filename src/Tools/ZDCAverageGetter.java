package Tools;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.Mesh;


public class ZDCAverageGetter{
	
	static File sf = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/zone_bounds_fujisawa_shape");
	static GeometryChecker gchecker = new GeometryChecker(sf);

	public static void main(String[] args) {

		Map<String,Integer> countdsmap1 = new HashMap<String, Integer>();
		Map<String,Integer> countdsmap2 = new HashMap<String, Integer>();
//		Map<String,Integer> countdsmap3 = new HashMap<String, Integer>();
//		Map<String,Integer> countdsmap4 = new HashMap<String, Integer>();
//		Map<String,Integer> countdsmap5 = new HashMap<String, Integer>();
		Set<String> meshcodeset = new HashSet<String>();

		File dsfile1 = new File("C:/Users/yabec_000/Desktop/density_20110301_fujisawa_14.csv");
		File dsfile2 = new File("C:/Users/yabec_000/Desktop/density_20110302_fujisawa_14.csv");
//		File dsfile3 = new File("C:/Users/yabec_000/Desktop/density_20110303_fujisawa_14.tsv");
//		File dsfile4 = new File("C:/Users/yabec_000/Desktop/density_20110304_fujisawa_14.tsv");
//		File dsfile5 = new File("C:/Users/yabec_000/Desktop/density_20110307_fujisawa_14.tsv");

		File outfile = new File("C:/Users/yabec_000/Desktop/ZDC_heiji_Fujisawa_15.csv");

		try{
			BufferedReader br = new BufferedReader(new FileReader(dsfile1));
			String line = null;
			while( (line = br.readLine()) != null ) {

				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				Integer count   = Integer.valueOf(tokens[2]);

				Mesh mesh = new Mesh(meshcode);
				GeometryChecker inst = new GeometryChecker(sf);
				if (inst.checkOverlap(mesh.getCenter().getLon(), mesh.getCenter().getLat()) == true){
					countdsmap1.put(meshcode, count);
					meshcodeset.add(meshcode);
				}
			}
			br.close();

			BufferedReader br2= new BufferedReader(new FileReader(dsfile2));
			String line2 = null;
			while( (line2 = br2.readLine()) != null ) {

				String[] tokens = line2.split("\t");
				String meshcode = tokens[0];
				Integer count   = Integer.valueOf(tokens[2]);

				Mesh mesh = new Mesh(meshcode);
				GeometryChecker inst = new GeometryChecker(sf);
				if (inst.checkOverlap(mesh.getCenter().getLon(), mesh.getCenter().getLat()) == true){
					countdsmap2.put(meshcode, count);
					meshcodeset.add(meshcode);
				}
			}
			br2.close();

//			BufferedReader br3 = new BufferedReader(new FileReader(dsfile3));
//			String line3 = null;
//			while( (line3 = br3.readLine()) != null ) {
//
//				String[] tokens = line3.split("\t");
//				String meshcode = tokens[0];
//				Integer count   = Integer.valueOf(tokens[2]);
//
//				Mesh mesh = new Mesh(meshcode);
//				GeometryChecker inst = new GeometryChecker(sf);
//				if (inst.checkOverlap(mesh.getCenter().getLon(), mesh.getCenter().getLat()) == true){
//					countdsmap3.put(meshcode, count);
//					meshcodeset.add(meshcode);
//				}
//			}
//			br3.close();
//
//			BufferedReader br4 = new BufferedReader(new FileReader(dsfile4));
//			String line4 = null;
//			while( (line4 = br4.readLine()) != null ) {
//
//				String[] tokens = line4.split("\t");
//				String meshcode = tokens[0];
//				Integer count   = Integer.valueOf(tokens[2]);
//
//				Mesh mesh = new Mesh(meshcode);
//				GeometryChecker inst = new GeometryChecker(sf);
//				if (inst.checkOverlap(mesh.getCenter().getLon(), mesh.getCenter().getLat()) == true){
//					countdsmap4.put(meshcode, count);
//					meshcodeset.add(meshcode);
//				}
//			}
//			br4.close();
//
//			BufferedReader br5 = new BufferedReader(new FileReader(dsfile5));
//			String line5 = null;
//			while( (line5 = br5.readLine()) != null ) {
//
//				String[] tokens = line5.split("\t");
//				String meshcode = tokens[0];
//				Integer count   = Integer.valueOf(tokens[2]);
//
//				Mesh mesh = new Mesh(meshcode);
//				GeometryChecker inst = new GeometryChecker(sf);
//				if (inst.checkOverlap(mesh.getCenter().getLon(), mesh.getCenter().getLat()) == true){
//					countdsmap5.put(meshcode, count);
//					meshcodeset.add(meshcode);
//				}
//			}
//			br5.close();


			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

//			bw.write("meshcode\tave_count\tpolygon");
//			bw.newLine();
			for(String mc:meshcodeset){

				Mesh  mesh     = new Mesh(mc);
				Rectangle2D.Double rect = mesh.getRect();
				String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
						rect.getMinX(),rect.getMaxY(),
						rect.getMaxX(),rect.getMaxY(),
						rect.getMaxX(),rect.getMinY(),
						rect.getMinX(),rect.getMinY());

				int countds1 = 0;
				int countds2 = 0;
				int countds3 = 0;
				int countds4 = 0;
				int countds5 = 0;

				if(countdsmap1.containsKey(mc)){
					countds1 = countdsmap1.get(mc);
				};
				if(countdsmap2.containsKey(mc)){
					countds2 = countdsmap2.get(mc);
				};
//				if(countdsmap3.containsKey(mc)){
//					countds3 = countdsmap3.get(mc);
//				};
//				if(countdsmap4.containsKey(mc)){
//					countds4 = countdsmap4.get(mc);
//				};
//				if(countdsmap5.containsKey(mc)){
//					countds5 = countdsmap5.get(mc);
//				};

				int average_count = ((countds1+countds2)/2);

				bw.write(mc
						+"\t" + average_count
						+"\t" + wkt);
				bw.newLine();
			}
			bw.close();


		}

		catch(FileNotFoundException a) {
			System.out.println("File not found");
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
}

