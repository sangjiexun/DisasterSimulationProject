package KsymSimulation;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.obs.aggre.MeshTrafficVolume;

import com.beachstone.JWposChange;


/* made by Taka Yabe
 * October, 2014
 */

public class Simulation_after2hours {
	public static void main (String args[]){
		try{
			File onehour = new File ("C:/Users/yabec_000/Desktop/output/agent_log/agentlog_3600.csv"); //Output data of simulation at 1 hour
			File PTfile = new File ("");
			File parafile= new File ("C:/Users/..."); //Parameter File
			File results = new File ("C:/Users/...");
			int counter = 1;
			BufferedReader br1 = new BufferedReader(new FileReader(parafile));
			String varline = null;
			BufferedWriter result = new BufferedWriter(new FileWriter(results));

			while ( (varline = br1.readLine()) != null){  //for each parameter set
				String[] tokens = varline.split(",");
				double a = Double.valueOf(tokens[0]); //start time 
				double b = Double.valueOf(tokens[1]); //width of time 
				double c = Double.valueOf(tokens[2]); //cars going home
				double d = Double.valueOf(tokens[3]); //foot goes to station

				File outfile= new File ("C:/Users/yabec_000/Desktop/input/gen_agent.csv");
				try {
					BufferedReader br = new BufferedReader(new FileReader(onehour));
					BufferedWriter bw = new BufferedWriter (new FileWriter(outfile));
					String line = null;
					while ( (line = br.readLine()) != null){
						String[] token = line.split(",");
						String  PID   = token[0];
						Double nowx  = Double.valueOf(token[1]);
						Double nowy  = Double.valueOf(token[2]);
						String  way   = token[3];
						String transport = token[5];

						double time  = 0;
						Double age   = Double.valueOf(token[2]);
						Double homex = Double.valueOf(token[9]);
						Double homey = Double.valueOf(token[10]);
						Double goalx = Double.valueOf(token[11]);
						Double goaly = Double.valueOf(token[12]);

						String  goal  = null;

						//select a random number between 0~1
						Random ran = new Random();
						double dig = ran.nextDouble();
						double dig1 = ran.nextDouble();

						double dis = Math.pow(((homex - nowx)*(homex - nowx)+(homey - nowy)*(homey - nowy)),0.5)/1000;
						double disgoal = Math.pow(((goalx - nowx)*(goalx - nowx)+(goaly - nowy)*(goaly - nowy)),0.5)/1000;

						// yet to move
						if (way.equals("移動済み")){
							bw.write("100000" + "," + PID +"," + "0" +  "," + "1" +  "," + PID + "," + 1 + "," + "0");
						}
						else if (way.equals("まだ移動していない")){
							if (dig<=b){time = (60+a+dig1*30)*60;}
							else if ((dig>b)&&(dig<=b*2)){time = (60+a+dig1*30+30)*60;}
							else if ((dig>b*2)&&(dig<=b*3)){time = (60+a+dig1*30+60)*60;}
							else if ((dig>b*3)&&(dig<=b*4)){time = (60+a+dig1*30+90)*60;}
							else if ((dig>b*4)&&(dig<=b*5)){time = (60+a+dig1*30+120)*60;}
							else if ((dig>b*5)&&(dig<=b*6)){time = (60+a+dig1*30+150)*60;}
							else if ((dig>b*6)&&(dig<=b*7)){time = (60+a+dig1*30+180)*60;}
							else if ((dig>b*7)&&(dig<=b*8)){time = (60+a+dig1*30+210)*60;}
							else if ((dig>b*8)&&(dig<=b*9)){time = (60+a+dig1*30+240)*60;}

							if ((age>=1)&&(age<=4)){
								if (disgoal<=10){goal = ("a" + PID);}
								else {goal = ("0");}
							}
							else if ((age>=5)&&(age<=6)){if (dis<=15){goal = ("a" + PID);}
							else {goal = ("0");}
							}
							else if ((age>=7)&&(age<=10)){if (dis<=20){goal = ("a" + PID);}
							else {goal = ("0");}
							}
							else if ((age>=11)&&(age<=12)){if (dis<=15){goal = ("a" + PID);}
							else {goal = ("0");}
							}
							else if ((age>=13)){if (dis<=10){goal = ("a" + PID);}
							else {goal = ("0");}
							}
							int i = (int)time;
							bw.write(i + "," + PID +"," + "0" +  "," + "1" +  "," + PID + "," + 1 + "," + goal);
							bw.newLine();
						}
						if (way.equals("移動中")){
							time = 0;
							if ((age>=1)&&(age<=4)){
								if (disgoal<=10){goal = ("a" + PID);}
								else {goal = ("0");}
							}
							else if ((age>=5)&&(age<=6)){if (dis<=15){goal = ("a" + PID);}
							else {goal = ("0");}
							}
							else if ((age>=7)&&(age<=10)){if (dis<=20){goal = ("a" + PID);}
							else {goal = ("0");}
							}
							else if ((age>=11)&&(age<=12)){if (dis<=15){goal = ("a" + PID);}
							else {goal = ("0");}
							}
							else if ((age>=13)){if (dis<=10){goal = ("a" + PID);}
							else {goal = ("0");}
							}
							bw.write(time + "," + PID +"," + "0" +  "," + "1" +  "," + PID + "," + 1 + "," + goal);
							bw.newLine();
						}
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

				//				"Kashiyama Simulator"
				//				System.out.println("start ew-macs!");
				//				//run 140807ew-macs.exe
				//				ProcessBuilder pb = new ProcessBuilder("c:/Users/yabec_000/Desktop/140916nw-macs.exe",
				//						"c:/Users/yabec_000/Desktop/input", "c:/Users/yabec_000/Desktop/output");
				//				pb.inheritIO();
				//				try {
				//					Process process = pb.start();
				//					process.waitFor();
				//					System.out.println(pb.redirectInput());
				//				}
				//				catch (Exception ex) {
				//					ex.printStackTrace();
				//				}
				//				System.out.println("done ew-macs!");

				ArrayList<String> meshlist = new ArrayList<String>();
				File meshfile = new File("c:/Users/..."); //meshcode file, preferably mesh level 5
				try{
					BufferedReader meshreader = new BufferedReader(new FileReader(meshfile));
					String line = null;
					while((line=meshreader.readLine())!= null){
						String meshcode = line;
						meshlist.add(meshcode);
					}
					meshreader.close();
				}
				catch(FileNotFoundException z) {
					System.out.println("File not found 3");
				}
				catch(IOException e) {
					System.out.println(e);
				}

				// load files and aggregate 
				MeshTrafficVolume volume = new MeshTrafficVolume(5);	 // mesh level=5

				File indir1h = new File("c:/Users/yabec_000/Desktop/output/agent_log/agentlog_7200.csv"); //1 hour later

				//aggregate simulation result
				try{
					BufferedReader brmesh = new BufferedReader(new FileReader(indir1h));
					String line = brmesh.readLine();
					while( (line = brmesh.readLine()) != null ) {
						String[] tokenss = line.split(",");
						String pid = tokenss[0];
						double x = Double.parseDouble(tokenss[1]);
						double y = Double.parseDouble(tokenss[2]);
						LonLat pos = xy2lonlat(x,y);
						volume.aggregate(pid,0,pos,1,1);
					}
					brmesh.close();
					volume.export(new File("C:/Users/yabec_000/Desktop/outputfiles/outputmesh" + counter + "_2h.csv"));
				}
				catch(FileNotFoundException e) {
					System.out.println("File not found 2");
				}
				catch(IOException e) {
					System.out.println(e);
				}

				//gather zdc and PT results
				Map<String,Integer> ptmap1h = new HashMap<String, Integer>();
				Map<String,Integer> zdcmap1h = new HashMap<String, Integer>();
				ArrayList<Double> RMSElist = new ArrayList<Double>();

				File zdcfile = new File("C:/Users/yabec_000/Desktop/TokyoGPS_1647_smooth.csv");
				File ptfile =  new File("C:/Users/yabec_000/Desktop/outputfiles/outputmesh" + counter + "_2h.csv");
				File datafile =  new File("C:/Users/yabec_000/Desktop/outputfiles/outputcor" + counter + "_2h.csv");
				try{
					BufferedReader br3 = new BufferedReader(new FileReader(zdcfile));
					String line = br3.readLine();
					while( (line = br3.readLine()) != null ) {
						String[] zdctokens = line.split(",");
						String meshcode = zdctokens[0];
						Integer count   = Integer.valueOf(zdctokens[1]);
						zdcmap1h.put(meshcode, count);
					}
					br3.close();

					BufferedReader br4 = new BufferedReader(new FileReader(ptfile));
					String line4 = br4.readLine();
					while( (line4 = br4.readLine()) != null ) {
						String[] pttokens = line4.split("\t");
						String meshcodes = pttokens[0];
						Integer counts   = Integer.valueOf(pttokens[1]);
						ptmap1h.put(meshcodes, counts);
					}
					br4.close();
				}
				catch(FileNotFoundException z) {
					System.out.println("File not found 3");
				}
				catch(IOException e) {
					System.out.println(e);
				}

				BufferedWriter bw5 = new BufferedWriter(new FileWriter(datafile));
				for(String mc:meshlist){

					Mesh  mesh     = new Mesh(mc);
					Rectangle2D.Double rect = mesh.getRect();
					String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
							rect.getMinX(),rect.getMaxY(),
							rect.getMaxX(),rect.getMaxY(),
							rect.getMaxX(),rect.getMinY(),
							rect.getMinX(),rect.getMinY());
					double countpt = 0d;
					double countds = 0d;

					if(ptmap1h.containsKey(mc)){countpt = ptmap1h.get(mc);}
					if(zdcmap1h.containsKey(mc)){countds = zdcmap1h.get(mc);}
					double diff = (countpt - countds);

					bw5.write(mc + "\t" + countpt +"\t" + countds +"\t" + diff + "\t"+ wkt);
					bw5.newLine();

					double temp = Math.pow(diff,2);
					RMSElist.add(temp);
				}
				bw5.close();

				double sum = 0d;
				for (double num : RMSElist){
					sum += num;			
				}
				double RMSE = Math.pow(sum / meshlist.size(), 0.5);
				Integer rmse = (int)(RMSE);
				result.write(rmse);
				result.newLine();

				System.out.println(counter);
				counter = counter + 1 ;

			} // until here for each parameter set
			br1.close();
			result.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 5");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
	}
	public static LonLat xy2lonlat(double x,double y) {
		JWposChange converter = new JWposChange(x,y,9);
		converter.XYtoLatLongJ();
		return new LonLat(converter.getY(), converter.getX());
	}
}
