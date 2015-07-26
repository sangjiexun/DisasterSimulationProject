package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Corrector {

	public static void main (String args[]){
		
		for(int i = 0; i<=23; i++){
			File in = new File ("C:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_5_" + i + ".csv");
			File out = new File ("C:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_5_real_" + i + ".csv");
			corrector(in,out);
		}
		
	}
	
	public static File corrector(File in, File out){
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line = null;
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String meshcode5 = tokens[0];
				String meshcode4 = meshcode5.substring(0, 9);
				if(meshcode4.equals("533945093")){
					Double count   = (Double.valueOf(tokens[1]))/10;
					bw.write(meshcode5 + "\t" + count);
					bw.newLine();
				}
				else{
					Double count   = (Double.valueOf(tokens[1]));
					bw.write(meshcode5 + "\t" + count);
					bw.newLine();
				}
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return out;
	}

}
