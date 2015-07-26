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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import jp.ac.ut.csis.pflow.geom.Mesh;
import Tools.Correlation;
import Tools.MeshLevelConverter;

/* Made by T.Yabe
 * since 2014/11/24
 */

public class Simulator2ndPartTokyo {

	public static void main(String args[]){

		int h = 6; //hour of investigation
		double pop = 0; //minimum mesh pop of investigation (usually 0)

		int time = h + 14;
		int filenumber = 121; // ENTER number of scenarios!!
		File obs = new File("C:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110311_4_"+ time +".csv"); // define file of observation data

		File meshcodes = new File("C:/Users/yabetaka/Desktop/Tokyo3Wards_meshcodes_4.csv"); //define meshcode file for area of study
		//		File parafile  = new File("C:/Users/yabetaka/Desktop/Experiment0331/1hour/parameter1.csv");
		File parafile  = new File("C:/Users/yabetaka/Desktop/Exp0503/"+h+"hour/parameters"+h+".csv");
		File newweight  = new File ("C:/Users/yabetaka/Desktop/Exp0503/"+h+"hour/NewWeightresults"+h+".csv");

		/* phases 1 and 2 ... */
		getWeight(getresultFile(filenumber, parafile, obs, meshcodes, h, pop, 10000), newweight);

		/*get cor. coeff.*/
		File results = new File ("C:/Users/yabetaka/Desktop/Exp0503/"+h+"hour/NewWeightresults"+h+".csv");
		getBestPara(results);
		Double likesum = getBestPara(results)[4];
				System.out.println("linesum is " + likesum);

		/* phase 3 */
		int hour = h+1; // ENTER next hour!!
		getNextPara(results, likesum, hour, getBestPara(results)[0], 
				getBestPara(results)[1], getBestPara(results)[2], getBestPara(results)[3], 0.1);

	}

	// input:parameter scnenario para file, output:parameters
	public static String[] getPara(File infile, int i){
		String[] parameters = new String[4];
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String line = null;
			while ((line = br.readLine()) != null){
				String tokens[] = line.split(",");
				String scenum = tokens[0];
				if (scenum.equals(i)){
					parameters[0] = tokens[1];
					parameters[1] = tokens[2];
					parameters[2] = tokens[3];
					parameters[3] = tokens[4];
				}
				else {continue;}
			}
			br.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return parameters;
	}

