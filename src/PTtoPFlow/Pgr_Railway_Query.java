package PTtoPFlow;
import java.util.Arrays;
import java.util.List;

import jp.ac.ut.csis.pflow.routing.pgr.query.PgrQuery;
import jp.ac.ut.csis.pflow.routing.pgr.query.IPgrQuery;
import jp.ac.ut.csis.pflow.routing.res.Node;

import org.apache.commons.lang.StringUtils;

/* table schema
CREATE TABLE data2003.railnetwork (
  gid integer,
  compname character varying(32),
  linename character varying(32),
  stncode0 integer,
  stnname0 character varying(32),
  stncode1 integer,
  stnname1 character varying(32),
  length double precision,
  stngeom0 geometry(Point,4326),
  stngeom1 geometry(Point,4326),
  the_geom geometry(LineString,4326),
  source integer,
  target integer,
  expertname0 character varying(256),
  expertname1 character varying(256),
  timecost0 integer,
  timecost1 integer
);
 */
/**
 * Class for Railway query generator (Railway network data is now under maintenance at 2014.06.06)
 * @author H.Kanasugi@EDITORIA. UTokyo.
 * @since 2014/06/06
 * @version 0.0.0.1
 */
public class Pgr_Railway_Query extends PgrQuery {
	private static final String PGR_RAILWAY_TABLE = null;


	/* ==============================================================
	 * constructors
	 * ============================================================== */
	/**
	 * instantiate object with default values. version is 2.0
	 */ 
	public Pgr_Railway_Query() {
		this(Version.V2_0);
	}
	
	/**
	 * instantiate object with version code
	 * @param v version code
	 */
	public Pgr_Railway_Query(Version v) {
		super(v,DEFAULT_BUF_SIZE*2);
	}
	
	
	/* ==============================================================
	 * instance methods
	 * ============================================================== */
	/* @see jp.ac.ut.csis.pflow.routing.pgr.query.PgrQuery#getLinkTableName() */
	@Override
	public String getLinkTableName() { 
		return PGR_RAILWAY_TABLE; 
	}
	
	/* @see jp.ac.ut.csis.pflow.routing.pgr.query.PgrQuery#getNodeTableName() */
	@Override
	public String getNodeTableName() {
		return PGR_RAILWAY_TABLE;
	}
	
	/* @see jp.ac.ut.csis.pflow.routing.pgr.query.PgrQuery#getRoutingQuery(jp.ac.ut.csis.pflow.routing.res.Node, jp.ac.ut.csis.pflow.routing.res.Node) */
	@Override
	public String getRoutingQuery(Node node0, Node node1) {
		String pgrSql = getPgrSql(node0,node1); 
		
		return  getVersion().equals(Version.V2_0) ? 
				// case version 2.0 +++++++++++++++++++++++
				String.format(
					"select r.seq,r.cost,r.id1 as node,r.id2 as link,"
					+ "case when r.id1=n.source then n.stngeom0 else n.stngeom1 end as p_geom,"	// station position
					+ "case when r.id1=n.source then n.the_geom else st_reverse(n.the_geom)  end as l_geom "	// railway geometry
					+ "from "
					+ "  (select * from pgr_dijkstra('%s',%s,%s,false, false)) as r,"
					+ "  %s as n "
					+ "where r.id2=n.gid order by r.seq",
					pgrSql,node0.getId(),node1.getId(),getLinkTableName()) 
				:
				// case version 1.05 ++++++++++++++++++++++
				String.format(
					"SELECT r.seq,r.cost,r.vertex_id AS node,r.edge_id as link,"
					+ "CASE WHEN r.vertex_id=n.source THEN n.stngeom0 ELSE n.stngemo1 END AS p_geom,"
					+ "CASE WHEN r.vertex_id=n.source THEN n.the_geom ELSE ST_Reverse(n.the_geom)  END AS l_geom "
					+ "FROM "
					+ "  (SELECT row_number() over() AS seq,* FROM shortest_path('%s',%s,%s,false,false)) AS r,"
					+ "  %s AS n "
					+ "WHERE r.edge_id=n.gid ORDER BY r.seq",
					pgrSql,node0.getId(),node1.getId(),getLinkTableName()) ;
	}

	/* @see jp.ac.ut.csis.pflow.routing.pgr.query.PgrQuery#getNodeQuery(double, double) */
	@Override
	public String getNodeQuery(double lon, double lat) {
		List<String> wheres = listNodeConds(lon,lat);
		
		String table = getNodeTableName();
		String point = String.format("ST_GeomFromText('POINT(%f %f)',4326)",lon,lat);
		String spoint= "stngeom0"; // String.format("ST_StartPoint(the_geom)");
		String epoint= "stngeom1"; // String.format("ST_EndPoint(the_geom)");
		String cond  = wheres.isEmpty() ? "" : "where " + StringUtils.join(wheres," AND ");
		
		return	String.format("select node,d,n_geom from (") + 
				String.format("  select source as node,ST_Distance(%s,%s) as d,%s as n_geom from %s %s",point,spoint,spoint,table,cond) + 
				" union " +
				String.format("  select target as node,ST_Distance(%s,%s) as d,%s as n_geom from %s %s",point,epoint,epoint,table,cond) + 
				") as b order by d limit 1";
	}

	
	/* @see jp.ac.ut.csis.pflow.routing.pgr.query.PgrQuery#listLinkConds(jp.ac.ut.csis.pflow.routing.res.Node, jp.ac.ut.csis.pflow.routing.res.Node) */
	@Override
	public List<String> listLinkConds(Node node0,Node node1) {
		return Arrays.asList();	// bbox is not necessarily required because railway includes smaller amount of links rather than roads
	}
}
