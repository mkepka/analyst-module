package cz.zcu.kgm.analyst.db.model;

/**
 * Class models an attribute (column) from database
 * It holds name and datatype of column and its current value 
 * @author mkepka
 */
public class Attribute {

    private String attName;
    private String attValue;
    private String attDataType;
    
    /**
     * Constructor that creates an instance with all information about attribute
     * @param attName - Name of column as String
     * @param attValue - Current value of column as String
     * @param attDataType - Name of data tape of column as String
     */
    public Attribute(String attName, String attValue, String attDataType) {
        this.attName = attName;
        this.attValue = attValue;
        this.attDataType = attDataType;
    }
    
    /**
     * Constructor that creates an instance with some information about attribute
     * @param attName - Name of column as String
     * @param attValue - Current value of column as String
     */
    public Attribute(String attName, String attValue) {
        this.attName = attName;
        this.attValue = attValue;
    }

    /**
     * Getter returns name of attribute as String
     * @return the attName - Name of attribute as String
     */
    public String getAttName() {
        return attName;
    }

    /**
     * Getter returns current value of attribute as String 
     * @return the attValue - current value of attribute as String
     */
    public String getAttValue() {
        return attValue;
    }

    /**
     * Getter returns name of data type of attribute as String
     * @return the attDataType - name of data type of attribute as String
     */
    public String getAttDataType() {
        return attDataType;
    }

    /**
     * Method returns String of all properties in order attributeName, attributeValue, attributeDataType
     * @return String of all properties in order attributeName, attributeValue, attributeDataType 
     */
    @Override
    public String toString() {
        return "[" + attName + ", " + attValue+ "]";
    }
}