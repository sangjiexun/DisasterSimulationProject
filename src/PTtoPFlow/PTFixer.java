package PTtoPFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import jp.ac.ut.csis.pflow.geom.Mesh;

public class PTFixer {

	public static void main(String args[]){
		File in = new File ("c:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo_PT_Final.csv");
		File out = new File ("c:/Users/yabec_000/Desktop/Tokyo_PT_Final3.csv");
		writeMeshcode(in,out);
	}

	public static File writeMeshcode(File infile, File outfile){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Double lon = Double.parseDouble(tokens[10]);
				Double lat = Double.parseDouble(tokens[11]);
				Mesh mesh = new Mesh(5, lon,lat);
				String meshcode = mesh.getCode();
				bw.write(line + "," + meshcode);
				bw.newLine();
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}

		return outfile;
	}

	public static File removePID(File infile, File outfile){
		int i = 0;
		int j = 0;
		int k = 0;
		int count = 1;

		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Double lon = Double.parseDouble(tokens[10]);
				Double lat = Double.parseDouble(tokens[11]);
				Mesh mesh = new Mesh(5, lon,lat);
				String meshcode = mesh.getCode();

				Random ran = new Random();
				double dig = ran.nextDouble();

				if(meshcode.equals("5339462121")){
					if(dig<0.765897){
						bw.write(line);
						bw.newLine();
					}
					else{
						i++;
						continue;
					}
				}
				if(meshcode.equals("5339461114")){
					if(dig<0.810442){
						bw.write(line);
						bw.newLine();
					}
					else{
						j++;
						continue;
					}
				}
				if(meshcode.equals("5339452542")){
					if(dig<0.789624){
						bw.write(line);
						bw.newLine();
					}
					else{
						k++;
						continue;
					}
				}
				else {
					bw.write(line);
					bw.newLine();
				}
				count++;
				System.out.println("count: " + count );
			}
			System.out.println("5339462121: " + i );
			System.out.println("5339461114: " + j );
			System.out.println("5339452542: " + k );
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}

		return outfile;
	}


}
