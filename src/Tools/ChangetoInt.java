package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ChangetoInt {

	public static void main(String args[]){
		File infile = new File ("c:/Users/yabec_000/Desktop/08tokyoPT-zone-point.csv");
		File outfile = new File ("c:/Users/yabec_000/Desktop/New-Zone-Point.csv");

		changetoInt(infile, outfile);
		
	}
	
	public static File changetoInt(File infile, File outfile){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Integer zone = Integer.valueOf(tokens[0]);
				Double lon = Double.parseDouble(tokens[1]);
				Double lat = Double.parseDouble(tokens[2]);
				bw.write(zone + "," + lon + "," + lat);
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
		return outfile;
	}
	
}
