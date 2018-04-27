/**
 * 
 */
package cz.zcu.kgm.analyst.db.pool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for executing SQL commands. This class is responsible for proper
 * Connections handling. This class is singleton.
 * @author jezekjan
 *
 */
public class SQLExecutor {
    
    private static String configfile = null;
    private static ConnectionPool mycp;
    private static Properties prop;
    private static SQLExecutor SQLEXEC;
    public static final String LOGGER_ID = "database_logger";
    public static Logger logger = Logger.getLogger(LOGGER_ID);
    
    private static final String ADDRESS_PROP = "Address";
    private static final String USERNAME_PROP = "Username";
    private static final String PASSWORD_PROP = "Password";
    private static final String MAX_POOL_SIZE_PROP = "MaxPoolSize";
    private static final String MIN_POOL_SIZE_PROP = "MinPoolSize";
    
    public static final String DB_SCHEMA_PROP = "DbSchema";
    public static String DB_SCHEMA;

    /**
     * Private constructor for preventing further instantiation 
     */
    private SQLExecutor() {
        if (prop == null) {
            throw new NullPointerException("You have to call setProperties() before getting Connection!");
        }
    }
    
    /**
     * Method returns instance of the SQLExecutor class
     * if the instance doesn't exist, it creates new instance 
     * @return instance of SQLExecutor class
     */
    public static synchronized SQLExecutor getInstance() {
        if (SQLEXEC == null) {
            SQLEXEC = new SQLExecutor();
        }
        return SQLEXEC;
    }
    
    /**
     * Method sets properties from Properties object 
     * @param proper instance of Properties class with settings
     */
    public static void setProperties(Properties proper) {
        prop = proper;
        mycp = new ConnectionPool(String.valueOf(prop.get(ADDRESS_PROP)),
                String.valueOf(prop.get(USERNAME_PROP)), 
                String.valueOf(prop.get(PASSWORD_PROP)));
        
        mycp.setMaxPoolSize(Integer.valueOf(prop.getProperty(MAX_POOL_SIZE_PROP)));
        mycp.setMinPoolSize(Integer.valueOf(prop.getProperty(MIN_POOL_SIZE_PROP)));
        DB_SCHEMA = prop.getProperty(DB_SCHEMA_PROP);
    }
    
    /**
     * Method executes SELECT command
     * 
     * @param sql Select command as String
     * @return ResultSet object that contains the data produced by the given query; never null 
     * @throws SQLException if an exception occurs during execution of SQL command 
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        PooledConnection con = mycp.getPooledConnection();
        ResultSet rs = null;
        Statement st= null;
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            con.release();
        } catch (SQLException e) {
            mycp.removeConnection(con);
            throw new SQLException(e);
        }
        return rs;
    }
    
    /**
     * Method executes SQL Data Manipulation Language (DML) statements
     * @param sql INSERT, UPDATE or DELETE command as String
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements 
     * or (2) 0 for SQL statements that return nothing
     * @throws SQLException if an exception occurs during execution of SQL command
     */
    public static synchronized int executeUpdate(String sql) throws SQLException {
        PooledConnection con = mycp.getPooledConnection();
        int rs;
        Statement st= null;
        try {
            st = con.createStatement();
            rs = st.executeUpdate(sql);
            con.release();
        } catch (SQLException e) {
            mycp.removeConnection(con);
            throw new SQLException(e);
        }
        return rs;
    }
    
    /**
     * Method closes connections in ConnectionPool
     */
    public static void close() {
        mycp.closeConnections();
    }

    /**
     * Method returns number of connections in ConnectionPool
     * @return number of connections
     */
    public static int getNumberOfConnection() {
        return mycp.getNumConnections();
    }

    /**
     * Method returns number of used connections in ConnectionPool
     * @return number of used connections
     */
    public static int getNumberOfUsedConnection() {
        try {
            return mycp.getNumUsedConnections();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Method returns configuration file
     * @return config file as String
     */
    public static String getConfigfile() {
        return configfile;
    }

    /**
     * Method sets configuration file as String
     * @param configfile as String
     */
    public static void setConfigfile(String configfile) {
        SQLExecutor.configfile = configfile;
    }

    /**
     * Method returns ConnectionPool with Connections to database
     * @return
     */
    protected static ConnectionPool getCoonectionPool() {
        return mycp;
    }
    
    /**
     * Method returns PooledConnection with connection to the DB
     * @return PooledConnection
     * @throws SQLException
     */
    public static PooledConnection getConnection() throws SQLException{
        return mycp.getPooledConnection();
    }
}