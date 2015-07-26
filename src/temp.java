import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import jp.ac.ut.csis.pflow.geom.Mesh;


public class temp {

	public static void main(String args[]){
		File result = new File ("C:/Users/yabec_000/Desktop/Test_AllAgents.csv");
		File newres = new File ("C:/Users/yabec_000/Desktop/new_Test_AllAgents.csv");
		try{
			BufferedReader br = new BufferedReader(new FileReader(result));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newres));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				String code = tokens[0];
				String pop  = tokens[1];

				Mesh  mesh     = new Mesh(code);
				Rectangle2D.Double rect = mesh.getRect();
				String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
						rect.getMinX(),rect.getMaxY(),
						rect.getMaxX(),rect.getMaxY(),
						rect.getMaxX(),rect.getMinY(),
						rect.getMinX(),rect.getMinY());

				bw.write(code + "\t" + pop + "\t" + wkt);
				bw.newLine();
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
	}
}
