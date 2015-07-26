package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ReviseMagFac {
	
	public static void main(String args[]){
		File mf = new File ("c:/Users/yabec_000/Desktop/zone-magfac.csv");
		File pidmf = new File ("c:/Users/yabec_000/Desktop/Final_Pid-Zone-Magfac.csv");
		File outfile = new File ("c:/Users/yabec_000/Desktop/Last_Pid-Zone-Magfac.csv");
		
		reviseMF(pidmf, mf, outfile);
		
	}

	public static File reviseMF(File infile, File mffile, File outfile){
		int yes = 0;
		int no = 0;
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				String pid = tokens[0];
				String zone = tokens[1];
				Double mf = Double.parseDouble(tokens[2]);
				if(getMFMap(mffile).containsKey(zone)){
					bw.write(pid + "," + zone + "," + (int)Math.floor(mf*getMFMap(mffile).get(zone)));
					bw.newLine();
					yes = yes + 1;
				}
				else{
					bw.write(pid + "," + zone + "," + (int)Math.floor(mf));
					bw.newLine();
					no = no + 1;
					}
			}
			bw.close();
			br.close();
			System.out.println("yes: "+ yes + " no :" + no);
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return outfile;
	}

	public static HashMap<String, Double> getMFMap(File infile){
		HashMap<String,Double> map = new HashMap<String,Double>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				String zone = tokens[0];
				Double mf = Double.parseDouble(tokens[1]);
				map.put(zone, mf);
			}
			br.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return map;
	}

}