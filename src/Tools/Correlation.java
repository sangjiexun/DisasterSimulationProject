package Tools;
import java.awt.Frame;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

public class Correlation extends Frame {

	//データファイル名
	static String DATAFILE = null;

	public static void main (String args []) {

		//線形最小二乗法によって得られた定数
		//y=ax+b のa,b を格納する。val[0]=a,val[1]=b
		//基本型は参照渡しできないのでこうしてみる
		double val[] = new double[2];
		//ファイルから読み込んだデータの一時保管用
		Vector v1 = new Vector();
		//ファイルから読み込んだ元データは文字列であるため，
		//数値に変換したものをこちらに格納する。
		Vector DataA = new Vector();


		//データファイル読み込み
		try{
			if (args.length ==0 ){
				readTextFromFile_AndSetVector(DATAFILE,v1);
			} else {
				readTextFromFile_AndSetVector(args[0],v1);
			}
		}
		catch(Exception e){
			System.out.println(e.toString());
			System.exit(-1);
		}// of try catch

		//一時読み込みしたデータは文字列なので数値に変換し，
		//配列にセットする
		KataHenkan(v1,DataA);
		//得られた数値データに最小二乗法を適用し，近似

		//相関係数を求める
		double r = getCorrelationCoefficient(DataA);

//		//相関係数を出力
//		System.out.println("r = " + r);

	}

	/*
	 * 目的  : 相関係数を求める
	 * 引数  : data 数値データの配列への参照
	 * 戻り値 : 相関計数値
	 */
	public static double getCorrelationCoefficient(Vector data){
		//相関係数を求めるために用意する一時的な変数
		double XAve = 0; //観測値のx 成分の平均値
		double YAve = 0; //観測値のy 成分の平均値
		double XVari = 0; //x の分散
		double YVari = 0; //y の分散
		double XYVari = 0; //xy の共分散

		XAve = getAverage(data,"x");
		YAve = getAverage(data,"y");

		XVari = getVariance(data,"x",XAve,YAve);
		YVari = getVariance(data,"y",XAve,YAve);
		XYVari = getVariance(data,"xy",XAve,YAve);

		return XYVari / (Math.sqrt(XVari * YVari));
	}// end of getCorrelationCoefficient

	/*
	 * 目的   : 分散や共分散を計算する
	 * 引数   : data 数値データの配列への参照
	 *       : axis "x" or "y" or "xy"
	 *       : xave x の平均値
	 *       : yave y の平均値
	 * 戻り値 : 分散（または共分散）
	 */
	static double getVariance(Vector data,String axis,
			double xave,double yave){
		double xvari = 0;
		double yvari = 0;
		double xyvari = 0;

		double tempvalX = 0;
		double tempvalY = 0;
		double tempvalXY = 0;
		double x,y;
		Point temppos;
		for (int i=0; i<data.size(); ++i){
			temppos = (Point)data.get(i);
			x = (double) temppos.getX();
			tempvalX += Math.pow(x - xave,2);
			y = (double) temppos.getY();
			tempvalY += Math.pow(y - yave,2);
			tempvalXY += (x - xave) * (y - yave);
		}
		if (axis =="x") {
			return tempvalX / data.size();
		} else if (axis =="y") {
			return tempvalY / data.size();
		} else if (axis =="xy") {
			return tempvalXY / data.size();
		} else {
			return Double.NaN;
		}
	}// end of getVariance


	/*
	 * 目的   : x 座標かy 座標かのデータの平均値を計算する
	 * 引数   : data 数値データの配列への参照
	 *       : axis "x" or "y"
	 * 戻り値 : 平均値
	 */
	static double getAverage(Vector data,String axis){
		double tempvalX = 0;
		double tempvalY = 0;
		double x,y;
		Point temppos;
		for (int i=0; i<data.size(); ++i){
			temppos = (Point)data.get(i);
			x = (double) temppos.getX();
			tempvalX += x;
			y = (double) temppos.getY();
			tempvalY += y;
		}
		if (axis =="x") {
			return tempvalX / data.size();
		} else if (axis =="y") {
			return tempvalY / data.size();
		} else {
			return Double.NaN;
		}
	}// end of getAverage

	/*
	 * 目的 : CSV の数値データを数値型に型変換
	 * 引数 : v 文字列のデータを格納したVector
	 *       Data 変換後のデータを格納したVector
	 */
	public static void KataHenkan(Vector v, Vector Data){
		for(int i=0; i <= (v.size()-1); i++){
			String str = (String)v.get(i);
			StringTokenizer st
			= new StringTokenizer(str, ",");
			Point pos =
					new Point(
							Integer.parseInt((String)st.nextToken()),
							Integer.parseInt((String)st.nextToken())
							);
			Data.add(pos);
		}// of for i
	}// end of static void KataHenkan


	/*
	 * 目的 : CSV ファイルから1 行ずつデータを読み込む
	 * 引数 : filename データファイルのファイル名
	 *       v データファイルから読み込んだデータ
	 */
	public static void readTextFromFile_AndSetVector
	(String filename,Vector v) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String rdata;
			String alldata = "";
			while((rdata = br.readLine()) != null) {
				v.add(rdata);
			}// of while
			fr.close();
		}catch(Exception e){
			System.out.println(e);
		}// of try catch
	}// of readTextFromFile_AndSetVector
}// end of class Sample_Correlation