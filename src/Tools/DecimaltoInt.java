package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DecimaltoInt {

	public static void main(String args[]){
		
		File infile = new File ("c:/Users/yabec_000/Desktop/exfactor_Tokyo.txt");
		File outfile = new File ("c:/Users/yabec_000/Desktop/Tokyo_newMagFac.csv");
		decimaltoInt(infile, outfile);
	
	}
	
	public static File decimaltoInt(File infile, File outfile){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				String id = tokens[0];
				int magfac = (int)Math.floor(Double.parseDouble(tokens[1]));
								
				bw.write(id + "," + magfac);
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
	
}
