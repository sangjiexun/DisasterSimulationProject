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
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;

public class Simulation_ver2 {
	/* Written by Taka YABE; U.Tokyo
	 * since11/12/2014 :) 
	 * */

	public static void main(String args[]){

		//		File infile = new File ("C:/Users/..."); //Final PT Data
		//		File parafile= new File ("C:/Users/..."); //Parameter File
		//		File results = new File ("C:/Users/..."); //RMSE of all scenarios

		int scenum = 1;
		//		try{
		//			BufferedReader br1 = new BufferedReader(new FileReader(parafile));
		//			String paraline = null;
		//			BufferedWriter result = new BufferedWriter(new FileWriter(results, true));

		//			while ( (paraline = br1.readLine()) != null){  //for each parameter set
		//				String[] tokens = paraline.split(",");
		//				double a = Double.valueOf(tokens[0]); //% of staying people moving
		//				double b = Double.valueOf(tokens[1]); //% of moving people moving
		//				double c = Double.valueOf(tokens[2]); //coefficient of people going home
		//				double d = Double.valueOf(tokens[3]); //goes to mokuteki
		//
		//				File outfile= new File ("C:/Users/yabec_000/Desktop/input/gen_agent.csv");
		//				try {
		//					BufferedReader br = new BufferedReader(new FileReader(infile));
		//					BufferedWriter bw = new BufferedWriter (new FileWriter(outfile));
		//					String line = null;
		//					while ( (line = br.readLine()) != null){ // for each Person
		//						String[] token = line.split(",");
		//						String  PID   = token[0];
		//						Double age   = Double.valueOf(token[2]);
		//						Double nowx  = Double.valueOf(token[8]);
		//						Double nowy  = Double.valueOf(token[7]);
		//						Double homex = Double.valueOf(token[10]);
		//						Double homey = Double.valueOf(token[9]);
		//						Double mokux = Double.valueOf(token[12]);
		//						Double mokuy = Double.valueOf(token[11]);
		//						String  way   = token[6];
		//						double dis = Math.pow(((homex - nowx)*(homex - nowx)+(homey - nowy)*(homey - nowy)),0.5)/1000;
		//						double time = time(a, b, way, 1);
		//						String goal = getgoal(c, d, age, dis, way); //return words
		//						double goalx;
		//						double goaly;
		//						if(goal.equals("home")){
		//							goalx = homex;
		//							goaly = homey;
		//						}
		//						else if (goal.equals("mokuteki")){
		//							goalx = mokux;
		//							goaly = mokuy;
		//						}
		//						else{
		//							goalx = 0; //station point
		//							goaly = 0; 
		//						}
		//
		//						bw.write(PID + "," + age + "," + way + "," + nowx + "," + nowy + "," + homex + "," + homey + "," + 
		//								mokux + "," + mokuy + "," + time + "," + goalx + "," + goaly);
		//						bw.newLine();
		//
		//					} // for each person ends
		//					br.close();
		//					bw.close();
		//				}
		//				catch(FileNotFoundException xx) {
		//					System.out.println("File not found 1");
		//				}
		//				catch(IOException xxx) {
		//					System.out.println(xxx);
		//				}

		//				"Kashiyama Simulator" ... outputs population data by mesh.

		File simfile= new File ("C:/Users/yabec_000/Desktop/Tokyo_PT_Mesh5.csv"); //in mesh.
		File obsfile= new File ("C:/Users/yabec_000/Desktop/TokyoZDC_konzatsu5.tsv"); //obsdata file in mesh
		File meshlist = new File("C:/Users/yabec_000/Desktop/Tokyo3wards_meshcodes_5.csv"); //meshlist of area
		File TwoMeshPop = new File("");
		double RMSE = getRMSE(simfile, obsfile, meshlist, TwoMeshPop);
//		double likelihood = getlikelihood(RMSE, 100, 3146);
		String rmse = String.valueOf(RMSE);
		System.out.println(rmse);
		//				result.write(scenum + "," + a + "," + b + "," + c + "," + d + "," + rmse + "," + likelihood);
		//				result.write(rmse);
		//				result.newLine();

		System.out.println(scenum);
		scenum = scenum + 1 ;
		//			} // until here for each parameter set
		//			br1.close();
		//			result.close();
		//		}
		//		catch(FileNotFoundException xx) {
		//			System.out.println("File not found 5");
		//		}
		//		catch(IOException xxx) {
		//			System.out.println(xxx);
		//		}
	}

	public static double time(double a, double b, String way, int hour){ 
		Random ran = new Random();
		double dig = ran.nextDouble();
		double dig1 = ran.nextDouble();
		double time;
		if(way.equals("stay")){
			if (dig<=a){time = ((hour*60-1)+dig1*60)*60;} //in seconds
			else{time = 100000;}
		}
		else{
			if (dig<=b){time = ((hour*60-1)+dig1*60)*60;} //in seconds
			else{time = 100000;}
		}
		return time;
	}

