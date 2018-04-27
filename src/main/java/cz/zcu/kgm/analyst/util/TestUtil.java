package cz.zcu.kgm.analyst.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.zcu.kgm.analyst.db.pool.SQLExecutor;

public class TestUtil {

    /**
     * Test utilisation method to select given query
     * @param query
     * @return
     * @throws SQLException
     */
    public static String testQuery(String query) throws SQLException{
        ResultSet res = SQLExecutor.getInstance().executeQuery(query); 
        if(res.next()){
            String resp = res.getString(1);
            return resp;
        }
        else{
            throw new SQLException("There is not any query!");
        } 
        
    }
}
