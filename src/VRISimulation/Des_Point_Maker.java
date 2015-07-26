package VRISimulation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Des_Point_Maker {

	public static void main(String[] args){
		File infile = new File ("C:/Users/yabec_000/Desktop/Tokyo_PT_small.txt");
		File outfile= new File ("C:/Users/yabec_000/Desktop/input/destination_point.csv");

		try {
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter (new FileWriter(outfile));
			String line = null;
			while ( (line = br.readLine()) != null){

				String[] tokens = line.split(",");
				String PID = tokens[0];
				String homelocx = tokens[10];
				String homelocy = tokens[9];
				String goallocx = tokens[12];
				String goallocy = tokens[11];

				bw.write(PID + "," + homelocx +","+ homelocy +","+ goallocx +","+ goallocy);
				bw.newLine();
			}
			br.close();
			bw.close();
		}
		catch(FileNotFoundException a) {
			System.out.println("File not found:");
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
}
