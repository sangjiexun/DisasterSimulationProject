package Tools;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.geom.Mesh;
import jp.ac.ut.csis.pflow.geom.smoothing.Kde.IKernel;

public class Kernel {
	public static void main(String[] args){
		//read file

		for(int i = 6; i<=12 ; i++){
			File gpsfile = new File("C:/Users/yabec_000/Desktop/Snow_Tokyo_"+ i + ".csv");
			File smthfile = new File("c:/Users/yabec_000/Desktop/Snow_Tokyo_" + i + "_smoothed.csv");

			//aggregate&magfac into mesh
			Map<String, Integer> meshdata = Aggregate(gpsfile); // Tokyo Version
			System.out.println("done aggregation");

			//smooth by Kde.estimate2D
			Rectangle2D box = GetMaxMin(gpsfile);
			Map<String,Double> result = estimate2d(meshdata, box.getMinX(),box.getMaxX(),box.getMinY(),box.getMaxY(),
					Mesh.LNG_WIDTH_MESH5.doubleValue(),
					Mesh.LAT_HEIGHT_MESH5.doubleValue(),
					Mesh.LNG_WIDTH_MESH5.doubleValue()/3,
					Mesh.LAT_HEIGHT_MESH5.doubleValue()/3,
					GAUSSIAN,
					getSum(meshdata));
			System.out.println("done smoothing");

			System.out.println(getSum(meshdata));

			//file出力 outfile
			exportMap(smthfile, result);

			//		Double cor = getCorrelation(zdcfile , smthfile, meshcodes);
			//		System.out.println(cor);
		}
	}

