/**
 * 
 */
package cz.zcu.kgm.analyst.db.model;

import java.util.List;

/**
 * @author mkepka
 *
 */
public class FeatureCollection {

    private String type;
    private List<Feature> features;
    
    public FeatureCollection(){}
    
    /**
     * @param features
     */
    public FeatureCollection(List<Feature> features) {
        this.type = "FeatureCollection";
        this.features = features;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the features
     */
    public List<Feature> getFeatures() {
        return features;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FeatureCollection [type=" + type + ", features=" + features
                + "]";
    }
}