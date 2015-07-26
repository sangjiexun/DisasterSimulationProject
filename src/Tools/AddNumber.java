package Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AddNumber {

	public static void main(String args[]){
		File infile = new File ("c:/Users/yabetaka/Desktop/PTAnalyze/GoHomeTime2.csv");
		
		for(int i=0; i<=23; i++){
			System.out.println(i + "," + AggregateNumber(infile, i));
		}
	}

	public static int AggregateNumber(File infile, int num){
		int i = 0;
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				int hour = Integer.parseInt(tokens[1]); 
				if(hour==num){
					i++;
				}
			}
			br.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return i;
	}
}
