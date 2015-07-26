package Tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class RMSE {
	public static void main (String args[]){
		//		for (int x=1; x <=9 ; x++){
		File infile = new File("c:/Users/yabec_000/Desktop/Tokyo_2/outputfiles/outputcor1_5h_rev3.csv");
		File outfile = new File("c:/Users/yabec_000/Desktop/Tokyo_2/outputfiles/outputcor1_5h_rev4.csv");
		ArrayList<Double> list = new ArrayList<Double>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			int count = 0;

			while ( (line = br.readLine()) != null ){
				String tokens[] = line.split(",");
				double a = Double.parseDouble(tokens[0]);
				double b = Double.parseDouble(tokens[1]);
				double d = Math.pow((a-b),2);
				list.add(d);
				count = count + 1;
			}
			br.close();
			bw.close();

			double sum = 0d;
			for (double num : list){
				sum += num;

			}
			double RMSE = (sum / count);
			System.out.println(RMSE);
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
}
//}
