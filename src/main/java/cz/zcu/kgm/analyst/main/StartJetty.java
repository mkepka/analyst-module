/**
 * 
 */
package cz.zcu.kgm.analyst.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author mkepka
 *
 */
public class StartJetty {

    public static final String HOST = "localhost";
    public static final int PORT = 8282;
    public static final String PATH = "ant";
    public static final String WARPATH ="src/main/webapp";
    
    public static Server server = new Server();
    
    /**
     * Method starts new Jetty server
     */
    public static void start(){
        try {
            // HTTP connector
            ServerConnector http = new ServerConnector(server);
            http.setHost(HOST);
            http.setPort(PORT);
            http.setIdleTimeout(30000);

            // Set connector
            server.addConnector(http);
            
            // WebApp handler on context
            WebAppContext context = new WebAppContext();
            context.setContextPath("/"+PATH);
            context.setWar(WARPATH);

            // Set handler
            server.setHandler(context);

            // Start server
            server.start();
            server.join();

        } catch (Exception e) {
            if(server != null){
                try {
                    server.stop();
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
                
            }
        }
    }
    
    /**
     * Method stops the running server
     * @throws Exception
     */
    public static void stop() throws Exception{
        server.stop();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        start();
    }
}