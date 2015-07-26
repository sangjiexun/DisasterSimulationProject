package Tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GraphMaker {

	public static void main(String args[]){

		File out = new File ("c:/users/yabetaka/desktop/graph_seiki.csv");
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));	
			for (int i = 1; i <= 10000; i++){
				Random ran = new Random();
				double dig = ran.nextDouble();
				double x = dig*0.5-0.25;
				//				double x = 0;
//				bw.write(x + "," + function(x, 0, 0.05));
				bw.newLine();
				if(i % 1000 == 0){
					System.out.println(i);
				}
			}
			bw.close();
		}
		catch(FileNotFoundException z) {
			System.out.println("File not found pt");
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}

	public static double seiki(double x, double avg, double sigma){
		double y = (1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp( - ((x - avg) * (x - avg)) / (2 * sigma * sigma));
		return y;
	}

//	public static double BinomialDis(double n, double x, double p){
//		int y = (int)Math.floor(x);
//		double b = ((Math.pow(n*p, y))*(Math.exp(-(n*p))))/kaijo(y);
//		return b;
//	}

	public static double kaijo(int x){
		int kaijo = 1;
		if(x > 0){
			for(int i = 0; i<=x; i++){
				kaijo = kaijo * i;
			}
		}
		else {
			kaijo = 1;
		}
		return kaijo;
	}

}
