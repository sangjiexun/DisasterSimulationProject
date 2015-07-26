package PTtoPFlow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.STPoint;

import org.apache.commons.lang.text.StrTokenizer;

/**
 * class for handling midterm data of PT interpolation
 * @author H.Kanasugi@EDITORIA. UTokyo.
 * @since 2014/04/29
 * @version 0.0.0.1
 */
public class MidtermData {
	/* ==============================================================
	 * static fields
	 * ============================================================== */
	/** time stamp	*/	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyyMMddHHmm");

	/** station data file	*/
	private static final String STATION_FILE = System.getProperty("pflow.ptinterpolation.station_file",
		System.getProperty("user.dir") + "/station_names.csv");
	/**	station names created from Expert Station Table		*/
	private static final Map<String,String> STATION_CODES = new TreeMap<String,String>();
	/* load station data if exists */
	static {
		File file = new File(STATION_FILE);
		// if file exists
		if( !file.exists() ) {
//			System.out.println("station file is not found: " + file.getAbsolutePath());
		}
		else {
			System.out.println("loading station file: " + file.getAbsolutePath());
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"MS932"));
				String line = br.readLine();	// skip header
				while( (line=br.readLine()) != null ) {
					String tokens[] = StrTokenizer.getCSVInstance(line).getTokenArray();
					STATION_CODES.put(tokens[0],tokens[1]);
				}
				br.close();
			}
			catch(IOException exp) { exp.printStackTrace(); }
			finally {
				try { if (br != null) { br.close(); } }
				catch(IOException exp) { exp.printStackTrace(); }
			}
		}
	}


	/* ==============================================================
	 * static methods
	 * ============================================================== */
	/**
	 * parse instance with line data
	 * @param line line data from midterm file
	 * @return object
	 */
	public static MidtermData parse(String line) {
		MidtermData data = new MidtermData();
		try {
			data._pid       = line.substring( 76, 84);
			data._tno       = line.substring( 84, 86);
			data._sno       = line.substring( 88, 90);
			data._sex       = Integer.parseInt( line.substring(  8,  9) );
			data._age       = Integer.parseInt( line.substring(  9, 11) );
			data._padd      = line.substring(  0,  8);
			data._work      = Integer.parseInt( line.substring( 11, 13) );
			data._workplace = line.substring( 13, 21);
			data._goalloc   = line.substring( 44, 52);
			data._purpose   = Integer.parseInt( line.substring( 66, 68) );
			data._magfac1   = Integer.parseInt( line.substring( 68, 72) );
			data._magfac2   = Integer.parseInt( line.substring( 72, 76) );
			data._transport = Integer.parseInt( line.substring( 90, 92) );
			data._routeType = Integer.parseInt( line.substring( 92, 93) );
			// origin and destination
			double deplon   = Double.parseDouble( line.substring(190,200) );
			double deplat   = Double.parseDouble( line.substring(180,189) );
			double arrlon   = Double.parseDouble( line.substring(211,220) );
			double arrlat   = Double.parseDouble( line.substring(201,210) );
			Date   depts    = SDF_TS.parse(line.substring( 93,105));
			Date   arrts    = SDF_TS.parse(line.substring(105,117));
			data._dep       = new STPoint(depts,deplon,deplat);
			data._arr       = new STPoint(arrts,arrlon,arrlat);
			// station names
			String depcode  = line.substring(121,129).trim();
			String arrcode  = line.substring(129,137).trim();
			data._depStation= STATION_CODES.get(depcode);	// set null if unavailable code
			data._arrStation= STATION_CODES.get(arrcode);	// set null if unavailable code
		}
		catch(ParseException exp) { exp.printStackTrace(); data = null; }
		return data;
	}


	/* ==============================================================
	 * instance fields
	 * ============================================================== */
	/** person ID	*/	private String  _pid;		// Person ID
	/** Trip No		*/	private String	_tno;		// Trip ID
	/** Sub-trip No	*/	private String	_sno;		// Subtrip ID
	/** sex code	*/	private int		_sex;		// sex/gender
	/** age code	*/	private int		_age;		// age
	/** address code*/	private String	_padd;		// person address
	/** work code	*/	private int		_work;		// work/job
	/** workplace   */	private String	_workplace;	// workplace
	/** goal location */private String  _goalloc;    // goal location
	/** purpose code*/	private int		_purpose;	// trip purpose
	/** expansion 1	*/	private int		_magfac1;	// expansion factor1
	/** expansion 2	*/	private int		_magfac2;	// expansion factor2
	/** datum(trans)*/	private int		_transport;	// transportation
	/** dep point	*/	private STPoint _dep;
	/** arr point	*/	private STPoint _arr;
	/** route type	*/	private int     _routeType;	// 5:stay, 4:railway, 2:tollroad, others:road
	/** dep station	*/	private String  _depStation;// station
	/** arr station	*/	private String  _arrStation;// station



	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * PID取得
	 * @return PID
	 */
	public String getPID() {
		return _pid;
	}

	/**
	 * Goal Location取得
	 * @return Goal Location
	 */

	public String getGoalloc() {
		return _goalloc;
	}

	/**
	 * トリップ番号を取得
	 * @return トリップ番号
	 */
	public String getTripNo() {
		return _tno;
	}

	/**
	 * サブトリップ番号を取得
	 * @return サブトリップ番号
	 */
	public String getSubTripNo() {
		return _sno;
	}

	/**
	 * 性別コードを取得
	 * @return 性別コード
	 */
	public int getSex() {
		return _sex;
	}

	/**
	 * 年齢コードを取得
	 * @return 年齢コード
	 */
	public int getAge() {
		return _age;
		//		return _age - 1;	// TODO to fix Tokyo 2008
	}

	/**
	 * 住所コードを取得
	 * @return 住所コード
	 */
	public String getAddress() {
		return _padd;
	}

	/**
	 * 職業コードを取得
	 * @return 職業コード
	 */
	public int getWork() {
		return _work;
	}

	/**
	 * 職場コードを取得
	 * @return 職場コード
	 */
	public String getWorkplace(){
		return _workplace;
	}

	/**
	 * 移動目的コードを取得
	 * @return 移動目的コード
	 */
	public int getPurpose() {
		return _purpose;
	}

	/**
	 * 拡大係数1を取得
	 * @return 拡大係数1
	 */
	public int getExfactor1() {
		return _magfac1;
	}

	/**
	 * 拡大係数2を取得
	 * @return 拡大係数2
	 */
	public int getExfactor2() {
		return _magfac2;
	}

	/**
	 * 交通手段を取得
	 * @return 交通手段
	 */
	public int getTransport() {
		return _transport;
	}

	/**
	 * get route interpolation type. 5:stay, 4:railway, 2:tollroad, others:road
	 * @return route type code
	 */
	public int getRouteType() {
		return _routeType;
	}

	/**
	 * check if this subrip stays
	 * @return true if stays
	 */
	public boolean isStay() {
		return _routeType == 5;
	}
	/**
	 * check if this sub-trip uses railway
	 * @return true if uses railway
	 */
	public boolean useRailway() {
		return _routeType == 4;
	}
	/**
	 * check if this sub-trip uses tollroad(express way)
	 * @return true if uses tollroad
	 */
	public boolean useTollroad() {
		return _routeType == 2;
	}

	/**
	 * get departure position with time stamp
	 * @return departure position
	 */
	public STPoint getDepPoint() {
		return _dep;
	}
	/**
	 * get arrival position with time stamp
	 * @return arrival position
	 */
	public STPoint getArrPoint() {
		return _arr;
	}
	/**
	 * get departure station name
	 * @return departure station name
	 */
	public String getDepStation() {
		return _depStation;
	}
	/**
	 * get arrival station name
	 * @return arrival station name
	 */
	public String getArrStation() {
		return _arrStation;
	}
}