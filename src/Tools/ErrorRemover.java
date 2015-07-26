package Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorRemover {

	public static void main(String args[]){
		File infile = new File("C:/Users/yabec_000/Desktop/Tokyo_PTver3.csv");
		File outfile = new File("C:/Users/yabec_000/Desktop/Tokyo_PTver3_THISISIT.csv");
		
		changeNum(infile,outfile);
	}

	public static File changeNum(File infile, File outfile){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			int counter = 1;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
//				Integer pid = Integer.valueOf(tokens[0]);
				String id = tokens[1];
				String mf = tokens[2];
				String sex = tokens[3];
				String age = tokens[4];
				String trans = tokens[5];
				String nowx = tokens[6];
				String nowy = tokens[7];
				String homex = tokens[8];
				String homey = tokens[9];
				String goalx = tokens[10];
				String goaly = tokens[11];

				bw.write(counter + "," +id + "," + mf + "," + sex + "," + age + "," +  
						age + "," + trans + "," + nowx + "," + nowy + "," + homex + "," + homey + "," + 
						goalx + "," + goaly);
				bw.newLine();
				counter = counter + 1;
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

	public static File removeError(File infile, File outfile){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			int error = 0;
			String line = null;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(",");
				Double nowx = Double.parseDouble(tokens[6]);
				Double homex = Double.parseDouble(tokens[8]);
				Double goalx = Double.parseDouble(tokens[10]);
				if((nowx != 0)&&(homex!=0)&&(goalx!=0)){
					bw.write(line);
					bw.newLine();
				}
				else{
					error = error + 1;
				}
			}
			br.close();
			bw.close();
			System.out.println(error);
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return outfile;
	}

	public static File combineFiles(File infile1, File infile2, File outfile){
		try{
			BufferedReader br1 = new BufferedReader(new FileReader(infile1));
			BufferedReader br2 = new BufferedReader(new FileReader(infile2));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
			String line1 = null;
			while ((line1 = br1.readLine()) != null){
				bw.write(line1);
				bw.newLine();
			}
			String line2 = null;
			while ((line2 = br2.readLine()) != null){
				bw.write(line2);
				bw.newLine();
			}
			br1.close();
			br2.close();
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
