package cz.zcu.kgm.analyst.rest;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.geojson.FeatureCollection;

import com.fasterxml.jackson.databind.node.ArrayNode;

import cz.zcu.kgm.analyst.db.model.StoredQuery;
import cz.zcu.kgm.analyst.rest.beans.ExceptionBean;
import cz.zcu.kgm.analyst.util.FeatureUtil;

/**
 * Service is providing data publication in different formats 
 * @author mkepka
 *
 */
@Path("/query")
public class DataRest {

    /**
     * localhost:8282/ant/rest/query/1518731867338/geojson?user_id=tester
     * @param queryId - ID of query
     * @param userId - ID of user
     * @return
     */
    @Path("/{query_id}/geojson")
    @GET
    public Response getGeoJsonData(@PathParam("query_id") long queryId, @QueryParam("user_id") String userId){
        try {
            StoredQuery sq = FeatureUtil.getMetadataForSelecting(queryId, userId);
            if(sq.getHasGeom()){
                FeatureCollection fc = FeatureUtil.selectResultDataGeoJsonRef(sq);
                return Response.status(Status.OK)
                        .entity(fc)
                        .type("application/geo+json"+";"+MediaType.CHARSET_PARAMETER+"=utf-8")
                        .build();
            } else {
                return Response.status(Status.BAD_REQUEST)
                        .entity(new ExceptionBean("SQLException", "Selected query does not contain geometry!"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }
        catch (SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("SQLException", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    /**
     * localhost:8282/ant/rest/query/1518521080748/geojson-crs
     * @param queryId - ID of query
     * @param userId - ID of user
     * @return
     */
    @Path("/{query_id}/geojson-crs")
    @GET
    public Response getGeoJsonCrsData(@PathParam("query_id") long queryId, @QueryParam("user_id") String userId){
        try {
            StoredQuery sq = FeatureUtil.getMetadataForSelecting(queryId, userId);
            if(sq.getHasGeom()){
                FeatureCollection fc = FeatureUtil.selectResultDataGeoJson(sq);
                return Response.status(Status.OK)
                        .entity(fc)
                        .type("application/geo+json"+";"+MediaType.CHARSET_PARAMETER+"=utf-8")
                        .build();
            } else {
                return Response.status(Status.BAD_REQUEST)
                        .entity(new ExceptionBean("SQLException", "Selected query does not contain geometry!"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }
        catch (SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("SQLException", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    @Path("/{query_id}/json")
    @GET
    public Response getJsonData(@PathParam("query_id") long queryId, @QueryParam("user_id") String userId){
        try {
            StoredQuery sq = FeatureUtil.getMetadataForSelecting(queryId, userId);
            ArrayNode json = FeatureUtil.selectNonGeoResultData(sq);
            return Response.status(Status.OK)
                    .entity(json)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch (SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("SQLException", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    @Path("/{query_id}/csv")
    @GET
    public Response getCSVData(@PathParam("query_id") long queryId, @QueryParam("user_id") String userId){
        try {
            StoredQuery sq = FeatureUtil.getMetadataForSelecting(queryId, userId);
            String csv = FeatureUtil.selectNonGeoResultDataCSV(sq);
            return Response.status(Status.OK)
                    .entity(csv)
                    .type("text/csv"+";"+MediaType.CHARSET_PARAMETER+"=utf-8")
                    .build();
        }
        catch (SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("SQLException", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    @Path("/{query_id}/kml")
    @GET
    public Response getKMLData(@PathParam("query_id") long queryId, @QueryParam("user_id") String userId, @QueryParam("bbox") String BBOX){
        try {
            StoredQuery sq = FeatureUtil.getMetadataForSelecting(queryId, userId);
            String kml = FeatureUtil.getGeomFeaturesFromResultTable(sq, userId);
            return Response.status(Status.OK)
                    .entity(kml)
                    .type("application/vnd.google-earth.kml+xml")
                    .build();
        }
        catch (SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(new ExceptionBean("SQLException", e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}