package cz.zcu.kgm.analyst.db.model;

/**
 * Class is modelling one hash level of stored SQL query in database.
 * Table stores names of hash level for given stored query.
 * @author mkepka
 *
 */
public class StoredQueryHash {

    private long query_id;
    private int hash_level;
    private String hash_table_name;
    private String geometry_column;

    /**
     * Empty constructor for creating List of objects
     */
    public StoredQueryHash(){
    }
    
    /**
     * Constructor creates instance of class from parameters
     * @param query_id - identifier of SQL query
     * @param hash_level - level of geohash
     * @param hash_table_name - name of table with one hash level
     * @param geometry_column - name of the column with geometry
     */
    public StoredQueryHash(long query_id, int hash_level, String hash_table_name, String geometry_column) {
        this.query_id = query_id;
        this.hash_level = hash_level;
        this.hash_table_name = hash_table_name;
        this.geometry_column = geometry_column;
    }

    /**
     * Constructor creates instance of class from database ResultSet
     * @param res - ResultSet with result from database 
     * @throws SQLException - Throws SQLException if an exception occurs while getting a value form ResultSet object
     */
    /*
    public StoredQueryHash(ResultSet set) throws SQLException{
        this.query_id = set.getLong("query_id");
        this.hash_level = set.getInt("hash_level");
        this.hash_table_name = set.getString("hash_table_name");
        this.geometry_column = set.getString("geometry_column");
    }
    */
    /**
     * Getter returns ID of query
     * @return the query_id
     */
    public long getQuery_id() {
        return query_id;
    }

    /**
     * Getter returns ID of hash level
     * @return the hash_level
     */
    public int getHash_level() {
        return hash_level;
    }

    /**
     * Getter returns name of hash table
     * @return the hash_table_name
     */
    public String getHash_table_name() {
        return hash_table_name;
    }

    /**
     * Getter returns name of geometry column of the result
     * @return the geometry_column
     */
    public String getGeometry_column() {
        return geometry_column;
    }

    @Override
    public String toString() {
        return "StoredQueryHash [query_id=" + query_id + ", hash_level="
                + hash_level + ", hash_table_name=" + hash_table_name
                + ", geometry_column=" + geometry_column + "]";
    }
}