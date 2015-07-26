import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.Mesh;


public class MeshChecker{
	static File sf = new File("C:/Users/yabec_000/Desktop/Target3Area");
	static GeometryChecker gchecker = new GeometryChecker(sf);
	public static void main(String[] args) {

		Map<String,Integer> countptmap = new HashMap<String, Integer>();
		Map<String,Integer> countdsmap = new HashMap<String, Integer>();
		Set<String> meshcodeset = new HashSet<String>();

		File ptfile = new File("C:/Users/yabec_000/Desktop/Tokyo_PT_Mesh5.csv");
		File dsfile = new File("C:/Users/yabec_000/Desktop/tokyoGPS_center_mesh_5.tsv");
		//File outfile= new File("C:/Users/yabec_000/Desktop/comparison_1447_mesh_ver4_4.txt");
		File CORfile= new File("C:/Users/yabec_000/Desktop/Tokyo_Mesh5_cor.csv");

		try{

//			ArrayList<String> meshlist = new ArrayList<String>();
//			File meshfile = new File("c:/Users/yabec_000/Desktop/meshcodes.csv");
//			try{
//				BufferedReader meshreader = new BufferedReader(new FileReader(meshfile));
//				String line = null;
//				while((line=meshreader.readLine())!= null){
//					String meshcode = line;
//					meshlist.add(meshcode);
//				}
//				meshreader.close();
//			}
//			catch(FileNotFoundException z) {
//				System.out.println("File not found 3");
//			}
//			catch(IOException e) {
//				System.out.println(e);
//			}

			BufferedReader br = new BufferedReader(new FileReader(ptfile));
			String line = br.readLine();
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				Double countd   = Double.parseDouble((tokens[1]));
//				System.out.println(countd);
				int count = (int)Math.floor(countd);

				Mesh mesh = new Mesh(meshcode);
				GeometryChecker inst = new GeometryChecker(sf);
				if (inst.checkOverlap(mesh.getCenter().getLon(), mesh.getCenter().getLat()) == true){
					countptmap.put(meshcode, count);
					meshcodeset.add(meshcode);
				}
			}
			br.close();

			br = new BufferedReader(new FileReader(dsfile));
			String line1 = br.readLine();
			while( (line1 = br.readLine()) != null ) {

				String[] tokens = line1.split("\t");
				String meshcode = tokens[0];
				Integer count   = Integer.valueOf(tokens[1]);

				Mesh mesh = new Mesh(meshcode);
				GeometryChecker inst = new GeometryChecker(sf);
				if (inst.checkOverlap(mesh.getCenter().getLon(), mesh.getCenter().getLat()) == true){
					countdsmap.put(meshcode, count);
					meshcodeset.add(meshcode);
				}
			}
			br.close();


//			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(CORfile));
			bw2.write("meshcode, count_pt, count_ds, difference, polygon");
			bw2.newLine();
			for(String mc:meshcodeset){

				Mesh  mesh     = new Mesh(mc);
				Rectangle2D.Double rect = mesh.getRect();
				String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
																							rect.getMinX(),rect.getMaxY(),
																							rect.getMaxX(),rect.getMaxY(),
																							rect.getMaxX(),rect.getMinY(),
																							rect.getMinX(),rect.getMinY());

				int countpt = 0;
				int countds = 0;

			if(countptmap.containsKey(mc)){
				countpt = countptmap.get(mc);
			}
			if(countdsmap.containsKey(mc)){
				countds = countdsmap.get(mc);
			}

			int diff = (countpt - countds);

//			bw.write(mc + "\t" + countpt +"\t" + countds +"\t" + diff + "\t"+ wkt);
//			bw.newLine();

			bw2.write(mc + "," + countpt +"," + countds +"," + diff +"," +wkt);
			bw2.newLine();
			}
//			bw.close();
			bw2.close();


		}

		catch(FileNotFoundException a) {
			System.out.println("File not found:");
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
}
