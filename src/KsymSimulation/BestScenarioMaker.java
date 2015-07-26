package KsymSimulation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BestScenarioMaker {
	/* Written by Taka YABE @ U.Tokyo
	 * since 11/13/2014 :)
	 */

	/*
	 * 1. calculate best parameter
	 * 2. get PT(初期値)
	 * 3. simulate for 1 hour and 2 hours ahead
	 * 4. for 'simulation for 2 hours ahead', calculate Cor. Coeff.
	 * 
	 */

	public static void main(String args[]){

		File scenarioresults = new File(" results of all scenarios ");
		File bestpara = getBestPara(scenarioresults);
		File ptfile = new File ("pt data");
		File resfile = new File ("");

		try{
			BufferedReader br1 = new BufferedReader(new FileReader(bestpara));
			String paraline;
			BufferedWriter result = new BufferedWriter(new FileWriter(resfile, true));

			while ( (paraline = br1.readLine()) != null){  //for each parameter set
				String[] tokens = paraline.split(",");
				double a = Double.valueOf(tokens[0]); //% of staying people moving
				double b = Double.valueOf(tokens[1]); //% of moving people moving
				double c = Double.valueOf(tokens[2]); //coefficient of people going home
				double d = Double.valueOf(tokens[3]); //goes to mokuteki

				File outfile= new File ("C:/Users/yabec_000/Desktop/input/gen_agent.csv");
				try {
					BufferedReader br = new BufferedReader(new FileReader(ptfile));
					BufferedWriter bw = new BufferedWriter (new FileWriter(outfile));
					String line;
					while ( (line = br.readLine()) != null){ // for each Person
						String[] token = line.split(",");
						String  PID   = token[0];
						Double age   = Double.valueOf(token[2]);
						Double nowx  = Double.valueOf(token[7]);
						Double nowy  = Double.valueOf(token[8]);
						Double homex = Double.valueOf(token[9]);
						Double homey = Double.valueOf(token[10]);
						Double mokux = Double.valueOf(token[11]);
						Double mokuy = Double.valueOf(token[12]);
						String  way   = token[6];
						double dis = Math.pow(((homex - nowx)*(homex - nowx)+(homey - nowy)*(homey - nowy)),0.5)/1000;
						double time = Simulation_ver2.time(a, b, way, 1);
						String goal = Simulation_ver2.getgoal(c, d, age, dis, way); //return words
						double goalx;
						double goaly;
						if(goal.equals("home")){
							goalx = homex;
							goaly = homey;
						}
						else if (goal.equals("mokuteki")){
							goalx = mokux;
							goaly = mokuy;
						}
						else{
							goalx = 0; //station point
							goaly = 0; 
						}

						bw.write(PID + "," + age + "," + way + "," + nowx + "," + nowy + "," + homex + "," + homey + "," + 
								mokux + "," + mokuy + "," + time + "," + goalx + "," + goaly);
						bw.newLine();

					} // for each person ends
					br.close();
					bw.close();
				}
				catch(FileNotFoundException xx) {
					System.out.println("File not found 1");
				}
				catch(IOException xxx) {
					System.out.println(xxx);
				}

				//				"Kashiyama Simulator" ... outputs population data by mesh. (simulate 1 hour and 2 hours)

				File simfile1 = new File ("C:/...csv"); //in mesh.(result of simulator)
				File simfile2 = new File ("C:/...csv"); //in mesh.(result of simulator)
				File obsfile1 = new File ("C:/...csv"); //obsdata file in mesh (result of simulator)
				File obsfile2 = new File ("C:/...csv"); //obsdata file in mesh (result of simulator)
				File meshlist = new File ("C:/...csv"); //meshlist of area
				File result1  = new File ("");
				File result2  = new File ("");
				double RMSE1 = Simulation_ver2.getRMSE(simfile1, obsfile1, meshlist, result1);
				double RMSE2 = Simulation_ver2.getRMSE(simfile2, obsfile2, meshlist, result2);

				result.write("RMSE of best parameters is " + RMSE1 + "\n" + 
							 "RMSE of estimation is " +RMSE2);
				result.newLine();

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

	public static File getBestPara(File infile){
		File parafile = new File("");
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(parafile));
			String line = null;

			ArrayList<Double> like = new ArrayList<Double>();
			ArrayList<Double> alist = new ArrayList<Double>();
			ArrayList<Double> blist = new ArrayList<Double>();
			ArrayList<Double> clist = new ArrayList<Double>();
			ArrayList<Double> dlist = new ArrayList<Double>();

			while((line = br.readLine()) != null){
				String tokens[] = line.split(",");
				double a = Double.parseDouble(tokens[1]);
				double b = Double.parseDouble(tokens[2]);
				double c = Double.parseDouble(tokens[3]);
				double d = Double.parseDouble(tokens[4]);
				double likelihood = Double.parseDouble(tokens[5]);

				double aa = a*likelihood;
				alist.add(aa);
				double bb = b*likelihood;
				blist.add(bb);
				double cc = c*likelihood;
				clist.add(cc);
				double dd = d*likelihood;
				dlist.add(dd);
				like.add(likelihood);
			}
			br.close();

			double likesum = 0d;
			for (double ele : like){
				likesum = likesum + ele;
			}

			double opta = 0d;
			double optb = 0d;
			double optc = 0d;
			double optd = 0d;

			for (double elea : alist){
				elea = elea / likesum;
				opta = opta + elea;
			}
			for (double eleb : blist){
				eleb = eleb / likesum;
				optb = optb + eleb;
			}			
			for (double elec : clist){
				elec = elec / likesum;
				optc = optc + elec;
			}			
			for (double eled : dlist){
				eled = eled / likesum;
				optd = optd + eled;
			}

			bw.write(opta + "," + optb + "," + optc + "," + optd);
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return parafile;
	}
}