	public static File getresultFile(int filenumber, File parafile, File obs, File meshcodes, int h, double pop, double sigma){
		// record rmse and likelihood for each scenario
		File rmse_like = new File("C:/Users/yabetaka/Desktop/" + filenumber + "results.csv"); 
		File[] scenarioresults = new File[filenumber];

		Set<String> meshcodeset = new HashSet<String>();

		try{
			BufferedReader brm = new BufferedReader(new FileReader(meshcodes));
			String linemesh = null;
			while((linemesh = brm.readLine()) != null){
				String[] tokens = linemesh.split("\t");
				meshcodeset.add(tokens[0]);
			}
			brm.close();
		}
		catch(FileNotFoundException z) {System.out.println("File not found 1");}
		catch(IOException e) {System.out.println(e);}

		int n = filenumber-1;
		for (int i=0; i<=n; i++){
			//			//			File in = new File ("C:/Users/yabetaka/Desktop/Tokyo0207/Tokyo1Hour/output1/mesh_" + i + ".csv");
			//			//			File out = new File ("C:/Users/yabetaka/Desktop/Tokyo0207/Tokyo1Hour/output1/mesh4_" + i + ".csv");
			//			File in = new File ("C:/Users/yabetaka/Desktop/output1/mesh_" + i + ".csv");
			//			File out = new File ("C:/Users/yabetaka/Desktop/output1/mesh4_" + i + ".csv");
			//			scenarioresults[i] = MeshLevelConverter.mesh5to4(in,out,",");
			scenarioresults[i] = new File ("C:/Users/yabetaka/Desktop/Exp0503/"+h+"hour/output"+h+"hour/mesh_"+i+".csv");
		}

		try{
			File newdir = new File("c:/users/yabetaka/Desktop/tempresults");
			newdir.mkdir();

			BufferedWriter bw = new BufferedWriter(new FileWriter(rmse_like));
			String[] parameters = {"0" , "0" , "0" , "0"};
			for (int j=0; j<filenumber; j++){
				File TempResult = new File("c:/users/yabetaka/Desktop/tempresults/tempresultfile"+ j +".csv");
				//				String[] parameters = getPara(parafile,j);			
				BufferedReader br = new BufferedReader(new FileReader(parafile));
				String line = null;
				while ((line = br.readLine()) != null){
					String tokens[] = line.split(",");
					String scenum = tokens[0];
					if (scenum.equals(String.valueOf(j+1))){
						parameters[0] = tokens[1];
						parameters[1] = tokens[2];
						parameters[2] = tokens[3];
						parameters[3] = tokens[4];
					}
					else {continue;}
				}
				br.close();

				double RMSE = getRMSE(scenarioresults[j], obs, meshcodes, TempResult, pop);
				double likelihood = getlikelihood(RMSE,sigma, 0);
				double l2 = getL2(scenarioresults[j], obs, meshcodes);
				double correl = getCorrelation(scenarioresults[j],obs,meshcodes, pop);
				//				double error = getErrorPercentage(scenarioresults[j],obs,meshcodes, pop);
				String rmse = String.valueOf(RMSE);
				//				double rmsepercent = RMSE/4000*100;

				bw.write(j+1 	      + "," + 
						parameters[0] + "," + 
						parameters[1] + "," + 
						parameters[2] + "," + 
						parameters[3] + "," + 
						rmse		  + "," + 
						likelihood	  + "," +
						l2			  + "," +
						correl		  + "," 
						//						error         + "," + 
						//						rmsepercent		
						);
				//				for(String m:meshcodeset){	
				//					bw.write(m + "," + getLforMesh(scenarioresults[j], obs, meshcodes,m)[0] 
				//							+ "," +	getLforMesh(scenarioresults[j], obs, meshcodes,m)[1]
				//									+ "," +	getLforMesh(scenarioresults[j], obs, meshcodes,m)[2] + "," );
				//				}
				bw.newLine();
				//				System.out.println("yeah");
			}
			bw.close();
		}
		catch(FileNotFoundException xx) {
			System.out.println("File not found 1");
		}
		catch(IOException xxx) {
			System.out.println(xxx);
		}
		return rmse_like;
	}

