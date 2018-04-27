/**
 * 
 */
package cz.zcu.kgm.analyst.rest;

import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cz.zcu.kgm.analyst.rest.beans.TestBean;
import cz.zcu.kgm.analyst.util.DateUtil;
import cz.zcu.kgm.analyst.util.TestUtil;

/**
 * @author mkepka
 *
 */
@Path("/test/")
public class TestRest {

    /**
     * Empty constructor
     */
    public TestRest(){
        super();
    }
    
    /**
     * Method catches rest/test/testget requests to test the connection to receiver
     * /rest/test/testget?value=<>
     * @param testValue value of parameter test as String
     * @return response of the servlet as String
     * @throws Exception 
     */
    @Path("/testget")
    @GET
    @Produces("text/plain")
    public String testGet(@QueryParam("value") String testValue) throws Exception{
        if(testValue == null){
            return "empty";
        }
        else{
            //String modif = testValue.toUpperCase();
            Date parseMicro = DateUtil.parseTimestampMicro(testValue);
            Date parse = DateUtil.parseTimestamp(testValue);
            return ""+parseMicro+" : "+parseMicro.getTime()+"\n"+parse+" : "+parse.getTime();
        }
    }
    
    /**
     * /rest/test/testbean1?message=<>
     * @param testValue
     * @param userValue
     * @return
     */
    @Path("/testbean1")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public TestBean testXML(@QueryParam("message") String testValue, @QueryParam("user") String userValue){
        if(testValue == null){
            return null;
        }
        else{
            String modif = testValue.toUpperCase();
            return new TestBean(modif);
        }
    }
    
    /**
     * /rest/test/testbean2?message=<>
     * @param testValue
     * @return
     */
    @Path("/testbean2")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestBean testJSON(@QueryParam("message") String testValue){
        if(testValue == null){
            return null;
        }
        else{
            String modif = testValue.toUpperCase();
            return new TestBean(modif);
        }
    }
    
    /**
     * http://localhost:8282/ant/rest/test/testdb?message=<>
     * @param testValue
     * @return
     */
    @Path("/testdb")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestBean testDB(@QueryParam("message") String testValue){
        if(testValue == null){
            return null;
        }
        else{
            try {
                String response = TestUtil.testQuery("SELECT nazev FROM ruian.staty LIMIT 1;");
                return new TestBean(response);
            }
            catch (SQLException e) {
                return new TestBean("SELECT was not succesfull!");
            }
        }
    }
}