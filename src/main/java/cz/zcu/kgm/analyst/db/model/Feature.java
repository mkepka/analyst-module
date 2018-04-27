/**
 * 
 */
package cz.zcu.kgm.analyst.db.model;

/**
 * @author mkepka
 *
 */
public class Feature {

    private String type;
    private String properties;
    private String geometry;
    
    public Feature(){}
    
    /**
     * @param properties
     * @param geometry
     */
    public Feature(String properties, String geometry) {
        this.type = "Feature";
        this.properties = properties;
        this.geometry = geometry;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the properties
     */
    public String getProperties() {
        return properties;
    }

    /**
     * @return the geometry
     */
    public String getGeometry() {
        return geometry;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Feature [type=" + type + ", properties=" + properties
                + ", geometry=" + geometry + "]";
    }
}