	public static Double getCorrelation(File input1, File input2, File meshcodefile, double pop){
		Map<String,Integer> ptmap = new HashMap<String, Integer>();
		Map<String,Double> zdcmap = new HashMap<String, Double>();
		String temp = ("c:/Users/yabetaka/Desktop/tempforCorrel.csv");
		Set<String> meshcodeset = new HashSet<String>();

		try{
			BufferedReader brm = new BufferedReader(new FileReader(meshcodefile));
			String linemesh = null;
			while((linemesh = brm.readLine()) != null){
				String[] tokens = linemesh.split("\t");
				meshcodeset.add(tokens[0]);
			}
			brm.close();
		}
		catch(FileNotFoundException z) {System.out.println("File not found 1");}
		catch(IOException e) {System.out.println(e);}

		try{
			BufferedReader br3 = new BufferedReader(new FileReader(input1));
			String line = null;
			while( (line = br3.readLine()) != null ) {
				String[] zdctokens = line.split(",");
				String meshcode = zdctokens[0];
				Double count   = Double.valueOf(zdctokens[1]);
				zdcmap.put(meshcode, count);
			}
			br3.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 2");}
		catch(IOException e) {System.out.println(e);}

		try{
			BufferedReader br4 = new BufferedReader(new FileReader(input2));
			String line4 = null;
			while( (line4 = br4.readLine()) != null ) {
				String[] pttokens = line4.split("\t");
				String meshcode = pttokens[0];
				Double counts   = Double.valueOf(pttokens[1]);
				Integer intcount = (int)Math.floor(counts);
				ptmap.put(meshcode, intcount);
				//				meshcodeset.add(meshcodes);
			}
			br4.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found shitt");}
		catch(IOException e) {System.out.println(e);}

		try{
			BufferedWriter tempwriter = new BufferedWriter(new FileWriter(temp));
			for(String mc:meshcodeset){
				int countpt = 0;
				int countds = 0;
				if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
				if(zdcmap.containsKey(mc)){countds = (int)Math.floor(zdcmap.get(mc));}
				if(countds > pop){
					tempwriter.write(countpt + "," + countds);
					tempwriter.newLine();
				}
			}
			tempwriter.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found damnit");
		}
		catch(IOException e) {
			System.out.println(e);
		}

		//		int counter = meshcodeset.size();
		Vector v1 = new Vector();
		Vector v2 = new Vector();
		Correlation.readTextFromFile_AndSetVector(temp,v1);
		Correlation.KataHenkan(v1,v2);
		double cor  = Correlation.getCorrelationCoefficient(v2);
		return cor;
	}

	// calculate best parameter from likelihood
	public static Double[] getBestPara(File infile){
		Double[] optpara = new Double[5];
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String line = null;
			ArrayList<Double> like = new ArrayList<Double>();
			ArrayList<Double> alist = new ArrayList<Double>();
			ArrayList<Double> blist = new ArrayList<Double>();
			ArrayList<Double> clist = new ArrayList<Double>();
			ArrayList<Double> dlist = new ArrayList<Double>();

			while((line = br.readLine()) != null){
				String tokens[] = line.split(",");
				double aa = (Double.parseDouble(tokens[1]))*(Double.parseDouble(tokens[10]));
				alist.add(aa);
				//				System.out.println("aa is " + aa);
				double bb = (Double.parseDouble(tokens[2]))*(Double.parseDouble(tokens[10]));
				blist.add(bb);
				//				System.out.println("bb is " + bb);
				double cc = (Double.parseDouble(tokens[3]))*(Double.parseDouble(tokens[10]));
				clist.add(cc);
				//				System.out.println("cc is " + cc);
				double dd = (Double.parseDouble(tokens[4]))*(Double.parseDouble(tokens[10]));
				dlist.add(dd);
				//				System.out.println("dd is " + dd);
				like.add(Double.parseDouble(tokens[10]));
				//				System.out.println("like is " + like);
			}
			br.close();

			double likesum = 0d;
			for (double ele : like){
				likesum = likesum + ele;
			}
			//			System.out.println("sum of like is " + likesum);

			double opta = 0d;
			double suma = 0d;
			double optb = 0d;
			double sumb = 0d;
			double optc = 0d;
			double sumc = 0d;
			double optd = 0d;
			double sumd = 0d;

			for (double elea : alist){
				suma = elea + suma;
				opta = suma/likesum;
			}
			System.out.println("opta is " + opta);
			for (double eleb : blist){
				sumb = eleb + sumb;
				optb = sumb/likesum;
			}			
			System.out.println("optb is " + optb);
			for (double elec : clist){
				sumc = elec + sumc;
				optc = sumc/likesum;
			}			
			System.out.println("optc is " + optc);
			for (double eled : dlist){
				sumd = eled + sumd;
				optd = sumd/likesum;
			}
			System.out.println("optd is " + optd);

			optpara[0] = opta;
			optpara[1] = optb;
			optpara[2] = optc;
			optpara[3] = optd;
			optpara[4] = likesum;
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return optpara;
	}

	public static Double getErrorPercentage(File pt, File zdc, File meshlist, double pop){ 
		Map<String, Double> ptmap = Simulation_ver2.intomap(pt);
		Map<String, Double> zdcmap = Simulation_ver2.intomap2(zdc);
		ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);
		//		System.out.println("meshes: " + meshes);
		ArrayList<Double> templist = new ArrayList<Double>();
		for(String mc:meshes){
			double temp= 0;
			double countpt = 0d;
			double countds = 0d;
			if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
			if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}
			if((countds != 0)&&(countpt != 0)){
				if(countds > pop){
					double diff = Math.abs(countpt - countds);
					temp = (diff/countds)*100;
				}
			}
			else{
				temp = 0;
			}
			templist.add(temp);
		}
		Double sum = 0d;
		for(Double num:templist){
			sum = sum + num;
		}
		Double ErrorPercent = sum/templist.size();
		//		System.out.println("rmse: " + RMSE);
		return ErrorPercent;
	}

