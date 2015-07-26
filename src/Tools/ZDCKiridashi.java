package Tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ZDCKiridashi {

	public static void main (String[] args) throws IOException{

		//		HashSet<String> set = new HashSet<String>();

		for(int i=0; i<=23; i++){

			try{
				File input = new File ("c:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110310_fujisawa_tokyo_5.csv");
				File output = new File ("c:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110310_5_"+ i +".csv");
				BufferedReader br = new BufferedReader(new FileReader(input));
				BufferedWriter bw = new BufferedWriter(new FileWriter(output));
				String line = br.readLine();

				while ((line=br.readLine()) != null){
					String[] tokenss = line.split("\t");
					String meshcode = tokenss[0];
					int hour = Integer.valueOf(tokenss[1]);
					double pop = Double.valueOf(tokenss[2]);

					if(hour==i){
						bw.write(meshcode + "\t" + pop);
						bw.newLine();
					}
				}
				br.close();
				bw.close();
			}
			catch(FileNotFoundException e) {
				System.out.println("File not found" + e.getLocalizedMessage());
				e.printStackTrace();
			}
			catch(IOException e) {
				System.out.println(e);
			}
		}
	}
}