package cz.zcu.kgm.analyst.rest.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean class is modelling stored query metadata
 * @author mkepka
 *
 */
public class StoredQueryBean {

    @JsonProperty("query_id")
    private long queryId;
    @JsonProperty("query_name")
    private String queryName;
    private String query;
    @JsonProperty("processing_status")
    private String status;
    @JsonProperty("feature_count")
    private int featureCount;
    @JsonProperty("has_geometry")
    private boolean hasGeom;
    @JsonProperty("uses_time")
    private boolean usedTime;
    @JsonProperty("bbox")
    private String BBOX;
    @JsonProperty("bbox_srid")
    private String SRID;
    
    /**
     * Empty constructor
     */
    public StoredQueryBean(){
    }
    
    /**
     * @param queryId
     * @param sQLQuery
     * @param status
     * @param featureCount
     * @param hasGeom
     * @param usedTime
     * @param bBOX
     * @param sRID
     */
    public StoredQueryBean(long queryId, String queryName, String sQLQuery,
            String status, int featureCount, boolean hasGeom,
            boolean usedTime, String bBOX, String sRID) {
        this.queryId = queryId;
        this.queryName = queryName;
        this.query = sQLQuery;
        this.status = status;
        this.featureCount = featureCount;
        this.hasGeom = hasGeom;
        this.usedTime = usedTime;
        this.BBOX = bBOX;
        this.SRID = sRID;
    }

    /**
     * @return the queryId
     */
    public long getQueryId() {
        return queryId;
    }

    /**
     * @return the name of query
     */
    public String getQueryName() {
        return queryName;
    }
    
    /**
     * @return the sQLQuery
     */
    public String getQuery() {
        return query;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the featureCount
     */
    public int getFeatureCount() {
        return featureCount;
    }

    /**
     * @return the hasGeom
     */
    public boolean isHasGeom() {
        return hasGeom;
    }

    /**
     * @return the usedTime
     */
    public boolean isUsedTime() {
        return usedTime;
    }

    /**
     * @return the bBOX
     */
    public String getBBOX() {
        return BBOX;
    }

    /**
     * @return the sRID
     */
    public String getSRID() {
        return SRID;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[queryId=" + queryId + ", queryName="
                + queryName + ", query=" + query + ", status=" + status
                + ", featureCount=" + featureCount + ", hasGeom=" + hasGeom
                + ", usedTime=" + usedTime + ", BBOX=" + BBOX + ", SRID="
                + SRID + "]";
    }
}