	public static Double getRMSE(File pt, File zdc, File meshlist, File result, double pop){ 
		Map<String, Double> ptmap = Simulation_ver2.intomap(pt);
		Map<String, Double> zdcmap = Simulation_ver2.intomap2(zdc);
		ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);
		//		System.out.println("meshes: " + meshes);
		ArrayList<Double> templist = new ArrayList<Double>();

		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(result));
			for(String mc:meshes){
				double countpt = 0d;
				double countds = 0d;
				if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
				if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}

				if(countds > pop ){
					double diff = (countpt - countds);
					double temp = Math.pow(diff,2);
					templist.add(temp);
					Mesh  mesh     = new Mesh(mc);
					Rectangle2D.Double rect = mesh.getRect();
					String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
							rect.getMinX(),rect.getMaxY(),
							rect.getMaxX(),rect.getMaxY(),
							rect.getMaxX(),rect.getMinY(),
							rect.getMinX(),rect.getMinY());
					String zonecode = Simulation_ver2.getZonecode(mesh.getCenter());

					bw.write(mc + "\t" + zonecode + "\t" + countpt + "\t" + countds + "\t" + diff + "\t" + Math.abs(diff)/countpt + "\t" + wkt);
					//bw.write(mc + "," + zonecode + "," + countpt + "," + countds + "," + diff + "," + Math.abs(diff)/countpt + "," + wkt);
					bw.newLine();
				}
			}
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		Double sum = 0d;
		for(Double num:templist){
			sum = sum + num;
		}
		Double RMSE = Math.pow(sum/templist.size(), 0.5);
		return RMSE;
	}

	public static double getAveragePop(File zdc, File meshlist, double pop){
		Map<String, Double> zdcmap = Simulation_ver2.intomap2(zdc);
		ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);
		ArrayList<Double> templist = new ArrayList<Double>();
		for(String mc:meshes){
			double countds = 0d;
			if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}
			if(countds > pop ){
				templist.add(countds);
			}
		}
		Double sum = 0d;
		for(Double num:templist){
			sum = sum + num;
		}
		Double average = sum/templist.size();
		return average;

	}

	public static double getlikelihood(double RMSE, double sigma, double normalRMSE){
		double likelihood = (1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp( - ((RMSE - normalRMSE) * (RMSE - normalRMSE)) / (2 * sigma * sigma));
		return likelihood;
	}

	public static double getL2(File sim, File obs, File meshlist){
		ArrayList<Double> templist = new ArrayList<Double>();
		ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);
		Map<String, Double> ptmap = Simulation_ver2.intomap(sim);
		Map<String, Double> zdcmap = Simulation_ver2.intomap2(obs);

		for(String mc:meshes){
			double countpt = 0d;
			double countds = 0d;
			if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
			if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}

			//			if(mc.equals("533945093")){
			//				countds = countds/10;
			//			}

			double diff = (countpt - countds);
			double temp = 0;
			if((countpt > 0)&&(countds > 0)){
				//temp = Math.log(getlikelihood(diff,countpt*8.733*Math.pow(countpt,-0.441), 0)); //equation made by me from the experiment
				temp = Math.log(getlikelihood(diff,countds*14.475*Math.pow(countds,-0.51), 0)); //equation made by me from the experiment
				//				if(temp<-100000000){
				//					System.out.println(mc + "," + countpt + "," + countds + "," + temp);
				//				}
			}
			else{
				temp = 0;
			}
			templist.add(temp);		
		}

		double l = 0;
		for (double a:templist){
			l = l + a;
		}
		return l;
	}

	public static Double[] getLforMesh(File sim, File obs, File meshlist, String mesh){
		ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);
		Map<String, Double> ptmap = Simulation_ver2.intomap(sim);
		Map<String, Double> zdcmap = Simulation_ver2.intomap2(obs);
		Double res[] = new Double[3];

		for(String mc:meshes){
			double countpt = 0d;
			double countds = 0d;
			if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
			if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}

			if(mc.equals(mesh)){
				double diff = (countpt - countds);
				double temp = 0;
				if((countpt > 0)&&(countds > 0)){
					temp = Math.log(getlikelihood(diff,countds*14.475*Math.pow(countds,-0.51), 0)); //equation made by me from the experiment
				}
				else{
					temp = 0;
				}	
				res[0] = countpt;
				res[1] = countds;
				res[2] = temp;
			}
		}

		return res;
	}

	public static File getWeight(File infile, File outfile){
		try{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String line = null;
			ArrayList<Double> like = new ArrayList<Double>();

			while((line = br.readLine()) != null){
				String tokens[] = line.split(",");
				like.add(Double.parseDouble(tokens[7])); //token 7 is already logged
			}
			br.close();
			//			System.out.println(like); //TODO delete

			double maxweight = Collections.max(like);
			//			System.out.println(maxweight); //TODO delete

			double sumfai = 0; //Large fai
			for (double a:like){
				double fai = Math.exp(a-maxweight);
				//				System.out.println(fai); //TODO delete
				sumfai = sumfai + fai;
			}
			//			System.out.println(sumfai); //TODO delete

			BufferedReader br1 = new BufferedReader(new FileReader(infile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			while((line = br1.readLine()) != null){
				String tokens[] = line.split(",");
				double fai = Math.exp((Double.parseDouble(tokens[7]))-(maxweight))/sumfai;
				bw.write(line + "," + fai);
				bw.newLine();
			}
			br1.close();
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return outfile;
	}

	public static double Gaussian(double ran1, double ran2){
		double gaussian = Math.sqrt(-2*Math.log(ran1))*0.05*Math.sin(2*Math.PI*ran2);
		return gaussian;
	}

	public static File getNextPara(File in, Double likesum, int hour, double aa, double bb, double cc, double dd, double g){ //in=144results and their likelihood data
		File nextparafile = new File("c:/Users/yabetaka/Desktop/parameters" + hour + ".csv");

		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			BufferedWriter bw = new BufferedWriter(new FileWriter(nextparafile));
			String linex = null;
			Random ran = new Random();
			while((linex = br.readLine()) != null){
				String[] tokens = linex.split(",");
				Double l = Double.parseDouble(tokens[10])/likesum;
				Integer times = (int)Math.round(l*120);
				if (times >= 1){
					for(int i=1; i<=times; i++){
						Double a = Double.parseDouble(tokens[1]) + Gaussian(ran.nextDouble(),ran.nextDouble());
						if(a<0){a=0d;}
						Double b = Double.parseDouble(tokens[2]) + Gaussian(ran.nextDouble(),ran.nextDouble());
						if(b<0){b=0d;}
						Double c = Double.parseDouble(tokens[3]) + Gaussian(ran.nextDouble(),ran.nextDouble());
						if(c<0){c=0d;}
						Double d = Double.parseDouble(tokens[4]) + Gaussian(ran.nextDouble(),ran.nextDouble());
						if(d<0){d=0d;}
						bw.write(a + "," + b + "," + c + "," + d + ",/home/ubuntu/Desktop/yabe/output/yes.dump");
						bw.newLine();
					}
				}
			}

			double[] ax = new double[2];
			double[] bx = new double[2];
			double[] cx = new double[2];
			double[] dx = new double[2];

			ax[0]=aa+g; ax[1]=aa-g; 
			bx[0]=bb+g; bx[1]=bb-g; 
			cx[0]=cc-g; cx[1]=cc+g; 
			dx[0]=dd-g; dx[1]=dd+g; 

			for (int ii=0; ii<=1; ii++){
				for (int jj=0; jj<=1; jj++){
					for (int kk=0; kk<=1; kk++){
						for (int ll=0; ll<=1; ll++){
							if((ax[ii])>0 && bx[jj]>0 && cx[kk]>0 && dx[ll]>0){
								bw.write(ax[ii] + "," + bx[jj] + "," + cx[kk] + "," + dx[ll] + ",/home/ubuntu/Desktop/yabe/output/yes.dump");
								bw.newLine();
							}
						}
					}
				}
			}

			br.close();
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 3");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return nextparafile;
	}
}

