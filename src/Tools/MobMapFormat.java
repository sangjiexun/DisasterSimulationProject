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


public class MobMapFormat {

	public static LonLat xy2lonlat(double x,double y) {
		JWposChange converter = new JWposChange(x,y,9);
		converter.XYtoLatLongJ();
		return new LonLat(converter.getY(), converter.getX());
	}	

	public static void main (String[] args) throws IOException{
		//		try{
		int i = 300;
		try{
			File output = new File ("c:/Users/yabec_000/Desktop/Fujisawa_animation.csv");
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			for (i=60; i<=14400; i=i+60){
				File input = new File ("c:/Users/yabec_000/Desktop/agent_log_Fujisawa/agent_log/agentlog_"+i+".csv");
				//			File output = new File ("c:/Users/yabec_000/Desktop/Tokyo_anime_conv/Tokyo_animation"+i+".csv");
				BufferedReader br = new BufferedReader(new FileReader(input));
				String line = br.readLine();
				while ((line=br.readLine()) != null){
					
					br.readLine();
					br.readLine();
					br.readLine();
					
					String[] tokenss = line.split(",");
					String PID = tokenss[0];
					Double x = Double.parseDouble(tokenss[1]);
					Double y = Double.parseDouble(tokenss[2]);
					LonLat point = xy2lonlat(x,y);

					int ii = i/60;
					int time =0;
					String newtime = null;
					String hour = null;
					if (47+ii <60){
						time = 47 + ii;
						newtime = String.valueOf(time);
						hour = "14";
					}
					if ((47+ ii >= 60)&&(47+ ii <= 120)){
						time = ii-13;
						newtime = String.valueOf(time);
						hour = "15";
					}
					if ((47+ ii >= 120)&&(47+ ii <= 180)){
						time = ii-73;
						newtime = String.valueOf(time);
						hour = "16";
					}
					if ((47+ ii >= 180)&&(47+ ii <= 240)){
						time = ii-133;
						newtime = String.valueOf(time);
						hour = "17";
					}
					if ((47+ ii >= 240)&&(47+ ii <= 300)){
						time = ii-193;
						newtime = String.valueOf(time);
						hour = "18";
					}
					if (47+ ii >= 300){
						time = ii-253;
						newtime = String.valueOf(time);
						hour = "19";
					}
					
					if (time==2){
						newtime = "02";
					}
					if (time==7){
						newtime = "07";
					}

					bw.write(PID + "," 
							+ "2011-03-11 " + hour + ":" + newtime + ":00 ,"
							+ point.getLat() +","
							+ point.getLon()
							);
					System.out.println(i);
					bw.newLine();
				}
				br.close();
			}
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
