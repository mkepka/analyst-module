package cz.zcu.kgm.analyst.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.zcu.kgm.analyst.db.pool.SQLExecutor;
import cz.zcu.kgm.analyst.db.util.InsertQuery;
import cz.zcu.kgm.analyst.db.util.UpdateQuery;
import cz.zcu.kgm.analyst.rest.beans.QueryBean;
import cz.zcu.kgm.analyst.rest.beans.StoredQueryBean;

public class QueryUtil {

    /**
     * Method inserts received query to DB by given parameters
     * @param queryName - name of the query given by user (optional)
     * @param query - new query to be updated (mandatory)
     * @param userId - ID of user (mandatory)
     * @param usesTime - indicates if the query is using time (optional)
     * @return ID of processed stored query
     */
    public static long insertQuery(String queryName, String query, String userId, boolean usesTime) {
        long queryId = new Date().getTime();
        if(queryName == null || queryName.isEmpty()){
            queryName = "Analysis "+queryId;
        }
        Thread insert = new InsertQuery(queryId, queryName, query, userId, usesTime);
        insert.setName("Inserting:"+queryId);
        insert.start();
        return queryId;
    }

    /**
     * Method gets list of all stored queries of given user 
     * @param userId - ID of user
     * @return ArrayList of StoredQueries
     * @throws SQLException 
     */
    public static List<StoredQueryBean> getQueriesList(String userId) throws SQLException {
        String select = "SELECT query_id, query_name, sql_query, processing_state, f_number,"
                    + " has_geom, used_time,"
                    + " st_astext(bbox) as bbox, st_srid(bbox) as srid"
                    + " FROM "+SQLExecutor.DB_SCHEMA+".stored_query WHERE user_id = '"+userId+"';";
        ResultSet res = SQLExecutor.getInstance().executeQuery(select);
        List<StoredQueryBean> queryList = new ArrayList<StoredQueryBean>();
        if(res != null){
            while(res.next()){
                queryList.add(new StoredQueryBean(
                        res.getLong("query_id"),
                        res.getString("query_name"),
                        res.getString("sql_query"),
                        res.getString("processing_state"),
                        res.getInt("f_number"),
                        res.getBoolean("has_geom"),
                        res.getBoolean("used_time"),
                        res.getString("bbox"),
                        res.getString("srid")));
            }
        }
        return queryList;
    }

    /**
     * Method gets stored query metadata of given user
     * @param queryId - Id of query
     * @param userId - ID of user
     * @return StoredQuery with metadata
     * @throws SQLException 
     */
    public static StoredQueryBean getQuery(long queryId, String userId) throws SQLException {
        String select = "SELECT query_id, query_name, sql_query, processing_state, f_number, has_geom, used_time,"
                    + " st_astext(bbox) as bbox, st_srid(bbox) as srid"
                    + " FROM "+SQLExecutor.DB_SCHEMA+".stored_query"
                    + " WHERE query_id = "+queryId+" AND user_id = '"+userId+"';";
        ResultSet res = SQLExecutor.getInstance().executeQuery(select);
        if(res != null){
            if(res.next()){
                return new StoredQueryBean(
                        res.getLong("query_id"),
                        res.getString("query_name"),
                        res.getString("sql_query"),
                        res.getString("processing_state"),
                        res.getInt("f_number"),
                        res.getBoolean("has_geom"),
                        res.getBoolean("used_time"),
                        res.getString("bbox"),
                        res.getString("srid"));
            }
            else{
                throw new SQLException("Any query with given parameters was not found!");
            }
        }
        else{
            throw new SQLException("An exception occurs during execution of SQL command!");
        }
    }
    
    /**
     * 
     * @param queryId
     * @param userId
     * @return
     * @throws SQLException
     */
    public static QueryBean getQueryStatus(long queryId, String userId) throws SQLException{
        String select = "SELECT query_id, processing_state"
                + " FROM "+SQLExecutor.DB_SCHEMA+".stored_query"
                + " WHERE query_id="+queryId+" AND user_id='"+userId+"';";
        ResultSet res = SQLExecutor.getInstance().executeQuery(select);
        if(res!=null){
            if(res.next()){
                return new QueryBean(res.getLong("query_id"), res.getString("processing_state"));
            }
            else{
                throw new SQLException("Any query with given parameters was not found!");
            }
        }
        else{
            throw new SQLException("An exception occurs during execution of SQL command!");
        }
    }
    /**
     * Method updates stored query by received query to DB
     * @param queryId - ID of stored query
     * @param query - new query to be updated
     * @param userId - ID of user
     * @param usesTime - indicates if the query is using time
     * @return boolean if updating of query was started
     */
    public static boolean updateQuery(long queryId, String queryName, String query, String userId, boolean usesTime) {
        Thread update = new UpdateQuery(queryId, queryName, query, userId, usesTime);
        update.setName("Updating:"+queryId);
        update.start();
        return true;
    }
    
    /**
     * Method deletes stored query from the DB
     * @param queryId - ID of stored query
     * @param userId - ID of user
     * @return boolean if the query was deleted
     * @throws SQLException 
     */
    public static boolean deleteQuery(long queryId, String userId) throws SQLException {
        String delete = "SELECT "+SQLExecutor.DB_SCHEMA+".delete_stored_query("+queryId+", '"+userId+"');";
        ResultSet res = SQLExecutor.getInstance().executeQuery(delete);
        if(res!=null){
            if(res.next()){
                return res.getBoolean(1);
            }
            else{
                return false;
            }
        }
        else{
            throw new SQLException("An exception occurs during execution of SQL command!");
        }
    }
}