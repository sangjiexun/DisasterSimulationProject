package Tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class ZDCPopAdjuster {

	public static void main(String args[]){

		File zdc = new File ("c:/Users/yabec_000/Desktop/.txt");
		File adjzdc = new File ("c:/Users/yabec_000/Desktop/Fujisawa_PT_animation.csv");
		
		PopAdjuster(zdc,adjzdc,1);
	
	}
	
	public static File PopAdjuster(File zdcfile, File adjzdcfile, int population){
		try{
			BufferedReader br = new BufferedReader(new FileReader(zdcfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(adjzdcfile));
			ArrayList<Integer> pops = new ArrayList<Integer>();
			
			String line = null;
			while ((line=br.readLine()) != null){
				String[] tokens = line.split("/t");
				Integer pop = (int)Math.floor(Double.parseDouble(tokens[2]));
				pops.add(pop);
			}
			int sum = 0;
			for (Integer p:pops){
				sum = sum + p;
			}
			while ((line=br.readLine()) != null){
				String[] tokens = line.split("/t");
				Integer pop = Integer.getInteger(tokens[1]);
				double adjpop = pop * population / sum;
				bw.write(tokens[0] + "," + adjpop);
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
		return adjzdcfile;
	}
}
