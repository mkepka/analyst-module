package cz.zcu.kgm.analyst.db.util;

import java.sql.SQLException;

import cz.zcu.kgm.analyst.db.pool.SQLExecutor;

/**
 * Class represents new thread for inserting of query to the DB
 * @author mkepka
 *
 */
public class InsertQuery extends Thread {
    
    private long queryId;
    private String queryName;
    private String query;
    private String userId;
    private boolean usesTime;
    
    /**
     * Constructor of the thread class
     * @param queryId - ID of stored query
     * @param query - new query to be updated
     * @param userId - ID of user
     * @param usesTime - indicates if the query is using time
     */
    public InsertQuery(long queryId, String query, String userId, boolean usesTime){
        this.queryName = "Analysis";
        this.queryId = queryId;
        this.query = query;
        this.userId = userId;
        this.usesTime = usesTime;
    }
    
    /**
     * Constructor of the thread class
     * @param queryId - ID of stored query
     * @param queryName - name of query given by user
     * @param query - new query to be updated
     * @param userId - ID of user
     * @param usesTime - indicates if the query is using time
     */
    public InsertQuery(long queryId, String queryName, String query, String userId, boolean usesTime){
        this.queryId = queryId;
        this.queryName = queryName;
        this.query = query;
        this.userId = userId;
        this.usesTime = usesTime;
    }
    
    /**
     * Run method of the thread
     */
    public void run(){
        //System.out.println("thread is running...");
        String ins = "SELECT "+SQLExecutor.DB_SCHEMA+".add_query("+queryId+",'"+queryName+"',$$"+query+"$$,'"+userId+"',"+usesTime+")";
        try {
            SQLExecutor.getInstance().executeQuery(ins);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}