	public static Map<String, Integer> Aggregate(File infile){
		try{
			BufferedReader br1 = new BufferedReader(new FileReader(infile));
			Integer counter = 0;
			String line = null;
			MeshAggregation volume = new MeshAggregation(5);
			while( (line = br1.readLine()) != null ) {
				String[] tokens = line.split(",");
				String pid = tokens[0];
				double longitude = Double.parseDouble(tokens[1]);
				double latitude = Double.parseDouble(tokens[2]);
				LonLat pos = new LonLat (longitude, latitude);
				volume.aggregate(pid,0,pos,1,1);
				counter = counter +1;
			}
			br1.close();
			double magfac = 400;
			Map<String, Integer> meshh = volume.getTrafficCount();
			for(String mesh: meshh.keySet()){
				int count = meshh.get(mesh);
				Double excount = count * magfac;
				meshh.put(mesh, excount.intValue());
			}
			//volume.export(new File("C:/Users/yabec_000/Desktop/FujisawaGPS_normal_mesh.csv"));
			return meshh;
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found" + e.getLocalizedMessage());
			e.printStackTrace();
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return null;
	}

	public static Integer getSum(Map<String, Integer> map){
		Integer sum = 0;
		for (String k:map.keySet()){
			sum = sum + map.get(k);
		}
		return sum;
	}

	public static Rectangle2D GetMaxMin(File infile){
		try{
			BufferedReader br1 = new BufferedReader(new FileReader(infile));

			String line = null;
			Double xmax = Double.MIN_VALUE;
			Double xmin = Double.MAX_VALUE;
			Double ymax = Double.MIN_VALUE;
			Double ymin = Double.MAX_VALUE;

			while( (line = br1.readLine()) != null ) {
				String[] tokens = line.split(",");
				double longitude = Double.parseDouble(tokens[1]);
				double latitude = Double.parseDouble(tokens[2]);
				xmin = Math.min(xmin, longitude);
				xmax = Math.max(xmax, longitude);
				ymin = Math.min(ymin, latitude);
				ymax = Math.max(ymax, latitude);
			}
			br1.close();
			return new Rectangle2D.Double(xmin, ymin, xmax-xmin, ymax-ymin);
		}
		catch(FileNotFoundException e) {
			System.out.println("File not found 2");
		}
		catch(IOException e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 * 観測値から確率密度を推定
	 * @param data　観測値(2D)
	 * @param minx　確率変数x最小値
	 * @param maxx　確率変数x最大値
	 * @param miny　確率変数y最小値
	 * @param maxy　確率変数y最大値
	 * @param rangex　確率変数xのグリッド幅
	 * @param rangey　確率変数yのグリッド幅
	 * @param h1　xバンド幅
	 * @param h2 yバンド幅
	 * @param kernel　カーネル関数
	 * @return　推定結果(key=position, value=estimated value)
	 */

	//mesh aggregation o
	public static Map<String,Double> estimate2d(Map<String, Integer> data,double minx,double maxx,double miny,double maxy,
			double rangex,double rangey,double h1,double h2,IKernel kernel, Integer totalpop)
			{
		Map<String,Double> result = new LinkedHashMap<String,Double>();
		double D = 0d;
		// density estimation ///////////////////
		for(double x=minx;x<=maxx;x+=rangex) {
			for(double y=miny;y<=maxy;y+=rangey) {
				double sum = 0d;
				for(String d : data.keySet()) {
					Mesh mesh = new Mesh(d);
					LonLat point = mesh.getCenter();
					//count*kernel密度
					sum += data.get(d)*kernel.getDensity(x,point.getLon(),h1) * kernel.getDensity(y,point.getLat(),h2);
				}
				double density = data.size()==0 ? 0d : sum/(data.size()*h1*h2);
				D += density;
				result.put(new Mesh(5,x,y).getCode(),density);
			}
		}
		// normalize;
		for(String p:result.keySet()) { result.put(p, result.get(p)* totalpop/ D); }
		return result;
			}

	public static void exportMap(File outfile, Map<String, Double> result) {
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			//			bw.write("mesh , count , wkt");
			//			bw.newLine();
			for (String mesh: result.keySet()){
				Mesh  mesh1     = new Mesh(mesh);
				Rectangle2D.Double rect = mesh1.getRect();
				String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",
						rect.getMinX(),rect.getMinY(),
						rect.getMinX(),rect.getMaxY(),
						rect.getMaxX(),rect.getMaxY(),
						rect.getMaxX(),rect.getMinY(),
						rect.getMinX(),rect.getMinY());
				bw.write(mesh + "\t" + (int)Math.floor(result.get(mesh)) +"\t"+ wkt);
				//				bw.write(mesh + "," + (int)Math.floor(result.get(mesh)) +","+ wkt);
				bw.newLine();
			}
			bw.close();
		}
		catch(IOException e){System.out.println("oh no");
		}
	}
	//	public static Double getCorrelation(File gpsinput, File input2, File meshcodefile){
	//		//観測データと比較
	//		Map<String,Integer> ptmap = new HashMap<String, Integer>();
	//		Map<String,Integer> zdcmap = new HashMap<String, Integer>();
	//		String temp = ("c:/Users/yabec_000/Desktop/resultfile.csv");
	//		Set<String> meshcodeset = new HashSet<String>();
	//
	//		try{
	//			BufferedReader brm = new BufferedReader(new FileReader(meshcodefile));
	//			String linemesh = null;
	//			while((linemesh = brm.readLine()) != null){
	//				meshcodeset.add(linemesh);
	//			}
	//			brm.close();
	//		}
	//		catch(FileNotFoundException z) {System.out.println("File not found 1");}
	//		catch(IOException e) {System.out.println(e);}
	//
	//		try{
	//			BufferedReader br3 = new BufferedReader(new FileReader(gpsinput));
	//			String line = br3.readLine();
	//			while( (line = br3.readLine()) != null ) {
	//				String[] zdctokens = line.split(",");
	//				String meshcode = zdctokens[0];
	//				Integer count   = Integer.valueOf(zdctokens[1]);
	//				zdcmap.put(meshcode, count);
	//			}
	//			br3.close();
	//		}
	//		catch(FileNotFoundException z) {
	//			System.out.println("File not found 2");}
	//		catch(IOException e) {System.out.println(e);}
	//
	//		try{
	//			BufferedReader br4 = new BufferedReader(new FileReader(input2));
	//			String line4 = br4.readLine();
	//			while( (line4 = br4.readLine()) != null ) {
	//				String[] pttokens = line4.split(",");
	//				String meshcode = pttokens[0];
	//				Double counts   = Double.valueOf(pttokens[1]);
	//				Integer intcount = (int)Math.floor(counts);
	//				ptmap.put(meshcode, intcount);
	//				//				meshcodeset.add(meshcodes);
	//			}
	//			br4.close();
	//		}
	//		catch(FileNotFoundException z) {
	//			System.out.println("File not found 3");}
	//		catch(IOException e) {System.out.println(e);}
	//
	//		try{
	//			BufferedWriter tempwriter = new BufferedWriter(new FileWriter(temp));
	//			for(String mc:meshcodeset){
	//				int countpt = 0;
	//				int countds = 0;
	//				if(ptmap.containsKey(mc)){countpt = ptmap.get(mc);}
	//				if(zdcmap.containsKey(mc)){countds = zdcmap.get(mc);}
	//				tempwriter.write(countpt + "," + countds);
	//				tempwriter.newLine();
	//			}
	//			tempwriter.close();
	//		}
	//		catch(FileNotFoundException e) {
	//			System.out.println("File not found 2");
	//		}
	//		catch(IOException e) {
	//			System.out.println(e);
	//		}
	//
	//		int counter = meshcodeset.size();
	//		//観測データとの相関を計算
	//		Vector v1 = new Vector();
	//		Vector v2 = new Vector();
	//		Correlation.readTextFromFile_AndSetVector(temp,v1);
	//		Correlation.KataHenkan(v1,v2);
	//		double cor  = Correlation.getCorrelationCoefficient(v2);
	//		return cor;
	//	}

	public static final IKernel GAUSSIAN = new IKernel() {
		public double getDensity(double x,double xi,double h) {
			double v = (x-xi)/h;
			return Math.exp(-1*v*v/2) / Math.sqrt(2*Math.PI);
		}
	};
}

