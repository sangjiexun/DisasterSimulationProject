package PTtoPFlow;


import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jp.ac.ut.csis.pflow.geom.LonLat;

/**
 * Class for representing map matching score
 * @author H.Kanasugi@EDITORIA. UTokyo.
 * @since 2014/06/16
 * @version 0.0.0.1
 */
public class MMScore {
	/* ==============================================================
	 * instance fields
	 * ============================================================== */	
	/** input point for map matching	*/	private LonLat       _input;
	/** output point from map matching	*/	private LonLat       _output;
	/** matching link id if exists		*/	private String       _linkid; 
	/** distance between in and out		*/	private double       _dist;
	/** attribute List	 				*/ 	private List<String> _attrs;
	

	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * initialization
	 * @param in input point
	 * @param out output point
	 * @param dist distance(m)
	 */
	protected MMScore(LonLat in,LonLat out,double dist) {
		this(in,out,null,dist,null);
	}

	/**
	 * initialization
	 * @param in input point
	 * @param out output point
	 * @param linkid link id
	 * @param dist distance(m)
	 */
	protected MMScore(LonLat in,LonLat out,String linkid,double dist) {
		this(in,out,linkid,dist,null);
	}
	
	/**
	 * initialization
	 * @param in input point
	 * @param out output point
	 * @param dist distance (m)
	 * @param attrs attribute list
	 */
	protected MMScore(LonLat in,LonLat out,double dist,List<String> attrs) {
		this(in,out,null,dist,attrs);
	}
	
	/**
	 * initialization
	 * @param in input point
	 * @param out output point
	 * @param linkid link ID
	 * @param dist distance (m)
	 * @param attrs attribute list
	 */
	protected MMScore(LonLat in,LonLat out,String linkid,double dist,List<String> attrs) {
		_dist   = dist;
		_input  = in;
		_output = out;
		_linkid = linkid;
		_attrs  = attrs;
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/**
	 * get input point 
	 * @return input point
	 */
	public LonLat getInputPoint() {
		return _input;
	}
	
	/**
	 * get output point
	 * @return output point from map matching. returns null when failed to matching
	 */
	public LonLat getOutputPoint() {
		return _output;
	}
	
	/**
	 * get link ID
	 * @return link Id
	 */
	public String getLinkId() {
		return _linkid;
	}
	
	/**
	 * get distance between input and output points
	 * @return distance(m)
	 */
	public double getDistance() {
		return _dist;
	}
	
	/**
	 * get additional attributes
	 * @return attributes
	 */
	public List<String> getAttributes() {
		return _attrs;
	}
	
	/**
	 * format output string with tab delimiter
	 * @return output string
	 */
	public String toResultString() {
		return toResultString("\t");
	}
	
	/**
	 * format output string
	 * @param delim delimiter
	 * @return output string
	 */
	public String toResultString(String delim) {
		DecimalFormat df  = new DecimalFormat("###.######");
		List<String>  val = Arrays.asList(
								df.format(_input.getLon()),
								df.format(_input.getLat()),
								_output!=null?df.format(_output.getLon()):"-",
								_output!=null?df.format(_output.getLat()):"-",
								_output!=null?String.valueOf(_dist):"-",
								_linkid!=null?_linkid : "-"
							);
		return StringUtils.join(val,delim) + (_attrs != null && !_attrs.isEmpty() ? delim + StringUtils.join(_attrs,delim) : "");
	}
	
}