	public static String getgoal(double c, double d,Double age, double dishome, String mode){
		String goal;
		Random ran = new Random();
		double dig = ran.nextDouble();

		if(mode.equals("stay")){
			if ((age>=1)&&(age<=4)){
				if (dishome<=10*c){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}}
			else if ((age>=5)&&(age<=6)){
				if (dishome<=15*c){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
			else if ((age>=7)&&(age<=10)){
				if (dishome<=20*c){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
			else if ((age>=11)&&(age<=12)){
				if (dishome<=15*c){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
			else{
				if (dishome<=10*c){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
		}
		else{
			if ((age>=1)&&(age<=4)){
				if (dishome<=10*c){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
			else if ((age>=5)&&(age<=6)){
				if (dishome*c<=15){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
			if ((age>=7)&&(age<=10)){
				if (dishome*c<=20){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
			if ((age>=11)&&(age<=12)){
				if (dishome*c<=15){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
			else{
				if (dishome*c<=10){goal = ("home");}
				else if(dig<d){goal= ("mokuteki");}
				else {goal = ("station");}
			}
		}
		return goal;
	}

	//make list of mesh
	public static ArrayList<String> getMeshlist(File infile){
		ArrayList<String> meshlist = new ArrayList<String>();
		try{
			BufferedReader meshreader = new BufferedReader(new FileReader(infile));
			String line = null;
			while((line=meshreader.readLine())!= null){
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				meshlist.add(meshcode);
			}
			meshreader.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found 2");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return meshlist;
	}

	public static Map<String, Double> intomap(File popfile){
		//gather pop results into map
		Map<String,Double> popmap = new HashMap<String, Double>();
		try{
			BufferedReader br3 = new BufferedReader(new FileReader(popfile));
			String line = null;
//			String line = br3.readLine();
			while( (line = br3.readLine()) != null ) {
				String[] tokens = line.split(",");
				String meshcode = tokens[0];
				Double count   = (Double.valueOf(tokens[1]));
//				Double count   = (Double.valueOf(tokens[1])*1.3);
//				Double count   = (Double.valueOf(tokens[1])*48.2);
				popmap.put(meshcode, count);
			}
			br3.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return popmap;
	}

	public static Map<String, Double> intomap2(File popfile){
		//gather pop results into map
		Map<String,Double> popmap = new HashMap<String, Double>();
		try{
			BufferedReader br3 = new BufferedReader(new FileReader(popfile));
			String line = null;
//			String line = br3.readLine();
			while( (line = br3.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
//				Double count   = (Double.valueOf(tokens[1])*7.912); //fujisawa
//				Double count   = (Double.valueOf(tokens[1])*48.2);  // tokyo
				Double count   = (Double.valueOf(tokens[1]));
				popmap.put(meshcode, count);
			}
			br3.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found zdc");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return popmap;
	}

	public static Double getRMSE(File pt, File zdc, File meshlist, File result){ 
		Map<String, Double> ptmap = intomap(pt);
		Map<String, Double> zdcmap = intomap2(zdc);
		ArrayList<String> meshes = getMeshlist(meshlist);
//		System.out.println("meshes: " + meshes);
		ArrayList<Double> templist = new ArrayList<Double>();

		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(result));
			for(String mc:meshes){
				double countpt = 0d;
				double countds = 0d;
				if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
				if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}
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
				String zonecode = getZonecode(mesh.getCenter());

				bw.write(mc + "\t" + zonecode + "\t" + countpt + "\t" + countds + "\t" + diff + "\t" + Math.abs(diff)/countpt + "\t" + wkt);
//				bw.write(mc + "," + zonecode + "," + countpt + "," + countds + "," + diff + "," + Math.abs(diff)/countpt + "," + wkt);
				bw.newLine();
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

	public static double getlikelihood(double RMSE, double sigma, double normalRMSE){
		double likelihood = (1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp( - ((RMSE - normalRMSE) * (RMSE - normalRMSE)) / (2 * sigma * sigma));
		return likelihood;
	}

	static File shapedir = new File("C:/Users/yabetaka/Desktop/Tokyo3WardZone");
//	static File shapedir = new File("C:/Users/yabec_000/Desktop/FujisawaEQProject/zone_bounds_fujisawa_shape");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static String getZonecode(LonLat point) {
		String zonecode;
		List<String> zonecodeList = gchecker.listOverlaps("zonecode",point.getLon(),point.getLat());
		if( zonecodeList == null || zonecodeList.isEmpty() ) {
			zonecode = "0"; 
		}
		else{
			zonecode = String.valueOf(Integer.parseInt(zonecodeList.get(0)));
		}
		return zonecode ;
	}

}