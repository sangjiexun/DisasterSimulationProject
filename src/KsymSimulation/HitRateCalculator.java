package KsymSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Tools.MeshLevelConverter;
import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.Mesh;

public class HitRateCalculator {

	// TO DO : geomchecker to limit meshcodes
	static File shapedir = new File("C:/Users/yabec_000/Desktop/TokyoEQProject/Tokyo3WardZone");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static void main(String args[]){

		int opthour = 1;
		int exp = 4;
		double K = 1.2; //
		double mul = 1; //平時で平均人口の何倍以上のメッシュを対象にする
		
		int time = exp + 14;
		double meshpop = getAvgofNormal(time);
		System.out.println(meshpop);

		int yes = 0;
		int count = 0;
		int morethanmeshpop = 0;
		int total = 0;

		double hitrate = 0;

		File in = new File ("c:/Users/yabec_000/Desktop/TokyoOptExp/mofo"+opthour+exp+".csv"); 
//		File in4 = new File ("c:/Users/yabec_000/Desktop/TokyoOptExp/mofo"+opthour+exp+".csv"); 
//		MeshLevelConverter.mesh5to4(in5,in4);
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			String line = null;
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				Double sim = Double.parseDouble(tokens[2]);
				Double obs = Double.parseDouble(tokens[3]);
				Double normal = getNormal(meshcode);
				if(normal > (meshpop*mul)){   //普段の人口が4000/divより大きい場合
					if((obs/normal)> K){      //災害時が平時のk倍以上ならcount
						if((sim/normal) > 1){ //推定値も平時のk倍以上ならyes
							yes++;
						}
						count++;
					} 
					morethanmeshpop++;
				}
				total++;
			}
			double yesd = (double)yes;
			double countd = (double)count;
			hitrate = (yesd/countd) * 100;
			br.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		System.out.println("hitrate: "+ hitrate + ", 災害時が平時のk倍以上なmesh: " + count);
		System.out.println("普段の人口が平均人口*mulより大きいmesh: "+ morethanmeshpop);
		System.out.println("all meshes: " + total);	
	}

	public static double getNormal(String meshcode){
		File normalobs = new File("C:/Users/yabec_000/Desktop/ZDCKonzatsuToukeiData/ZDC_20110309_5_15.csv");
		double normal = 0d;
		try{
			BufferedReader br = new BufferedReader(new FileReader(normalobs));
			String line = null;
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String mesh = tokens[0];
				if(mesh.equals(meshcode)){
					normal = Double.parseDouble(tokens[1]);
				}
			}
			br.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return normal;
	}

	public static double getAvgofNormal(int hour){
		File normalobs = new File("C:/Users/yabec_000/Desktop/ZDCKonzatsuToukeiData/ZDC_20110309_5_"+hour+".csv");
		double sum = 0;
		double avg = 0;
		ArrayList<Double> list = new ArrayList<Double>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(normalobs));
			String line = null;
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split("\t");
				String meshcode = tokens[0];
				Mesh mesh = new Mesh(meshcode);
				if(gchecker.checkOverlap(mesh.getCenter().getLon(),mesh.getCenter().getLat())==true){
					Double pop = Double.parseDouble(tokens[1]);
					list.add(pop);
				}
			}
			br.close();

			for(Double p:list){
				sum = sum + p;
			}

			avg = sum/list.size();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return avg;
	}
}
