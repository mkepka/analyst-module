package cz.zcu.kgm.analyst.db.model;

/**
 * Class models Bounding Box for current map window extent in client application
 * @author mkepka
 *
 */
public class BBOX2D {

    private String bboxPolygon;
    private double xmin;
    private double xmax;
    private double ymin;
    private double ymax;
    
    /**
     * Constructor creates instance of BBOX class from BBOX String that is returned by client application
     * Constructor supposed that coordinates limits are separated by comma
     * @param bboxString - String with coordinates limits separated by comma
     */
    public BBOX2D(String bboxString){
        String[] corners = bboxString.split(",");
        this.xmin = Double.parseDouble(corners[0]);
        this.ymin = Double.parseDouble(corners[1]);
        this.xmax = Double.parseDouble(corners[2]);
        this.ymax = Double.parseDouble(corners[3]);
        this.bboxPolygon = "POLYGON(("+xmin+" "+ymax+", "+xmax+" "+ymax+", "+xmax+" "+ymin+", "+xmin+" "+ymin+", "+xmin+" "+ymax+"))";
    }

    /**
     * Constructor creates instance of BBOX class from values of coordinates limits
     * @param xmin - minimal X coordinate
     * @param xmax - maximal X coordinate
     * @param ymin - minimal Y coordinate
     * @param ymax - maximal Y coordinate
     */
    public BBOX2D(double xmin, double xmax, double ymin, double ymax){
        this.xmin=xmin;
        this.xmax=xmax;
        this.ymin=ymin;
        this.ymax=ymax;
        this.bboxPolygon = "POLYGON(("+xmin+" "+ymax+", "+xmax+" "+ymax+", "+xmax+" "+ymin+", "+xmin+" "+ymin+", "+xmin+" "+ymax+"))";
    }

    /**
     * Getter returns BBOX polygon as WKT geometry:
     * POLYGON((xmin ymax, xmax ymax, xmax ymin, xmin ymin, xmin ymax))
     * @return the bboxPolygon - BBOX polygon as WKT geometry
     */
    public String getBboxPolygon() {
        return bboxPolygon;
    }

    /**
     * Getter returns value of minimal X coordinate
     * @return the xmin - value of minimal X coordinate as double
     */
    public double getXmin() {
        return xmin;
    }

    /**
     * Getter returns value of maximal X coordinate
     * @return the xmax - value of maximal X coordinate as double
     */
    public double getXmax() {
        return xmax;
    }

    /**
     * Getter returns value of minimal Y coordinate
     * @return the ymin - value of minimal Y coordinate as double
     */
    public double getYmin() {
        return ymin;
    }

    /**
     * Getter returns value of maximal Y coordinate
     * @return the ymax - value of maximal Y coordinate as double
     */
    public double getYmax() {
        return ymax;
    }

    /**
     * Method returns BBOX polygon as WKT geometry:
     * POLYGON((xmin ymax, xmax ymax, xmax ymin, xmin ymin, xmin ymax))
     * @return - BBOX polygon as WKT geometry as String
     */
    public String toString(){
        return bboxPolygon;
    }
}
