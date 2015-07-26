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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import Tools.Correlation;
import Tools.MeshLevelConverter;
import jp.ac.ut.csis.pflow.geom.Mesh;

public class MeshMeshComparer {

	public static void main(String args[]){

		File input1 = new File ("c:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110310_5_15.csv"); //mesh file 1
		File input2 = new File ("c:/Users/yabetaka/Desktop/ZDCKonzatsuToukeiData/ZDC_20110310_5_22.csv"); //mesh file 1
		//File input1 = new File ("c:/Users/yabetaka/Desktop/1hour.csv"); //mesh file 2

//		File input1 = new File ("c:/Users/yabetaka/Desktop/osaragi5.csv"); //mesh file 1
		//File input2 = new File ("c:/Users/yabetaka/Desktop/Experiment0331/1hour/mesh_0.csv"); //mesh file 2
		//File meshes = new File ("c:/Users/yabetaka/Desktop/Tokyo3Wards_meshcodes_4.csv"); //file of meshcodes
		File meshes = new File ("c:/Users/yabetaka/Desktop/Tokyo3Wards_meshcodes_5.csv"); //file of meshcodes
		File result = new File ("c:/Users/yabetaka/Desktop/mofo4.csv");

//		File input11 = new File ("C:/Users/yabetaka/Desktop/osaragi5_4.csv");
//		MeshLevelConverter.mesh5to4(input1,input11,",");

		double rmse = getRMSE(input1, input2, meshes, result);
		System.out.println("rmse is: " + rmse);

		double correl = getCorrelation(input1, input2, meshes);
		System.out.println("R is: " + correl);

		double errorPercent = getErrorPercentage(input1, input2, meshes);
		System.out.println("error % is: " + errorPercent);
	}

	public static Map<String, Double> intomap(File popfile){
		//gather pop results into map
		Map<String,Double> popmap = new HashMap<String, Double>();
		try{
			BufferedReader br3 = new BufferedReader(new FileReader(popfile));
			String line = null;
			//String line = br3.readLine();
			while( (line = br3.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				//Double count   = (Double.valueOf(tokens[1]));
				//Double count   = (Double.valueOf(tokens[1])*8);
				Double count   = (Double.valueOf(tokens[1]));
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
			//String line = br3.readLine();
			while( (line = br3.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				//Double count   = (Double.valueOf(tokens[1])*7.912); //fujisawa
				Double count   = (Double.valueOf(tokens[1]));  // tokyo
				//Double count   = (Double.valueOf(tokens[1]));
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

				//				if(countds > 0){
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

				bw.write(mc + "\t" + zonecode + "\t" + countpt + "\t" + countds + "\t" + diff + "\t" + (diff)/countpt + "\t" + wkt);
				//bw.write(mc + "," + zonecode + "," + countpt + "," + countds + "," + diff + "," + Math.abs(diff)/countpt + "," + wkt);
				bw.newLine();
				//			}
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
		//		System.out.println("rmse: " + RMSE);
		return RMSE;
	}

	public static Double getErrorPercentage(File pt, File zdc, File meshlist){ 
		Map<String, Double> ptmap = intomap(pt);
		Map<String, Double> zdcmap = intomap2(zdc);
		ArrayList<String> meshes = Simulation_ver2.getMeshlist(meshlist);
		//		System.out.println("meshes: " + meshes);
		ArrayList<Double> templist = new ArrayList<Double>();
		for(String mc:meshes){
			double temp= 0;
			double countpt = 0d;
			double countds = 0d;
			if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
			if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}
			if(countds != 0){
				double diff = (countpt - countds);
				temp = (diff/countds)*100;
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

	public static Double getCorrelation(File input1, File input2, File meshcodefile){
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
			String line = br3.readLine();
			while( (line = br3.readLine()) != null ) {
				String[] zdctokens = line.split("\t");
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
			System.out.println("File not found 3");}
		catch(IOException e) {System.out.println(e);}

		try{
			BufferedWriter tempwriter = new BufferedWriter(new FileWriter(temp));
			for(String mc:meshcodeset){
				int countpt = 0;
				int countds = 0;
				if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
				if(zdcmap.containsKey(mc)){countds = (int)Math.floor(zdcmap.get(mc));}
				tempwriter.write(countpt + "," + countds);
				tempwriter.newLine();
			}
			tempwriter.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found 2");
		}
		catch(IOException e) {
			System.out.println(e);
		}

		int counter = meshcodeset.size();
		//隕ｳ貂ｬ繝�繝ｼ繧ｿ縺ｨ縺ｮ逶ｸ髢｢繧定ｨ育ｮ�
		Vector v1 = new Vector();
		Vector v2 = new Vector();
		Correlation.readTextFromFile_AndSetVector(temp,v1);
		Correlation.KataHenkan(v1,v2);
		double cor  = Correlation.getCorrelationCoefficient(v2);
		return cor;
	}

}
