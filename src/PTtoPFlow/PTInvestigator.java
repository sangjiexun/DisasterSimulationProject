package PTtoPFlow;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class PTInvestigator {
	public static void main(String args[]) {

		File infile = new File ("C:/Users/yabec_000/Desktop/Fujisawa_PT.txt");
		File outfile= new File ("C:/Users/yabec_000/Desktop/FPTtransport.csv");
		try {
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter (new FileWriter(outfile));
			String line = null;

			int walk = 0;
			int vehicle = 0;
			int publictrans = 0;
			int stay = 0;
			int others = 0;

			while ( (line = br.readLine()) != null){

				String[] token = line.split(",");
				String way = token[6];

				if ((way.equals("1"))||(way.equals("2"))){
					walk = walk + 1;
				}
				else if((way.equals("3"))||(way.equals("4"))||(way.equals("5"))||
						(way.equals("6"))||(way.equals("7"))||(way.equals("8"))
						||(way.equals("9"))) {
					vehicle = vehicle + 1;
				}
				else if((way.equals("10"))||(way.equals("11"))||(way.equals("12"))||
						(way.equals("13"))||(way.equals("14"))||(way.equals("15"))) {
					publictrans = publictrans + 1;
				}
				else if ((way.equals("97"))){
					stay = stay + 1;
				}
				else {
					others = others + 1;
				}
				//				br.readLine();
				//				br.readLine();
				//				br.readLine();
				//				
				//				String[] token = line.split(",");
				//				String PID   = token[0];
				//				
				//				String age = token[1];
				//				String way = token[2];
				//				String dis = token[3];
				//				String disgoal = token[4];

				//				Double age   = Double.valueOf(token[2]);
				//				String  way   = token[6];
				//				Double nowx  = Double.valueOf(token[7]);
				//				Double nowy  = Double.valueOf(token[8]);
				//				Double homex = Double.valueOf(token[9]);
				//				Double homey = Double.valueOf(token[10]);
				//				Double goalx = Double.valueOf(token[11]);
				//				Double goaly = Double.valueOf(token[12]);
				//				double dis = Math.pow(((homex - nowx)*(homex - nowx)+(homey - nowy)*(homey - nowy)),0.5)/1000;
				//				double disgoal = Math.pow(((goalx - nowx)*(goalx - nowx)+(goaly - nowy)*(goaly - nowy)),0.5)/1000;
				//				bw.write(PID + "," + age + "," + way + "," + dis + "," + disgoal);
			}
			bw.write(walk + "," + vehicle +"," + publictrans +"," + stay +"," + others);
			br.close();
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
	}
}
