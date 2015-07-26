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

import jp.ac.ut.csis.pflow.geom.Mesh;


public class MeshLevelConverter {
	public static void main(String args[]){

		//		File out = new File("C:/Users/yabec_000/Desktop/Tokyo3wards_meshcodes_3.csv");

		for (int i = 0; i<=23; i++){
			File infile= new File("C:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_5_real_" + i + ".csv");
			File out = new File("C:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_4_" + i + ".csv");
			mesh5to4(infile, out, "\t");
		}

	}

	public static File mesh5to4(File in, File out, String div){
		File meshes = new File("C:/Users/yabetaka/Desktop/Tokyo3wards_meshcodes_4.csv");

		//be able to put many counts as possible.. List?
		Map<String, Integer> meshmap = new HashMap<String, Integer>();
		Set<String> meshcodeset = new HashSet<String>();

		try{
			BufferedReader br1 = new BufferedReader (new FileReader(meshes));
			String line1 = null;
			while ((line1 = br1.readLine()) != null){
				meshcodeset.add(line1);
			}
			br1.close();

			BufferedReader br = new BufferedReader (new FileReader(in));
			String line = null;
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split(div);
				String meshcode = tokens[0];
				Double countd   = Double.parseDouble((tokens[1]));
				int count = (int)Math.floor(countd);

				String conmesh = meshcode.substring(0, 9);

				if(meshmap.containsKey(conmesh)){
					count = (meshmap.get(conmesh) + count) ;
				}
				meshmap.put(conmesh, count);
			}
			br.close();

			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			for (String mc:meshcodeset){
				Mesh  mesh     = new Mesh(mc);
				Rectangle2D.Double rect = mesh.getRect();
				String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
						rect.getMinX(),rect.getMaxY(),
						rect.getMaxX(),rect.getMaxY(),
						rect.getMaxX(),rect.getMinY(),
						rect.getMinX(),rect.getMinY());
				if(meshmap.get(mc)!=null){
					bw.write(mc + "\t" + meshmap.get(mc) + "\t" + wkt);
				}
				else{
					bw.write(mc + "\t" + 0 + "\t" + wkt);
				}
				bw.newLine();
			}
			bw.close();
		}

		catch(FileNotFoundException a) {
			System.out.println("File not found:");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return out;

	}
}
