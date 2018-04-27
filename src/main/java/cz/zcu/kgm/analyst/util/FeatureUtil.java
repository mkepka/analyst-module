package cz.zcu.kgm.analyst.util;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cz.zcu.kgm.analyst.db.model.KmlGeometryFeature;
import cz.zcu.kgm.analyst.db.model.StoredQuery;
import cz.zcu.kgm.analyst.db.pool.GeometryQueryStack;
import cz.zcu.kgm.analyst.db.pool.SQLExecutor;
import de.micromata.opengis.kml.v_2_2_0.Data;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.ExtendedData;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;

/**
 * Utility class for selecting data from DB 
 * @author mkepka
 *
 */
public class FeatureUtil {
    
    public FeatureUtil(){
    }

    /**
     * Method is selecting meta data of stored query
     * @param queryId - ID of query
     * @param userId - Id of user
     * @return StoredQuery object with meta data
     * @throws SQLException
     */
    public static StoredQuery getMetadataForSelecting(long queryId, String userId) throws SQLException{
        String sel = "SELECT sq.query_id, sq.query_name, sq.result_table_name, sq.geometry_column, sq.used_time,"
                + " sq.user_id, sq.time_column, sq.f_number,"
                + " sq.has_geom, array_agg(c.column_name::text) AS columns"
                + " FROM "+SQLExecutor.DB_SCHEMA+".stored_query sq, information_schema.columns c"
                + " WHERE sq.query_id = "+queryId+" AND user_id = '"+userId+"'"
                + " AND c.table_schema = '"+SQLExecutor.DB_SCHEMA+"'"
                + " AND c.table_name = sq.result_table_name AND udt_name <> 'geometry'"
                + " GROUP BY sq.query_id;";
        ResultSet res = SQLExecutor.getInstance().executeQuery(sel);
        if(res != null){
            if(res.next()){
                String nonGeoColArr = res.getString("columns");
                if(nonGeoColArr != null)
                    if(!nonGeoColArr.isEmpty()){
                        nonGeoColArr = nonGeoColArr.replace("{", "");
                        nonGeoColArr = nonGeoColArr.replace("}", "");
                    }
                StoredQuery sq = new StoredQuery(
                        res.getLong("query_id"),
                        res.getString("query_name"),
                        res.getString("result_table_name"),
                        res.getBoolean("has_geom"),
                        res.getString("geometry_column"),
                        res.getBoolean("used_time"),
                        res.getString("time_column"),
                        res.getString("user_id"),
                        res.getInt("f_number"),
                        nonGeoColArr);
                return sq;
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
     * Method selects features by given parameters of StoredQuery as GeoJSON by standard 
     * @param sq - StoredQuery with query meta data
     * @return - FeatureCollection of GeoJSON
     * @throws SQLException
     */
    public static FeatureCollection selectResultDataGeoJsonRef(StoredQuery sq) throws SQLException{
        try{
            StringBuffer select = new StringBuffer();
            select.append("SELECT ");
            if(sq.getNonGeoCols() != null){
                select.append(sq.getNonGeoCols());
                select.append(",");
            }
            select.append("st_asgeojson(st_transform("+sq.getGeometryColumnName()+", 4326)) AS geojson");
            select.append(" FROM ");
            select.append(sq.getResTableName()+";");
            
            ResultSet res = SQLExecutor.getInstance().executeQuery(select.toString());
            ResultSetMetaData rsmd = res.getMetaData();
            int colCount = rsmd.getColumnCount();
            
            FeatureCollection featureColl = new FeatureCollection();
            if(res != null){
                while (res.next()){
                    Feature feat = new Feature();
                    for (int i = 1; i<colCount+1; i++){
                        String colName = rsmd.getColumnLabel(i);
                        if(!colName.equalsIgnoreCase("geojson")){
                            feat.setProperty(colName, res.getObject(i));
                        }
                        else{
                            GeoJsonObject geometry = new ObjectMapper().readValue(res.getString(i), GeoJsonObject.class);
                            feat.setGeometry(geometry);
                        }
                    }
                    featureColl.add(feat);
                }
            }
            return featureColl;
        }
        catch(IOException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects features by given parameters of StoredQuery
     * @param sq - StoredQuery with query meta data
     * @return - FeatureCollection of GeoJSON
     * @throws SQLException
     */
    public static FeatureCollection selectResultDataGeoJson(StoredQuery sq) throws SQLException{
        try{
            StringBuffer select = new StringBuffer();
            select.append("SELECT ");
            if(sq.getNonGeoCols() != null){
                select.append(sq.getNonGeoCols());
                select.append(",");
            }
            select.append("st_asgeojson("+sq.getGeometryColumnName()+", 2, 2) AS geojson");
            select.append(" FROM ");
            select.append(sq.getResTableName()+";");
            
            ResultSet res = SQLExecutor.getInstance().executeQuery(select.toString());
            ResultSetMetaData rsmd = res.getMetaData();
            int colCount = rsmd.getColumnCount();
            
            FeatureCollection featureColl = new FeatureCollection();
            Crs crs = null;
            if(res != null){
                int iter = 0;
                while (res.next()){
                    iter++;
                    Feature feat = new Feature();
                    for (int i = 1; i<colCount+1; i++){
                        String colName = rsmd.getColumnLabel(i);
                        if(!colName.equalsIgnoreCase("geojson")){
                            feat.setProperty(colName, res.getObject(i));
                        }
                        else{
                            GeoJsonObject geometry = new ObjectMapper().readValue(res.getString(i), GeoJsonObject.class);
                            if(iter == 1){
                                crs = geometry.getCrs();
                            }
                            geometry.setCrs(null);
                            feat.setGeometry(geometry);
                        }
                    }
                    featureColl.add(feat);
                }
                featureColl.setCrs(crs);
            }
            return featureColl;
        }
        catch(IOException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * 
     * @param sq
     * @return
     * @throws SQLException
     */
    public static Kml selectResultDataKML(StoredQuery sq) throws SQLException{
        StringBuffer select = new StringBuffer();
        select.append("SELECT ");
        if(sq.getNonGeoCols() != null){
            select.append(sq.getNonGeoCols());
            select.append(",");
        }
        select.append("st_asKML("+sq.getGeometryColumnName()+") AS kml");
        select.append(" FROM ");
        select.append(sq.getResTableName()+";");
        
        ResultSet res = SQLExecutor.getInstance().executeQuery(select.toString());
        if(res!= null){
            ResultSetMetaData rsmd = res.getMetaData();
            int colCount = rsmd.getColumnCount();
            Kml kml = KmlFactory.createKml();
            Document doc = KmlFactory.createDocument();
            //List<Placemark> placemarkList = new ArrayList<Placemark>();
            while (res.next()){
                Placemark placemark = KmlFactory.createPlacemark();
                ExtendedData extData = KmlFactory.createExtendedData();
                for (int i = 1; i < colCount+1; i++){
                    String colName = rsmd.getColumnLabel(i);
                    if(!colName.equalsIgnoreCase("kml")){
                        Data col = KmlFactory.createData(colName);
                        col.setValue(res.getString(i));
                        extData.addToData(col);
                    }
                    else{
                        String geomVal = res.getString(colName);
                        geomVal = geomVal.replaceAll("<Point>", "");
                        geomVal = geomVal.replaceAll("</Point>", "");
                        //JAXBContext jc = JAXBContext.newInstance(Point.class);
                        //Unmarshaller u = jc.createUnmarshaller();
                        //StringReader read = new StringReader(res.getString("kml"));
                        //JAXBElement<Point> point = (JAXBElement<Point>) u.unmarshal(read);
                        JAXBElement<Point> pointElem = new JAXBElement<Point>(new QName(Point.class.getSimpleName()), Point.class, new Point());
                        pointElem.setValue(new Point().addToCoordinates(geomVal));
                        placemark.setGeometry(pointElem.getValue());
                    }
                }
                placemark.setExtendedData(extData);
                doc.addToFeature(placemark);
            }
            kml.setFeature(doc);
            return kml;
        }
        else {
            throw new SQLException("An exception occurs during execution of SQL command!");
        }
    }
    
    /**
     * Method creates new SQL from result table name and current BBOX and sends
     * query to executing to DBMS and creates list of objects from ResultSet
     * If number of points in the result exceeds given threshold, 
     * clusters are selected instead
     * 
     * @param storedQuery - StoredQuery object with query which result should be returned
     * @param bbox - BBOX of current map window in which will be data loaded
     * @return List of objects from ResultSet or empty list if ResultSet returns no results
     * @throws SQLException - Throws exception if there occurs any problems during executing SQL command in DBMS 
     * or if number of features exceeds defined threshold
     */
    public static String getGeomFeaturesFromResultTable(StoredQuery sq, String userId) throws SQLException {
        StringBuffer select = new StringBuffer();
        select.append("SELECT ");
        if(sq.getNonGeoCols() != null){
            select.append(sq.getNonGeoCols());
            select.append(",");
        }
        select.append("st_asKML("+sq.getGeometryColumnName()+") AS kml");
        select.append(" FROM ");
        select.append(sq.getResTableName()+";");
        
        List<KmlGeometryFeature> featList = GeometryQueryStack.getKmlGF(userId, select.toString());
        return prepareStaticKML(featList);
        
        /*
        int numOfFeatures = getPointCount(storedQuery, bbox);
        if(numOfFeatures > 30000){
            StoredQueryHash hash = storedQuery.getHash();
            String query = "SELECT st_asKML(" + hash.getGeometry_column()
                    + "), * FROM temp." + hash.getHash_table_name()
                    + " WHERE " + hash.getGeometry_column() + " "
                    + "&& st_transform(st_polygonFromText('" + bbox.getBboxPolygon()
                    + "', 4326), st_srid(" + hash.getGeometry_column() + "));";
            List<GeometryFeature> featList = GeometryQueryStack.getGF(sessionId, query);
            return featList;
        }
        else{
            String query = "SELECT st_asKML(" + storedQuery.getGeometryColumnName()
                    + "), * FROM temp." + storedQuery.getResTableName() + " "
                    + "WHERE " + storedQuery.getGeometryColumnName() + " "
                    + "&& st_transform(st_polygonFromText('" + bbox.getBboxPolygon()
                    + "', 4326), st_srid(" + storedQuery.getGeometryColumnName()
                    + "));";
            List<GeometryFeature> featList = GeometryQueryStack.getGF(sessionId, query);
            return featList;
        }
        */
    }
    
    /**
     * 
     * @param sq
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public static String selectNonGeoResultDataCSV(StoredQuery sq) throws SQLException{
        try{
            String select = "SELECT "+sq.getNonGeoCols()+" FROM "+sq.getResTableName()+";";
            ResultSet res = SQLExecutor.getInstance().executeQuery(select);
            if(res != null){
                //ResultSetMetaData rsmd = res.getMetaData();
                //int colCount = rsmd.getColumnCount();
                StringWriter sw = new StringWriter();
                CSVPrinter csvPrint = new CSVPrinter(sw, CSVFormat.POSTGRESQL_CSV.withHeader(res).withQuoteMode(QuoteMode.NON_NUMERIC));
                csvPrint.printRecords(res);
                csvPrint.flush();
                csvPrint.close();
                return sw.toString();
            }
            else{
                throw new SQLException("An Exception occurs during execution of SQL query!");
            }
        } catch(IOException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects data from result table that doesn't contain geometry column
     * @param sq - StoredQuery object with meta data
     * @return ArrayNode with rows as JSON objects
     * @throws SQLException
     */
    public static ArrayNode selectNonGeoResultData(StoredQuery sq) throws SQLException{
        try{
            String select = "SELECT "+sq.getNonGeoCols()+" FROM "+sq.getResTableName()+";";
            ResultSet res = SQLExecutor.getInstance().executeQuery(select);
            if(res != null){
                FeatureUtil fUtil = new FeatureUtil();
                return fUtil.parseResSet(res);
            }
            else{
                throw new SQLException("An Exception occurs during execution of SQL query!");
            }
        }catch (SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method prepares KML file with given Geometry Features
     * @param gfList - list of KmlGeometryFeatures objects
     * @return KML file as String
     */
    private static String prepareStaticKML(List<KmlGeometryFeature> gfList){
        StringWriter sw = new StringWriter();
        //Sending part of KML response to client
        sw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n<Document>\n");
        for(int i = 0; i<gfList.size();i++){
            KmlGeometryFeature gf = gfList.get(i);
            sw.write("<Placemark id=\""+i+"\">\n");
            sw.write("<name>Geometry Feature</name>\n");
            sw.write("<ExtendedData>"+gf.getKMLExtendedData()+"</ExtendedData>\n");
            sw.write(gf.getGeomKML()+"\n");
            sw.write("</Placemark>\n");
        }
        sw.write("</Document>\n</kml>");
        return sw.toString();
    }
    
    /**
     * Method for reading ResultSet
     * @param res
     * @return
     * @throws SQLException
     */
    private ArrayNode parseResSet(ResultSet res) throws SQLException{
        ObjectMapper objMapper = new ObjectMapper();
        ArrayNode rootArr = objMapper.createArrayNode();
        ResultSetMetaData rsmd = res.getMetaData();
        int colCount = rsmd.getColumnCount();
        while (res.next()){
            ObjectNode rowNode = objMapper.createObjectNode();
            for (int j = 1; j < colCount+1; j++) {
                int colType = rsmd.getColumnType(j);
                String colName = rsmd.getColumnName(j);
                switch (colType) {
                case Types.INTEGER:
                    int i = res.getInt(j);
                    if (res.wasNull()) {
                        rowNode.putNull(colName);
                    } else {
                        rowNode.put(colName, i);
                    }
                    break;

                case Types.BIGINT:
                    long l = res.getLong(j);
                    if (res.wasNull()) {
                        rowNode.putNull(colName);
                    } else {
                        rowNode.put(colName, l);
                    }
                    break;

                case Types.DECIMAL:
                case Types.NUMERIC:
                    rowNode.put(colName, res.getBigDecimal(j));
                    break;

                case Types.FLOAT:
                case Types.REAL:
                case Types.DOUBLE:
                    double d = res.getDouble(j);
                    if (res.wasNull()) {
                        rowNode.putNull(colName);
                    } else {
                        rowNode.put(colName, d);
                    }
                    break;

                case Types.NVARCHAR:
                case Types.VARCHAR:
                case Types.LONGNVARCHAR:
                case Types.LONGVARCHAR:
                case Types.DATE:
                case Types.TIMESTAMP:
                    String s = res.getString(j);
                    if(res.wasNull()){
                        rowNode.putNull(colName);
                    } else{
                        rowNode.put(colName, s);
                    }
                    break;

                case Types.BOOLEAN:
                case Types.BIT:
                    boolean b = res.getBoolean(j);
                    if (res.wasNull()) {
                        rowNode.putNull(colName);
                    } else {
                        rowNode.put(colName, b);
                    }
                    break;

                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    byte[] by = res.getBytes(j);
                    if (res.wasNull()) {
                        rowNode.putNull(colName);
                    } else {
                        rowNode.put(colName, by);
                    }
                    break;

                case Types.TINYINT:
                case Types.SMALLINT:
                    short sh = res.getShort(j);
                    if (res.wasNull()) {
                        rowNode.putNull(colName);
                    } else {
                        rowNode.put(colName, sh);
                    }
                    break;
                    
                case Types.BLOB:
                    throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type BLOB");

                case Types.CLOB:
                    throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type CLOB");

                case Types.ARRAY:
                    throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type ARRAY");

                case Types.STRUCT:
                    throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type STRUCT");

                case Types.DISTINCT:
                    throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type DISTINCT");

                case Types.REF:
                    throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type REF");

                case Types.JAVA_OBJECT:
                    
                default:
                    String defObj = res.getObject(j).toString();
                    rowNode.put(colName, defObj);
                    break;
                }
            }
            rootArr.add(rowNode);
        }
        return rootArr;
    }
}