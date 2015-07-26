package VRISimulation;
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
import java.util.TreeSet;
import java.util.Vector;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.obs.aggre.MeshTrafficVolume;
import Tools.Correlation;

import com.beachstone.JWposChange;

public class Simulate_Fujisawa_7200 {

	public static void main(String args[]) {

		File infile = new File ("C:/Users/Administrator/Desktop/Fujisawa_1447_ver3_xy_half.txt");
		File varfile= new File ("C:/Users/Administrator/Desktop/Scenario_parameters_x.csv");
		//		File meshcor= new File ("C:/Users/Administrator/Desktop/corfile.txt");
		//		HashMap<String, Double> result = new HashMap<String, Double>();

		int counter = 1;

		try{
			BufferedReader br1 = new BufferedReader(new FileReader(varfile));
			String varline = null;

			while ( (varline = br1.readLine()) != null){  //for each parameter set
				String[] tokens = varline.split(",");
				double a = Double.valueOf(tokens[0]);//帰宅意思率にかけるパラメータ(1,0.9,...)
				double b = Double.valueOf(tokens[1]);//帰宅しない人がgoalに向かうかの閾値(km)
				double c = Double.valueOf(tokens[2]);//homeにもgoalにもいかない人が駅に行くか、避難所に行くか
				double d = Double.valueOf(tokens[3]);//移動開始時間の分布の変化(1.5, 1.25, 1, 0.75, 0.5)

				File outfile= new File ("C:/Users/Administrator/Desktop/input/gen_agent.csv");
				try {
					BufferedReader br = new BufferedReader(new FileReader(infile));
					BufferedWriter bw = new BufferedWriter (new FileWriter(outfile));
					String line = null;
					while ( (line = br.readLine()) != null){

						String[] token = line.split(",");
						String  PID   = token[0];
						double time  = 0;
						Double age   = Double.valueOf(token[2]);
						Double nowx  = Double.valueOf(token[7]);
						Double nowy  = Double.valueOf(token[8]);
						Double homex = Double.valueOf(token[9]);
						Double homey = Double.valueOf(token[10]);
						Double goalx = Double.valueOf(token[11]);
						Double goaly = Double.valueOf(token[12]);
						String  way   = token[6];
						String  goal  = null;

						//select a random number between 0~1
						Random ran = new Random();
						double dig = ran.nextDouble();
						double dig1 = ran.nextDouble();

						double dis = Math.pow(((homex - nowx)*(homex - nowx)+(homey - nowy)*(homey - nowy)),0.5)/1000;
						double disgoal = Math.pow(((goalx - nowx)*(goalx - nowx)+(goaly - nowy)*(goaly - nowy)),0.5)/1000;

						if (dig<=0.025*d){time = 0;}
						if ((dig>0.025*d)&&(dig<=0.10*d)){time = dig1*30*60;}
						if ((dig>0.10*d)&&(dig<=0.25*d)){time = (30 + dig1*30)*60;}
						if ((dig>0.25*d)&&(dig<=0.45*d)){time = (60 + dig1*30)*60;}
						if ((dig>0.45*d)&&(dig<=0.65*d)){time = (90 + dig1*30)*60;}
						if ((dig>0.65*d)&&(dig<=0.80*d)){time = (120 + dig1*30)*60;}
						if ((dig>0.80*d)&&(dig<=0.90*d)){time = (150 + dig1*30)*60;}
						if ((dig>0.90*d)&&(dig<=0.95*d)){time = (180 + dig1*30)*60;}
						if (dig>0.95*d){time = (210 + dig1*30)*60;}

						// walk or stay
						if ((way.equals("1")) || (way.equals("97"))){
							if ((age>=1)&&(age<=4)){
								if (dis<=5){
									if (dig <= 0.75*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.85*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){goal = ("b" + PID);}
								if ((dis>15)&&(dis<=20)){goal = ("b" + PID);}
								if ((dis>20)){goal = ("b" + PID);}
							}
							if ((age>=5)&&(age<=6)){
								if (dis<=5){
									if (dig <= 0.85*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.7*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.55*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.55*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.25*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.25*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							if ((age>=7)&&(age<=8)){
								if (dis<=5){
									if (dig <= 0.825*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.65*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.25*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.25*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.15*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.15*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							if ((age>=9)&&(age<=10)){
								if (dis<=5){
									if (dig <= 0.825*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.75*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.60*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.6*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.3*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.3*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							if ((age>=11)&&(age<=12)){
								if (dis<=5){
									if (dig <= 0.85*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.8*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.6*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.6*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.10*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.1*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							if ((age>=13)){
								if (dis<=5){
									if (dig <= 0.85*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.8*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.45*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= (0.45*a + c))){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.10*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else if ((disgoal > b)&&(dig <= 0.10*a+c)){goal = ("0");}
									else {goal = "1";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							int i = (int)time;
							bw.write(i + "," + PID +"," + "0" +  "," + "1" +  "," + PID + "," + 1 + "," + goal);
							bw.newLine();
						}

						//car ... 100% go home と仮定
						else if ((way.equals("2"))||(way.equals("3"))||(way.equals("4"))||(way.equals("5"))||
								(way.equals("6"))||(way.equals("7"))||(way.equals("8"))){
							if (dig < 0.5){
								goal = ("a"+PID);
								}
							else{
								goal = ("b"+ PID);
							}
							double timecar = dig*30;
							int i = (int)timecar;
							bw.write(i + "," + PID +"," + "4" +  "," + "1" +  "," + PID + "," + 1 + "," + goal);
							bw.newLine();
						}

						// train どっち向かうか office or home, near one by walk
						else{
							if ((age>=1)&&(age<=4)){
								if (dis<=5){
									if (dig <= 0.75*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.85*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){goal = ("b" + PID);}
								if ((dis>15)&&(dis<=20)){goal = ("b" + PID);}
								if ((dis>20)){goal = ("b" + PID);}
							}
							if ((age>=5)&&(age<=6)){
								if (dis<=5){
									if (dig <= 0.85*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.7*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.55*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.25*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							if ((age>=7)&&(age<=8)){
								if (dis<=5){
									if (dig <= 0.825*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.65*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.25*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>15)&&(dis<=20)){
									if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							if ((age>=9)&&(age<=10)){
								if (dis<=5){
									if (dig <= 0.825*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.75*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.60*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.3*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							if ((age>=11)&&(age<=12)){
								if (dis<=5){
									if (dig <= 0.85*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.8*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.6*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.10*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							if ((age>=13)){
								if (dis<=5){
									if (dig <= 0.85*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>5)&&(dis<=10)){
									if (dig <= 0.8*a){goal = ("a" + PID);}
									else {goal = ("b" + PID);}
								}
								if ((dis>10)&&(dis<=15)){
									if (dig <= 0.45*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = "0";}
								}
								if ((dis>15)&&(dis<=20)){
									if (dig <= 0.10*a){goal = ("a" + PID);}
									else if (disgoal <= b){goal = ("b" + PID);}
									else {goal = ("0");}
								}
								if ((dis>20)){
									if (disgoal <= c){goal = ("b" + PID);}
									else {goal = ("0");}
								}
							}
							int i = (int)time;
							bw.write(i + "," + PID +"," + "0" +  "," + "1" +  "," + PID + "," + 1 + "," + goal);
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

				System.out.println("start ew-macs!");
				//run 140807ew-macs.exe
				ProcessBuilder pb = new ProcessBuilder("c:/Users/Administrator/Desktop/140807nw-macs.exe",
						"c:/Users/Administrator/Desktop/input", "c:/Users/Administrator/Desktop/output");
				pb.inheritIO();
				try {
					Process process = pb.start();
					process.waitFor();
					System.out.println(pb.redirectInput());
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("done ew-macs!");

				ArrayList<String> meshlist = new ArrayList<String>();
				File meshfile = new File("c:/Users/Administrator/Desktop/meshcodes.csv");
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

				//結果をメッシュ集計
				MeshTrafficVolume volume = new MeshTrafficVolume(4);	 // mesh level=4
				// load files and aggregate traffic counts ////////

				File indir2h = new File("c:/Users/Administrator/Desktop/output/agent_log/agentlog_7200.csv");

				try{
					BufferedReader brmesh = new BufferedReader(new FileReader(indir2h));
					String line2 = brmesh.readLine();
					while( (line2 = brmesh.readLine()) != null ) {
						String[] tokenss = line2.split(",");
						String pid = tokenss[0];
						double x = Double.parseDouble(tokenss[1]);
						double y = Double.parseDouble(tokenss[2]);
						LonLat pos = xy2lonlat(x,y);
						volume.aggregate(pid,0,pos,1,1);
					}
					brmesh.close();
					volume.export(new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_2h.csv"));
				}
				catch(FileNotFoundException e) {
					System.out.println("File not found 2");
				}
				catch(IOException e) {
					System.out.println(e);
				}

				//観測データと比較
				Map<String,Integer> ptmap2h = new HashMap<String, Integer>();
				Map<String,Integer> zdcmap2h = new HashMap<String, Integer>();

				File zdcfile2h = new File("C:/Users/Administrator/Desktop/gps_1647_smoothed.csv");
				File ptfile2h =  new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_2h.csv");
				File datafile2h =  new File("C:/Users/Administrator/Desktop/outputfiles/outputcor" + counter + "_2h.csv");
				String temp2h   = ("C:/Users/Administrator/Desktop/outputfiles/temp" + counter + "_2h.csv");
				try{
					BufferedReader br32h = new BufferedReader(new FileReader(zdcfile2h));
					String line = br32h.readLine();
					while( (line = br32h.readLine()) != null ) {
						String[] zdctokens = line.split(",");
						String meshcode = zdctokens[0];
						Integer count   = Integer.valueOf(zdctokens[1]);
						zdcmap2h.put(meshcode, count);
						//						meshcodeset.add(meshcode);
					}
					br32h.close();

					BufferedReader br4 = new BufferedReader(new FileReader(ptfile2h));
					String line4 = br4.readLine();
					while( (line4 = br4.readLine()) != null ) {
						String[] pttokens = line4.split("\t");
						String meshcodes = pttokens[0];
						Integer counts   = Integer.valueOf(pttokens[1]);

						ptmap2h.put(meshcodes, counts);
						//						meshcodeset.add(meshcodes);
					}
					br4.close();
				}
				catch(FileNotFoundException z) {
					System.out.println("File not found 3");
				}
				catch(IOException e) {
					System.out.println(e);
				}

				BufferedWriter bw2h = new BufferedWriter(new FileWriter(datafile2h));
				BufferedWriter tempwriter2h = new BufferedWriter(new FileWriter(temp2h));
				for(String mc:meshlist){

					Mesh  mesh     = new Mesh(mc);
					Rectangle2D.Double rect = mesh.getRect();
					String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
							rect.getMinX(),rect.getMaxY(),
							rect.getMaxX(),rect.getMaxY(),
							rect.getMaxX(),rect.getMinY(),
							rect.getMinX(),rect.getMinY());
					int countpt = 0;
					int countds = 0;

					if(ptmap2h.containsKey(mc)){countpt = ptmap2h.get(mc);}
					if(zdcmap2h.containsKey(mc)){countds = zdcmap2h.get(mc);}
					int diff = (countpt - countds);

					bw2h.write(mc + "," + countpt +"," + countds +"," + diff + ","+ wkt);
					bw2h.newLine();
					tempwriter2h.write(countpt + "," + countds);
					tempwriter2h.newLine();
				}
				bw2h.close();
				tempwriter2h.close();

				HashMap<String, Double> result2 = new HashMap<String, Double>();
				//観測データとの相関を計算
				Vector v12h = new Vector();
				Vector v22h = new Vector();
				Correlation.readTextFromFile_AndSetVector(temp2h,v12h);
				Correlation.KataHenkan(v12h,v22h);
				double cor2h  = Correlation.getCorrelationCoefficient(v22h);
				System.out.println(cor2h);
				String number2h = (String.valueOf(counter)+ "2h") ;
				result2.put(number2h, cor2h);

				BufferedWriter r2h = new BufferedWriter(new FileWriter("c:/Users/Administrator/Desktop/result_2h" + counter + ".csv"));
				TreeSet<String> sortedKey2 = new TreeSet<String>(result2.keySet());
				for (String key: sortedKey2){
					Double value = result2.get(key);
					r2h.write(key +","+ value);
					r2h.newLine();
				}
				r2h.close();

				System.out.println(counter);
				counter = counter + 1 ;

			} // until here for each parameter set
			br1.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 4");
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
