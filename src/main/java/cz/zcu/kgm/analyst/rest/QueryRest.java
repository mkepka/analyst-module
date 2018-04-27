package cz.zcu.kgm.analyst.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.zcu.kgm.analyst.rest.beans.ExceptionBean;
import cz.zcu.kgm.analyst.rest.beans.QueryBean;
import cz.zcu.kgm.analyst.rest.beans.StoredQueryBean;
import cz.zcu.kgm.analyst.util.QueryUtil;

/**
 * Resource class that process requests on queries
 * @author mkepka
 *
 */
@Path("/query")
public class QueryRest {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * /rest/query/insert?query=&user_id=&uses_time
     * Method retrieves request for inserting SQL query and returns query identifier to client
     * @param query_name - name of the query given by user (optional)
     * @param query - SQL query as String
     * @param user_id - User identifier as String
     * @return ID of inserted query in JSON format  
     */
    @Path("/insert")
    @GET
    public Response insertQueryGet(@QueryParam("query") String query,
            @QueryParam("query_name") String queryName,
            @QueryParam("user_id") String userId,
            @DefaultValue("false") @QueryParam("uses_time") boolean usesTime) {
        if(query == null){
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ExceptionBean("Exception", "No query was given!"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } else if(query.isEmpty()){
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ExceptionBean("Exception", "Empty query parameter was given!"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        else{
            long queryId = QueryUtil.insertQuery(queryName, query, userId, usesTime);
            return Response.status(Status.ACCEPTED)
                    .entity(new QueryBean(queryId, "processing"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * /rest/query?user_id=&uses_time
     * Method retrieves request for inserting SQL query and returns query identifier to client
     * @param query - SQL query as JSON String
     * @param user_id - User identifier as String
     * @return ID of inserted query in JSON format with processing state
     */
    @POST
    public Response insertQueryPost(String query, @QueryParam("user_id") String userId, @DefaultValue("false") @QueryParam("uses_time") boolean usesTime) {
        try {
            if(query == null){
                return Response.status(Status.BAD_REQUEST)
                        .entity(new ExceptionBean("Exception", "No query was given!"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                String queryName = null;
                String sqlQuery = null;
                JsonNode queryJson = mapper.readTree(query);
                if(queryJson.has("query_name")){
                    queryName = queryJson.get("query_name").textValue();
                }
                if(queryJson.has("query")){
                    sqlQuery = queryJson.get("query").textValue();
                }
                if(sqlQuery.isEmpty()){
                    return Response.status(Status.BAD_REQUEST)
                            .entity(new ExceptionBean("Exception", "Empty query parameter was given!"))
                            .type(MediaType.APPLICATION_JSON)
                            .build();
                }
                else{
                    long queryId = QueryUtil.insertQuery(queryName, sqlQuery, userId, usesTime);
                    //Long queryId = new Date().getTime();
                    return Response.status(Status.ACCEPTED)
                            .entity(new QueryBean(queryId, "processing"))
                            .type(MediaType.APPLICATION_JSON)
                            .build();
                }
            }
        }
        catch (IOException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ExceptionBean("Exception", "Received query object was not correct!"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * /rest/query?user_id=
     * Method returns list of available queries of given user
     * @param userId - ID of user
     * @return List of available queries stored in DB
     */
    @GET
    public Response getUserQueries(@QueryParam("user_id") String userId){
        try{
            List<StoredQueryBean> queriesList = QueryUtil.getQueriesList(userId);
            return Response.status(Status.OK)
                    .entity(queriesList)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch(SQLException e){
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("SQLException", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * /rest/query/{query_id}/status?user_id=
     * Method returns status of given query
     * @param queryId - ID of query
     * @param userId - ID of user
     * @return QueryBean with query status
     */
    @Path("{query_id}/status")
    @GET
    public Response getQueryStatus(@PathParam("query_id") long queryId, @QueryParam("user_id") String userId){
        try {
            QueryBean query = QueryUtil.getQueryStatus(queryId, userId);
            //QueryBean query = new QueryBean(queryId, "finished");
            return Response.status(Status.OK)
                    .entity(query)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("Exception", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * Update StoredQuery with new query
     * @param query - new query to be inserted
     * @param queryId - ID of query
     * @param userId - ID of user
     * @return status of updating
     */
    @Path("{query_id}/update")
    @PUT
    public Response updateQueryParams(@QueryParam("query_name") String queryName, @QueryParam("sql_query") String query,
            @PathParam("query_id") long queryId, @QueryParam("user_id") String userId,
            @DefaultValue("false") @QueryParam("uses_time") boolean usesTime){
        boolean updated = QueryUtil.updateQuery(queryId, queryName, query, userId, usesTime);
        if(updated){
            return Response.status(Status.OK)
                    .build();
        }
        else{
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("SQLException", "Query was not updated!"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * Update StoredQuery with new query
     * @param query - new query to be inserted
     * @param queryId - ID of query
     * @param userId - ID of user
     * @return status of updating
     */
    @Path("{query_id}")
    @PUT
    public Response updateQuery(String queryJson, @PathParam("query_id") long queryId, @QueryParam("user_id") String userId,
            @DefaultValue("false") @QueryParam("uses_time") boolean usesTime){
        try {
            String queryName = null;
            String sqlQuery = null;
            JsonNode queryNode = mapper.readTree(queryJson);
            
            if(queryNode.has("query_name")){
                queryName = queryNode.get("query_name").textValue();
            }
            if(queryNode.has("query")){
                sqlQuery = queryNode.get("query").textValue();
            }
            if(sqlQuery.isEmpty()){
                return Response.status(Status.BAD_REQUEST)
                        .entity(new ExceptionBean("Exception", "Empty query parameter was given!"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                boolean updated = QueryUtil.updateQuery(queryId, queryName, sqlQuery, userId, usesTime);
                //boolean updated = true;
                if(updated){
                    return Response.status(Status.OK)
                            .build();
                }
                else{
                    return Response.status(Status.INTERNAL_SERVER_ERROR)
                            .entity(new ExceptionBean("SQLException", "Query was not updated!"))
                            .type(MediaType.APPLICATION_JSON)
                            .build();
                }
            }
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("SQLException", "Query was not updated!"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * Method deletes StoredQuery from database
     * @param queryId - ID of query
     * @param userId - ID of user
     * @return Status of deleting
     */
    @Path("{query_id}")
    @DELETE
    public Response deleteQuery(@PathParam("query_id") long queryId, @QueryParam("user_id") String userId){
        try{
            boolean deleted = QueryUtil.deleteQuery(queryId, userId);
            //boolean deleted = true;
            if(deleted){
                return Response.status(Status.OK)
                        .build();
            }
            else{
                return Response.status(Status.INTERNAL_SERVER_ERROR)
                        .entity(new ExceptionBean("SQLException", "Query was not deleted!"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch(Exception e){
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("Exception", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * Get StoredQuery metadata
     * @param queryId - ID of query
     * @param userId - ID of user
     * @return StoredQuery with metadata or SQLException bean
     */
    @Path("{query_id}")
    @GET
    public Response getQuery(@PathParam("query_id") long queryId, @QueryParam("user_id") String userId){
        //StoredQueryBean testBean = new StoredQueryBean(queryId, "Testing query", "SELECT * FROM table;", "finished", 10, false, false, null, null);
        try {
            return Response.status(Status.OK)
                    .entity(QueryUtil.getQuery(queryId, userId))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("Exception", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}