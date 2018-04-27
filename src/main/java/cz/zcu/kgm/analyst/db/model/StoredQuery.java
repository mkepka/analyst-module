package cz.zcu.kgm.analyst.db.model;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import cz.zcu.kgm.analyst.main.AppParams;
import cz.zcu.kgm.analyst.util.DateUtil;

/**
 * Class is modelling one stored SQL query in database. 
 * Table combines given SQL query and its result table under ID and state of processing. 
 * @author mkepka
 *
 */
public class StoredQuery {

    private long queryId;
    private String queryName;
    private String SQLQuery;
    private String resTableName;
    private String timeString;
    private Date timeStamp;
    private String status;
    private boolean hasGeom;
    private String geomColName;
    private boolean usedTime;
    private String timeColName;
    private String userId;
    private int featureCount;
    private String BBOX;
    private String nonGeoCols;
    
    private StoredQueryHash hash;
    
    /**
     * Empty constructor for creating List of objects
     */
    public StoredQuery(){
    }
    
    /**
     * Constructor creates instance from given attributes
     * @param queryId - ID of query
     * @param queryName - name of query
     * @param sQLQuery - given SQL query
     * @param resTableName - name of table with analysis result
     * @param timeString - time stamp when query was received 
     * @param status - processing status of query
     * @param hasGeom - indicates if result has geometry column
     * @param geomColName - name of geometry column of result
     * @param usedTime - indicates if result has time column
     * @param timeColName - name of time column of the result
     * @param userId - ID of user that sent query
     * @param featureCount - number of features in the result
     * @param bBOX - BBOX of the geometry of the result
     */
    public StoredQuery(long queryId, String queryName, String sQLQuery,
            String resTableName,
            String timeString, String status, boolean hasGeom,
            String geomColName, boolean usedTime, String timeColName,
            String userId, int featureCount, String bBOX) {
        this.queryId = queryId;
        this.queryName = queryName;
        this.SQLQuery = sQLQuery;
        this.resTableName = resTableName;
        this.timeString = timeString;
        try {
            this.timeStamp = DateUtil.parseTimestamp(timeString);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        this.status = status;
        this.hasGeom = hasGeom;
        this.geomColName = geomColName;
        this.usedTime = usedTime;
        this.timeColName = timeColName;
        this.userId = userId;
        this.featureCount = featureCount;
        this.BBOX = bBOX;
    }

    /**
     * Constructor creates instance of class from parameters
     * @param queryId - identifier of current combination of SQL query and result table  
     * @param SQLQuery - incoming user SQL query 
     * @param resTableName - name of table with results of incoming query
     * @param timeString - time stamp of receiving incoming query or finishing of query as String
     * @param timeStamp - time stamp of incoming query or finishing of query as Date
     * @param status - state of processing of incoming query
     * @param userId - id of user that inserted query as String
     * @param useTime - boolean that determine using of time
     */
    public StoredQuery(long queryId, String SQLQuery, String resTableName, Date timeStamp, String status, String userId, boolean usedTime) {
        this.queryId = queryId;
        this.SQLQuery = SQLQuery;
        this.resTableName = resTableName;
        this.timeString = AppParams.formater.format(timeStamp);
        this.timeStamp = timeStamp;
        this.status = status;
        this.userId = userId;
        this.usedTime = usedTime;
        
        //temporal
        this.timeColName = "time_stamp";
    }
    
    /**
     * Constructor creates class from select before loading result data
     * @param queryId
     * @param queryName
     * @param resTabName
     * @param hasGeom
     * @param geomCol
     * @param usedTime
     * @param timeCol
     * @param userId
     * @param fNumb
     * @param noGeoCols
     */
    public StoredQuery(long queryId, String queryName, String resTabName, boolean hasGeom,
            String geomCol, boolean usedTime, String timeCol, String userId,
            int fNumb, String noGeoCols) {
        this.queryId = queryId;
        this.queryName = queryName;
        this.resTableName = resTabName;
        this.hasGeom = hasGeom;
        this.geomColName = geomCol;
        this.usedTime = usedTime;
        this.timeColName = timeCol;
        this.userId = userId;
        this.featureCount = fNumb;
        this.nonGeoCols = noGeoCols;
    }

    /**
     * Constructor creates instance of class from database ResultSet
     * @param res - ResultSet with result from database 
     * @throws SQLException - Throws SQLException if an exception occurs while getting a value form ResultSet object
     */
    /*
    public StoredQuery(ResultSet res) throws SQLException{
        this.queryId = res.getLong("query_id");
        this.SQLQuery = res.getString("sql_query");
        this.resTableName = res.getString("result_table_name");
        this.timeString = res.getString("time_stamp");
        try {
            this.timeStamp = formater.parse(this.timeString+"00");
        } catch (ParseException e) {
            // should never happen 
          //  e.printStackTrace();
        }
        this.status = res.getString("processing_state");
        this.geomColName = res.getString("geometry_column");
        this.userId = res.getString("user_id");
        this.usedTime = res.getBoolean("used_time");
        
        // temporal
        this.timeColName = "time_stamp";
        
        // available hash
        String query = "SELECT * FROM temp.stored_query_hash WHERE query_id ="+this.queryId+";";
        ResultSet set = SQLExecutor.getInstance().executeQuery(query);
        set.next();
        this.hash = new StoredQueryHash(set);
    }
*/
    /**
     * Getter returns id of current combination of SQL query and result table
     * @return the queryId - id of current combination of SQL query and result table
     */
    public long getQueryId() {
        return queryId;
    }

    /**
     * Getters returns name of query given by user
     * @return the queryName - name of query
     */
    public String getQueryName() {
        return queryName;
    }
    
    /**
     * Getter returns incoming SQL query as String 
     * @return the SQLQuery - incoming SQL query as String
     */
    public String getSQLQuery() {
        return SQLQuery;
    }

    /**
     * Getter returns name of result table for incoming SQL query
     * @return the resTableName - name of result table for incoming SQL query as String
     */
    public String getResTableName() {
        return resTableName;
    }

    /**
     * Getter returns time stamp of receiving incoming query or finishing of query as String 
     * @return the timeString time stamp of receiving incoming query or finishing of query as String
     */
    public String getTimeString() {
        return timeString;
    }

    /**
     * Getter returns time stamp of receiving incoming query or finishing of query as Date
     * @return the timeStamp - time stamp of receiving incoming query or finishing of query as Date
     */
    public Date internalGetTimeStamp() {
        return timeStamp;
    }

    /**
     * Getter returns state of query processing, whether is query processing or finished
     * @return the status - state of query processing, whether is query processing or finished as String
     */
    public String getStatus() {
        return status;
    }

    /**
     * Method updates status of current query after its executing in DBMS 
     * @param status - new status of query
     */
    public void updateStatus(String status){
        this.status = status;
    }
    
    /**
     * Getter returns whether result has geometry field
     * @return true if result has geometry field
     */
    public boolean getHasGeom(){
        return hasGeom;
    }

    /**
     * Method returns name of geometry column in result table. 
     * @return Name of geometry column in result table as String
     * @throws SQLException - Throw SQLException if there is not any column with geometry in result table or has not been set yet.
     */
    public String getGeometryColumnName(){
        return this.geomColName;
    }

    /**
     * Method returns name of column with time value
     * @return name of time column as String
     */
    public String getTimeColumn(){
        return this.timeColName;
    }

    /**
     * Getter returns id of user that inserted query
     * @return the userId - id of user that inserted query as String
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Getter returns whether is time used in query for visualization
     * @return the usedTime
     */
    public boolean isUsedTime() {
        return usedTime;
    }
    
    /**
     * Getter returns number of features in the result
     * @return number of features
     */
    public int getFeatureCount(){
        return featureCount;
    }
    
    /**
     * Getter returns BBOX of the result geometry
     * @return BBOX as String
     */
    public String getBBOX(){
        return this.BBOX;
    }
    
    /**
     * Getter returns corresponding hash table info 
     * @return the hash
     */
    public StoredQueryHash getHash() {
        return hash;
    }

    /**
     * @return the nonGeoCols
     */
    public String getNonGeoCols() {
        return nonGeoCols;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[queryId=" + queryId + ", queryName=" + queryName
                + ", SQLQuery=" + SQLQuery + ", resTableName=" + resTableName
                + ", timeString=" + timeString + ", timeStamp=" + timeStamp
                + ", status=" + status + ", hasGeom=" + hasGeom
                + ", geomColName=" + geomColName + ", usedTime=" + usedTime
                + ", timeColName=" + timeColName + ", userId=" + userId
                + ", featureCount=" + featureCount + ", BBOX=" + BBOX
                + ", nonGeoCols=" + nonGeoCols + ", hash=" + hash + "]";
    }
}