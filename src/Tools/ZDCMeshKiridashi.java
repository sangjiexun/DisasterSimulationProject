package Tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class ZDCMeshKiridashi {

	public static void main (String[] args) throws IOException{

		//		HashSet<String> set = new HashSet<String>();

		for(int i=0; i<=46; i++){

			int date;
			int ii;
			if (i<=23){
				date = 11;
				ii = i;
			}
			else {
				date = 12;
				ii = i-23;
			}
			try{
				File input = new File ("c:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_201103" +date+ "_5_"+ ii +".csv");
				BufferedReader br = new BufferedReader(new FileReader(input));
				String line = null;

				while ((line=br.readLine()) != null){
					String[] tokenss = line.split("\t");
					String meshcode = tokenss[0];
					double pop = Double.valueOf(tokenss[1]);

					if(meshcode.equals("5339560641")){
						//System.out.println(meshcode + "," + i + "," + pop);
						System.out.println(pop);
					}
				}
				br.close();
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