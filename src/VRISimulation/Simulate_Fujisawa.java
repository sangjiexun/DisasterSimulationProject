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

public class Simulate_Fujisawa {

	public static void main(String args[]) {

		File infile = new File ("C:/Users/Administrator/Desktop/Fujisawa_PT_small.txt");
		//		File varfile= new File ("C:/Users/Administrator/Desktop/parameters_x.csv");
		//		File meshcor= new File ("C:/Users/Administrator/Desktop/corfile.txt");

		int counter = 1;

		try{
			//			BufferedReader br1 = new BufferedReader(new FileReader(varfile));
			//			String varline = null;
			//
			//			while ( (varline = br1.readLine()) != null){  //for each parameter set
			//				String[] tokens = varline.split(",");
			//				double a = Double.valueOf(tokens[0]); //
			//				double b = Double.valueOf(tokens[1]); //
			//				double c = Double.valueOf(tokens[2]); //
			//				double d = Double.valueOf(tokens[3]); //

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

					if (dig<=0){time = (0+dig1*60)*60;}
					else if ((dig>0)&&(dig<=0.45)){time = (dig1*60+60)*60;}
					else if ((dig>0.45)&&(dig<=0.55)){time = (dig1*60+120)*60;}
					else if ((dig>0.55)&&(dig<=0.82)){time = (dig1*60+180)*60;}
					else if ((dig>0.82)&&(dig<=1.00)){time = (dig1*60+240)*60;}

					// walk or stay
					if ((way.equals("1")) || (way.equals("97"))){
						if ((age>=1)&&(age<=4)){
							if (dis<=5){
								if (dig <= 0.75){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.85){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){goal = ("b" + PID);}
							if ((dis>15)&&(dis<=20)){goal = ("b" + PID);}
							if ((dis>20)){goal = ("b" + PID);}
						}
						if ((age>=5)&&(age<=6)){
							if (dis<=5){
								if (dig <= 0.85){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.7){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.55){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else{goal = ("0");}

							}
							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.25){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}

							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						if ((age>=7)&&(age<=8)){
							if (dis<=5){
								if (dig <= 0.825){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.65){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.25){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.15){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						if ((age>=9)&&(age<=10)){
							if (dis<=5){
								if (dig <= 0.825){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.75){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.60){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}								

							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.3){goal = ("a" + PID);}
								else if (disgoal <= 10 ){goal = ("b" + PID);}
								else {goal = ("0");}
							}
							if ((dis>20)){
								if (disgoal <=10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						if ((age>=11)&&(age<=12)){
							if (dis<=5){
								if (dig <= 0.85){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.8){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.6){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.10){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						if ((age>=13)){
							if (dis<=5){
								if (dig <= 0.85){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.8){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.45){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.10){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						int i = (int)time;
						bw.write(i + "," + PID +"," + "0" +  "," + "1" +  "," + PID + "," + 1 + "," + goal);
						bw.newLine();
					}

					//car ... 100% go home 縺ｨ莉ｮ螳�
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

					// train 縺ｩ縺｣縺｡蜷代°縺�° office or home, near one by walk
					else{
						if ((age>=1)&&(age<=4)){
							if (dis<=5){
								if (dig <= 0.75){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.85){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){goal = ("b" + PID);}
							if ((dis>15)&&(dis<=20)){goal = ("b" + PID);}
							if ((dis>20)){goal = ("b" + PID);}
						}
						if ((age>=5)&&(age<=6)){
							if (dis<=5){
								if (dig <= 0.85){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.7){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.55){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.25){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						if ((age>=7)&&(age<=8)){
							if (dis<=5){
								if (dig <= 0.825){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.65){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.25){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>15)&&(dis<=20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						if ((age>=9)&&(age<=10)){
							if (dis<=5){
								if (dig <= 0.825){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.75){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.60){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.3){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						if ((age>=11)&&(age<=12)){
							if (dis<=5){
								if (dig <= 0.85){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.8){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.6){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.10){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
						}
						else{
							if (dis<=5){
								if (dig <= 0.85){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>5)&&(dis<=10)){
								if (dig <= 0.8){goal = ("a" + PID);}
								else {goal = ("b" + PID);}
							}
							if ((dis>10)&&(dis<=15)){
								if (dig <= 0.45){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = "0";}
							}
							if ((dis>15)&&(dis<=20)){
								if (dig <= 0.10){goal = ("a" + PID);}
								else if (disgoal <= 10){goal = ("b" + PID);}
								else {goal = ("0");}
							}
							if ((dis>20)){
								if (disgoal <= 10){goal = ("b" + PID);}
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
			ProcessBuilder pb = new ProcessBuilder("c:/Users/Administrator/Desktop/140916nw-macs.exe",
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
			File meshfile = new File("c:/Users/Administrator/Desktop/Tokyo3wards_meshcodes_4.csv");
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

			//邨先棡繧偵Γ繝�す繝･髮�ｨ�
			MeshTrafficVolume volume = new MeshTrafficVolume(4);	 // mesh level=4
			// load files and aggregate traffic counts ////////

			File indir1h = new File("c:/Users/Administrator/Desktop/output/agent_log/agentlog_3600.csv");

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
				volume.export(new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_1h.csv"));
			}
			catch(FileNotFoundException e) {
				System.out.println("File not found 2");
			}
			catch(IOException e) {
				System.out.println(e);
			}

			//隕ｳ貂ｬ繝��繧ｿ縺ｨ豈碑ｼ�
			Map<String,Integer> ptmap1h = new HashMap<String, Integer>();
			Map<String,Integer> zdcmap1h = new HashMap<String, Integer>();

			File zdcfile = new File("C:/Users/Administrator/Desktop/TokyoGPS_1547_smooth.csv");
			File ptfile =  new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_1h.csv");
			File datafile =  new File("C:/Users/Administrator/Desktop/outputfiles/outputcor" + counter + "_1h.csv");
			String temp   = ("C:/Users/Administrator/Desktop/outputfiles/temp" + counter + "_1h.csv");
			try{
				BufferedReader br3 = new BufferedReader(new FileReader(zdcfile));
				String line = br3.readLine();
				while( (line = br3.readLine()) != null ) {
					String[] zdctokens = line.split(",");
					String meshcode = zdctokens[0];
					Integer count   = Integer.valueOf(zdctokens[1]);
					zdcmap1h.put(meshcode, count);
					//						meshcodeset.add(meshcode);
				}
				br3.close();

				BufferedReader br4 = new BufferedReader(new FileReader(ptfile));
				String line4 = br4.readLine();
				while( (line4 = br4.readLine()) != null ) {
					String[] pttokens = line4.split("\t");
					String meshcodes = pttokens[0];
					Integer counts   = Integer.valueOf(pttokens[1]);

					ptmap1h.put(meshcodes, counts);
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

			BufferedWriter bw5 = new BufferedWriter(new FileWriter(datafile));
			BufferedWriter tempwriter = new BufferedWriter(new FileWriter(temp));
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

				if(ptmap1h.containsKey(mc)){countpt = ptmap1h.get(mc);}
				if(zdcmap1h.containsKey(mc)){countds = zdcmap1h.get(mc);}
				int diff = (countpt - countds);

				bw5.write(mc + "," + countpt +"," + countds +"," + diff + ","+ wkt);
				bw5.newLine();
				tempwriter.write(countpt + "," + countds);
				tempwriter.newLine();
			}
			bw5.close();
			tempwriter.close();

			HashMap<String, Double> result1 = new HashMap<String, Double>();
			//隕ｳ貂ｬ繝��繧ｿ縺ｨ縺ｮ逶ｸ髢｢繧定ｨ育ｮ�
			Vector v1 = new Vector();
			Vector v2 = new Vector();
			Correlation.readTextFromFile_AndSetVector(temp,v1);
			Correlation.KataHenkan(v1,v2);
			double cor  = Correlation.getCorrelationCoefficient(v2);
			System.out.println(cor);
			String number = (String.valueOf(counter)+ "1h") ;
			result1.put(number, cor);

			BufferedWriter bw6 = new BufferedWriter(new FileWriter("c:/Users/Administrator/Desktop/result_1h" + counter + ".csv"));
			TreeSet<String> sortedKey1 = new TreeSet<String>(result1.keySet());
			for (String key: sortedKey1){
				Double value = result1.get(key);
				bw6.write(key +","+ value);
				bw6.newLine();
			}
			bw6.close();

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

			//隕ｳ貂ｬ繝��繧ｿ縺ｨ豈碑ｼ�
			Map<String,Integer> ptmap2h = new HashMap<String, Integer>();
			Map<String,Integer> zdcmap2h = new HashMap<String, Integer>();

			File zdcfile2h = new File("C:/Users/Administrator/Desktop/TokyoGPS_1647_smooth.csv");
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
			//隕ｳ貂ｬ繝��繧ｿ縺ｨ縺ｮ逶ｸ髢｢繧定ｨ育ｮ�
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

			File indir3h = new File("c:/Users/Administrator/Desktop/output/agent_log/agentlog_10800.csv");

			try{
				BufferedReader brmesh = new BufferedReader(new FileReader(indir3h));
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
				volume.export(new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_3h.csv"));
			}
			catch(FileNotFoundException e) {
				System.out.println("File not found 2");
			}
			catch(IOException e) {
				System.out.println(e);
			}

			//隕ｳ貂ｬ繝��繧ｿ縺ｨ豈碑ｼ�
			Map<String,Integer> ptmap3h = new HashMap<String, Integer>();
			Map<String,Integer> zdcmap3h = new HashMap<String, Integer>();

			File zdcfile3h = new File("C:/Users/Administrator/Desktop/TokyoGPS_1747_smooth.csv");
			File ptfile3h =  new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_3h.csv");
			File datafile3h =  new File("C:/Users/Administrator/Desktop/outputfiles/outputcor" + counter + "_3h.csv");
			String temp3h   = ("C:/Users/Administrator/Desktop/outputfiles/temp" + counter + "_3h.csv");
			try{
				BufferedReader br3 = new BufferedReader(new FileReader(zdcfile3h));
				String line = br3.readLine();
				while( (line = br3.readLine()) != null ) {
					String[] zdctokens = line.split(",");
					String meshcode = zdctokens[0];
					Integer count   = Integer.valueOf(zdctokens[1]);
					zdcmap3h.put(meshcode, count);
					//						meshcodeset.add(meshcode);
				}
				br3.close();

				BufferedReader br4 = new BufferedReader(new FileReader(ptfile3h));
				String line4 = br4.readLine();
				while( (line4 = br4.readLine()) != null ) {
					String[] pttokens = line4.split("\t");
					String meshcodes = pttokens[0];
					Integer counts   = Integer.valueOf(pttokens[1]);

					ptmap3h.put(meshcodes, counts);
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

			BufferedWriter bw3h = new BufferedWriter(new FileWriter(datafile3h));
			BufferedWriter tempwriter3h = new BufferedWriter(new FileWriter(temp3h));
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

				if(ptmap3h.containsKey(mc)){countpt = ptmap3h.get(mc);}
				if(zdcmap3h.containsKey(mc)){countds = zdcmap3h.get(mc);}
				int diff = (countpt - countds);

				bw3h.write(mc + "," + countpt +"," + countds +"," + diff + ","+ wkt);
				bw3h.newLine();
				tempwriter3h.write(countpt + "," + countds);
				tempwriter3h.newLine();
			}
			bw3h.close();
			tempwriter3h.close();

			HashMap<String, Double> result3 = new HashMap<String, Double>();
			//隕ｳ貂ｬ繝��繧ｿ縺ｨ縺ｮ逶ｸ髢｢繧定ｨ育ｮ�
			Vector v13h = new Vector();
			Vector v23h = new Vector();
			Correlation.readTextFromFile_AndSetVector(temp3h,v13h);
			Correlation.KataHenkan(v13h,v23h);
			double cor3h  = Correlation.getCorrelationCoefficient(v23h);
			System.out.println(cor3h);
			String number3h = (String.valueOf(counter)+ "3h") ;
			result3.put(number3h, cor3h);

			BufferedWriter r3h = new BufferedWriter(new FileWriter("c:/Users/Administrator/Desktop/result_3h" + counter + ".csv"));
			TreeSet<String> sortedKey3 = new TreeSet<String>(result3.keySet());
			for (String key: sortedKey3){
				Double value = result3.get(key);
				r3h.write(key +","+ value);
				r3h.newLine();
			}
			r3h.close();

			File indir4h = new File("c:/Users/Administrator/Desktop/output/agent_log/agentlog_14400.csv");

			try{
				BufferedReader brmesh = new BufferedReader(new FileReader(indir4h));
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
				volume.export(new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_4h.csv"));
			}
			catch(FileNotFoundException e) {
				System.out.println("File not found 2");
			}
			catch(IOException e) {
				System.out.println(e);
			}

			//隕ｳ貂ｬ繝��繧ｿ縺ｨ豈碑ｼ�
			Map<String,Integer> ptmap4h = new HashMap<String, Integer>();
			Map<String,Integer> zdcmap4h = new HashMap<String, Integer>();

			File zdcfile4h = new File("C:/Users/Administrator/Desktop/TokyoGPS_1847_smooth.csv");
			File ptfile4h =  new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_4h.csv");
			File datafile4h =  new File("C:/Users/Administrator/Desktop/outputfiles/outputcor" + counter + "_4h.csv");
			String temp4h   = ("C:/Users/Administrator/Desktop/outputfiles/temp" + counter + "_4h.csv");
			try{
				BufferedReader br3 = new BufferedReader(new FileReader(zdcfile4h));
				String line = br3.readLine();
				while( (line = br3.readLine()) != null ) {
					String[] zdctokens = line.split(",");
					String meshcode = zdctokens[0];
					Integer count   = Integer.valueOf(zdctokens[1]);
					zdcmap4h.put(meshcode, count);
					//						meshcodeset.add(meshcode);
				}
				br3.close();

				BufferedReader br4 = new BufferedReader(new FileReader(ptfile4h));
				String line4 = br4.readLine();
				while( (line4 = br4.readLine()) != null ) {
					String[] pttokens = line4.split("\t");
					String meshcodes = pttokens[0];
					Integer counts   = Integer.valueOf(pttokens[1]);

					ptmap4h.put(meshcodes, counts);
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

			BufferedWriter bw4h = new BufferedWriter(new FileWriter(datafile4h));
			BufferedWriter tempwriter4h = new BufferedWriter(new FileWriter(temp4h));
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

				if(ptmap4h.containsKey(mc)){countpt = ptmap4h.get(mc);}
				if(zdcmap4h.containsKey(mc)){countds = zdcmap4h.get(mc);}
				int diff = (countpt - countds);

				bw4h.write(mc + "," + countpt +"," + countds +"," + diff + ","+ wkt);
				bw4h.newLine();
				tempwriter4h.write(countpt + "," + countds);
				tempwriter4h.newLine();
			}
			bw4h.close();
			tempwriter4h.close();

			HashMap<String, Double> result4 = new HashMap<String, Double>();
			//隕ｳ貂ｬ繝��繧ｿ縺ｨ縺ｮ逶ｸ髢｢繧定ｨ育ｮ�
			Vector v14h = new Vector();
			Vector v24h = new Vector();
			Correlation.readTextFromFile_AndSetVector(temp4h,v14h);
			Correlation.KataHenkan(v14h,v24h);
			double cor4h  = Correlation.getCorrelationCoefficient(v24h);
			System.out.println(cor4h);
			String number4h = (String.valueOf(counter)+ "4h") ;
			result4.put(number4h, cor4h);

			BufferedWriter r4h = new BufferedWriter(new FileWriter("c:/Users/Administrator/Desktop/result_4h" + counter + ".csv"));
			TreeSet<String> sortedKey4 = new TreeSet<String>(result4.keySet());
			for (String key: sortedKey4){
				Double value = result4.get(key);
				r4h.write(key +","+ value);
				r4h.newLine();
			}
			r4h.close();

			File indir5h = new File("c:/Users/Administrator/Desktop/output/agent_log/agentlog_18000.csv");

			try{
				BufferedReader brmesh = new BufferedReader(new FileReader(indir5h));
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
				volume.export(new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_5h.csv"));
			}
			catch(FileNotFoundException e) {
				System.out.println("File not found 2");
			}
			catch(IOException e) {
				System.out.println(e);
			}

			//隕ｳ貂ｬ繝��繧ｿ縺ｨ豈碑ｼ�
			Map<String,Integer> ptmap5h = new HashMap<String, Integer>();
			Map<String,Integer> zdcmap5h = new HashMap<String, Integer>();

			File zdcfile5h = new File("C:/Users/Administrator/Desktop/TokyoGPS_1947_smooth.csv");
			File ptfile5h =  new File("C:/Users/Administrator/Desktop/outputfiles/outputmesh" + counter + "_5h.csv");
			File datafile5h =  new File("C:/Users/Administrator/Desktop/outputfiles/outputcor" + counter + "_5h.csv");
			String temp5h   = ("C:/Users/Administrator/Desktop/outputfiles/temp" + counter + "_5h.csv");
			try{
				BufferedReader br3 = new BufferedReader(new FileReader(zdcfile5h));
				String line = br3.readLine();
				while( (line = br3.readLine()) != null ) {
					String[] zdctokens = line.split(",");
					String meshcode = zdctokens[0];
					Integer count   = Integer.valueOf(zdctokens[1]);
					zdcmap5h.put(meshcode, count);
					//						meshcodeset.add(meshcode);
				}
				br3.close();

				BufferedReader br4 = new BufferedReader(new FileReader(ptfile5h));
				String line4 = br4.readLine();
				while( (line4 = br4.readLine()) != null ) {
					String[] pttokens = line4.split("\t");
					String meshcodes = pttokens[0];
					Integer counts   = Integer.valueOf(pttokens[1]);

					ptmap5h.put(meshcodes, counts);
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

			BufferedWriter bw5h = new BufferedWriter(new FileWriter(datafile5h));
			BufferedWriter tempwriter5h = new BufferedWriter(new FileWriter(temp5h));
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

				if(ptmap5h.containsKey(mc)){countpt = ptmap5h.get(mc);}
				if(zdcmap5h.containsKey(mc)){countds = zdcmap5h.get(mc);}
				int diff = (countpt - countds);

				bw5h.write(mc + "," + countpt +"," + countds +"," + diff + ","+ wkt);
				bw5h.newLine();
				tempwriter5h.write(countpt + "," + countds);
				tempwriter5h.newLine();
			}
			bw5h.close();
			tempwriter5h.close();

			HashMap<String, Double> result5 = new HashMap<String, Double>();
			//隕ｳ貂ｬ繝��繧ｿ縺ｨ縺ｮ逶ｸ髢｢繧定ｨ育ｮ�
			Vector v15h = new Vector();
			Vector v25h = new Vector();
			Correlation.readTextFromFile_AndSetVector(temp5h,v15h);
			Correlation.KataHenkan(v15h,v25h);
			double cor5h  = Correlation.getCorrelationCoefficient(v25h);
			System.out.println(cor5h);
			String number5h = (String.valueOf(counter)+ "4h") ;
			result5.put(number5h, cor5h);

			BufferedWriter r5h = new BufferedWriter(new FileWriter("c:/Users/Administrator/Desktop/result_5h" + counter + ".csv"));
			TreeSet<String> sortedKey5 = new TreeSet<String>(result5.keySet());
			for (String key: sortedKey5){
				Double value = result5.get(key);
				r5h.write(key +","+ value);
				r5h.newLine();
			}
			r5h.close();


			System.out.println(counter);
			counter = counter + 1 ;

			//			} // until here for each parameter set
			//			br1.close();
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
