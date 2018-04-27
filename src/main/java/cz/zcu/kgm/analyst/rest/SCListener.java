package cz.zcu.kgm.analyst.rest;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cz.zcu.kgm.analyst.db.pool.SQLExecutor;

/**
 * 
 * @author mkepka
 *
 */
public class SCListener implements ServletContextListener{
    
    public static final String LOGGER_ID = "app_logger";
    public static Logger logger = Logger.getLogger(LOGGER_ID);
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SQLExecutor.close();
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dbPropFile = sce.getServletContext().getRealPath("WEB-INF/database.properties");
        //String appPropFile = sce.getServletContext().getRealPath("WEB-INF/application.properties");
        Properties dbProp = new Properties();
        //Properties appProp = new Properties();
        
        try {
            dbProp.load(new FileInputStream(dbPropFile));
            //appProp.load(new FileInputStream(appPropFile));
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        } 
        SQLExecutor.setProperties(dbProp);
    }
}