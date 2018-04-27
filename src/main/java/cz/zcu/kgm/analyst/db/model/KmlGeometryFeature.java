package cz.zcu.kgm.analyst.db.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class is modelling result table of incoming SQL query
 * From this class is than generated output KML format
 * @author mkepka
 */
public class KmlGeometryFeature {

    private List<Attribute> attributes;
    private String geomKML;
    private String timeKML;
    private String color;
    
    private final String geomColType = "geometry";
    private String[] kmlNames = {"st_askml", "askml"};
    private String timeColName = "time_stamp";
    
    /**
     * Constructor creates instance of this class
     */
    public KmlGeometryFeature(){
    }

    /**
     * Constructor creates instance of this class from ResultSet object
     * @param set - ResultSet object with one row from database result table  
     * @throws SQLException - Throws SQLException if an exception occurs while getting values from ResultSet
     */
    public KmlGeometryFeature (ResultSet set) throws SQLException{
        attributes = new LinkedList<Attribute>();
        Attribute att;
        String colName;
        String colVal;
        String colType;
        ResultSetMetaData rsmd = set.getMetaData();
        int colCount = rsmd.getColumnCount();
        for (int col = 1; col <= colCount; col++){
            colName = rsmd.getColumnName(col);
            colType = rsmd.getColumnTypeName(col);
            if(colName.equalsIgnoreCase("kml")){
                this.geomKML = set.getString(col);
            }
            else{
                if(!colType.equalsIgnoreCase(geomColType)){
                    colVal = set.getString(col);
                    att = new Attribute(colName, colVal);
                    attributes.add(att);
                }
                else if (colName.equalsIgnoreCase("color")){
                    color = set.getString(col);
                }
            }
        }
    }

    /**
     * Constructor creates instance of this class from ResultSet object
     * @param set - ResultSet object with one row from database result table
     * @param usedTime - boolean determine if time will be used in visualisation
     * @throws SQLException - Throws SQLException if an exception occurs while getting values from ResultSet
     */
    public KmlGeometryFeature (ResultSet set, boolean usedTime) throws SQLException{
        attributes = new LinkedList<Attribute>();
        Attribute att;
        String colName;
        String colVal;
        String colType;
        ResultSetMetaData rsmd = set.getMetaData();
        int colCount = rsmd.getColumnCount();
        for (int col = 1; col<=colCount; col++){
            colName = rsmd.getColumnName(col);
            colType = rsmd.getColumnTypeName(col);
            if(colName.equalsIgnoreCase(kmlNames[0]) || colName.equalsIgnoreCase(kmlNames[1])){
                this.geomKML=set.getString(col);
            }
            else if(colName.equals(timeColName) && usedTime==true){
                String timeStamp = set.getString(col);
                timeStamp = timeStamp.replace(" ", "T")+":00";
                this.timeKML = "<TimeStamp><when>"+timeStamp+"</when></TimeStamp>";
            }
            else{
                if(!colType.equalsIgnoreCase(geomColType)){
                    colVal = set.getString(col);
                    att = new Attribute(colName, colVal);
                    attributes.add(att);
                }
            }
        }
        if(usedTime==false){
            this.timeKML = "<TimeStamp></TimeStamp>";
        }
    }
    
    /**
     * Getter returns LinkedList of Attribute classes that was in result table without columns asKML(...) or the_geom
     * @return the attributes - LinkedList of Attribute classes that was in result table
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Getter returns name of column that contains geometry in KML format
     * @return the geomKML - name of column that contains geometry in KML format
     */
    public String getGeomKML() {
        return geomKML;
    }

    /**
     * Getter return Timestamp element from KML
     * @return the timeKML
     */
    public String getTimeKML() {
        if (timeKML==null) return "";
        return timeKML;
    }
    
    public String getColor(){
        return color;
    }

    /**
     * Getter returns String with attributes names and values in form of KML ExtendedData element
     * @return - String with attributes names and values in form of KML ExtendedData element
     */
    public String getKMLExtendedData(){
        String exData = "";
        for (int i = 0; i<attributes.size(); i++){
            String exDataRow = "<Data name=\""+attributes.get(i).getAttName()+"\"><value>"+attributes.get(i).getAttValue()+"</value></Data>\n";
            exData = exData+exDataRow;
        }
        return exData;
    }

    /**
     * Method returns String of geometry column name and all column names
     */
    public String toString() {
        return "[" + geomKML + ", " + attributes.toString() + "]";
    }
}