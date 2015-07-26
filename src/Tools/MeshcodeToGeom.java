package Tools;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.Mesh;
import KsymSimulation.Simulation_ver2;

public class MeshcodeToGeom {

	public static void main(String args[]){
		File in = new File("c:/Users/yabec_000/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_5_20.csv");
		File out = new File("c:/Users/yabec_000/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_5_20_Tokyo.csv");
		File meshes = new File ("c:/Users/yabec_000/Desktop/Tokyo3Wards_meshcodes_5.csv"); //file of meshcodes

		getGeomfromMeshcode(in, out, meshes);
	}

	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	//static File shapedir = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/zone_bounds_fujisawa_shape");

	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static File getGeomfromMeshcode(File infile, File outfile, File meshlist){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while((line = br.readLine()) != null){
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				Mesh mesh = new Mesh(meshcode);
				ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);

				if( meshes.contains(meshcode) ){
					Double pop = Double.parseDouble(tokens[1]);
					Rectangle2D.Double rect = mesh.getRect();
					String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
							rect.getMinX(),rect.getMaxY(),
							rect.getMaxX(),rect.getMaxY(),
							rect.getMaxX(),rect.getMinY(),
							rect.getMinX(),rect.getMinY());

					bw.write(meshcode + "\t" + pop + "\t" + wkt);
					bw.newLine();
				}
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return outfile;
	}
}