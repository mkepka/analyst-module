package cz.zcu.kgm.analyst.rest.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean that returns information about processed query to client 
 * @author mkepka
 *
 */
public class QueryBean {
    @JsonProperty("query_id")
    private long queryId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String query;
    @JsonProperty("processing_status")
    private String queryStatus;
    
    /**
     * Empty constructor
     */
    public QueryBean(){
    }
    
    /**
     * Constructor initialises class from given attributes
     * for returning query status
     * @param queryId
     * @param queryStatus
     */
    public QueryBean(long queryId, String queryStatus) {
        this.queryId = queryId;
        this.queryStatus = queryStatus;
    }
     
    /**
     * Constructor initialises class from given attributes
     * for returning list of queries
     * @param queryId
     * @param query
     * @param queryStatus
     */
    public QueryBean(long queryId, String query, String queryStatus) {
        this.queryId = queryId;
        this.query = query;
        this.queryStatus = queryStatus;
    }

    /**
     * 
     * @return
     */
    public long getQueryId() {
        return queryId;
    }

    /**
     * 
     * @return
     */
    public String getQuery() {
        return query;
    }

    /**
     * 
     * @return
     */
    public String getQueryStatus() {
        return queryStatus;
    }

    @Override
    public String toString() {
        return "[queryId=" + queryId + ", queryStatus=" + queryStatus + "]";
    }
}