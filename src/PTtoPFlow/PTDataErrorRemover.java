package PTtoPFlow;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class PTDataErrorRemover {

	public static void main(String[] args) throws Exception {

		File infile = new File("C:/Users/yabec_000/Desktop/test.txt");
		File zonefile = new File("C:/Users/yabec_000/Documents/AllMyWork/SekimotoLab/FujisawaProject/Fundamental files/08tokyoPT-zone-point.csv");
		String filepath= "c:/Users/yabec_000/Desktop/ErrorZoneCodes.txt";

		ArrayList<String> ptzone = new ArrayList<String>();
		HashSet<String> errors = new HashSet<String>();

		BufferedReader br1 = new BufferedReader (new FileReader(zonefile));
		String zoneline = null;
		while ( (zoneline= br1.readLine()) != null ) {
			String zonecode[] = zoneline.split(",");
			String zoneID = zonecode[0];
			ptzone.add(zoneID);
		}
		br1.close();

		try {
			BufferedReader br = new BufferedReader (new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
			String line = null;
			while( (line= br.readLine()) != null ) {
				String homezone = line.substring(3,8);
				if(ptzone.contains(homezone)){continue;}
				else {
					errors.add(homezone);
				}
				String goalzone = line.substring(47,52);
				if(ptzone.contains(goalzone)){continue;}
				else {
					errors.add(goalzone);
				}
			}
			for ( String elem : errors ) {
				bw.write(elem);
				bw.newLine();
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found: " + filepath);
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
}
