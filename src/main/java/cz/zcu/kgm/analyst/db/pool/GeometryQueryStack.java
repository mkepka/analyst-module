package cz.zcu.kgm.analyst.db.pool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.zcu.kgm.analyst.db.model.KmlGeometryFeature;

/**
 * Class with stack containing previous queries 
 * @author jezekjan
 *
 */
public class GeometryQueryStack {

    private static final Map<String, TheQuery> queries = Collections.synchronizedMap(new HashMap<String, TheQuery>());

    /**
     * Method prevents cycling of SQL queries from one user
     * @param id - name of user
     * @param query - query to be run
     * @return
     * @throws SQLException
     */
    public static List<KmlGeometryFeature> getKmlGF(String id, String query) throws SQLException {
        TheQuery newQ = new TheQuery(query);
        synchronized (queries){
            TheQuery oldQ = queries.get(id);
            if (oldQ != null) {
                oldQ.cancel();
                queries.remove(id);
            }
            queries.put(id, newQ);
        }
        List<KmlGeometryFeature> gfl = newQ.run();
        queries.remove(id);
        return gfl;
    }
}

class TheQuery {
    String query;
    Statement statement;
    PooledConnection pConn;
    ResultSet res;

    TheQuery(String q) throws SQLException {
        this.query = q;
        this.pConn = SQLExecutor.getConnection();
        statement = pConn.createStatement();
        statement.execute("SET statement_timeout=20000;");
    }

    /**
     * Method runs query
     * @return
     * @throws SQLException
     */
    public List<KmlGeometryFeature> run() throws SQLException {
        try {
            ResultSet rs = statement.executeQuery(query);
            List<KmlGeometryFeature> gfl = new LinkedList<KmlGeometryFeature>();
            while (rs.next()) {
                gfl.add(new KmlGeometryFeature(rs));
            }
            rs.close();
            statement.close();
            pConn.release();
            return gfl;

        } catch (SQLException e) {
            statement.close();
            pConn.release();
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Method cancels running query
     * @throws SQLException
     */
    public void cancel() throws SQLException {
        System.out.println("Cancelling TheQuery");
        statement.cancel();
        pConn.release();
    }
}