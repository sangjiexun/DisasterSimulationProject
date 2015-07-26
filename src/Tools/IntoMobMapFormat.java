package Tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import jp.ac.ut.csis.pflow.geom.LonLat;

import com.beachstone.JWposChange;


public class IntoMobMapFormat {

	public static void main(String args[]){
		int i;
		for (i=1; i<=240; i++){
			int time = i*60;
			File infile = new File ("C:/Users/yabec_000/Desktop/agent_log_Fujisawa/agent_log/agentlog_" + time + ".csv");
			File outfile= new File ("C:/Users/yabec_000/Desktop/agent_log_Fujisawa/agent_log/agentlog_modified.csv");
//			File infile2 = new File ("C:/Users/yabec_000/Desktop/Research/"
//					+ "agent_log_Fujisawa/agent_log/agentlog_" + time + ".csv");
//			File outfile2 = new File ("C:/Users/yabec_000/Desktop/Research/"
//					+ "agent_log_Fujisawa/agent_log/agentlog_modified.csv");

			modifyFile(infile, outfile, i);
//			modifyFile(infile2, outfile2, i);
			System.out.println(i);
		}
	}

	public static File modifyFile(File infile, File outfile, int i){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile,true));
			String line = br.readLine();
			while((line = br.readLine()) != null){
				line = br.readLine();
				line = br.readLine();
				line = br.readLine();


				String tokens[] = line.split(",");
				String pid = tokens[0];
				Double x = Double.parseDouble(tokens[1]);
				Double y = Double.parseDouble(tokens[2]);
				LonLat place = xy2lonlat(x,y);
				Double lon = place.getLon();
				Double lat = place.getLat();
				int hour = gethour(i);
				int minute = getminute(i);

				bw.write(pid + "," + lon + "," + lat + "," + 
						"2011-03-11 "+ hour + ":" + minute + ":00");
				bw.newLine();

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

	public static LonLat xy2lonlat(double x,double y) {
		JWposChange converter = new JWposChange(x,y,9);
		converter.XYtoLatLongJ();
		return new LonLat(converter.getY(), converter.getX());
	}

	public static int gethour(int i){
		int hour;
		if(i <= 12){
			hour = 14;
		}
		else if(i>=13 && i<= 72){
			hour = 15;
		}
		else if(i>=73 && i<= 132){
			hour = 16;
		}
		else if(i>=133 && i<= 192){
			hour = 17;
		}
		else if(i>=193 && i<= 252){
			hour = 18;
		}
		else if(i>=253 && i<= 312){
			hour = 19;
		}
		else if(i>=313 && i<= 372){
			hour = 20;
		}
		else{
			hour=0;
		}
		return hour;
	}
	public static int getminute(int i){
		int minute;
		if(i <= 12){
			minute = 47 + i;
		}
		else if(i>=13 && i<= 72){
			minute = i-13;
		}
		else if(i>=73 && i<= 132){
			minute = i-73;
		}
		else if(i>=133 && i<= 192){
			minute = i-133;
		}
		else if(i>=193 && i<= 252){
			minute = i-193;
		}
		else if(i>=253 && i<= 312){
			minute = i-253;
		}
		else if(i>=313 && i<= 372){
			minute = i-313;
		}
		else{
			minute =0;
		}
		return minute;
	}
}
