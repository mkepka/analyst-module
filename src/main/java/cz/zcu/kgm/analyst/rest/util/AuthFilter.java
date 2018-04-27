package cz.zcu.kgm.analyst.rest.util;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class AuthFilter implements ContainerRequestFilter{

    @Override
    public void filter(ContainerRequestContext reqCont) throws IOException {
        String method = reqCont.getMethod();
        System.out.println("Request method: "+method);
        /*UriBuilder b = reqCont.getUriInfo().getRequestUriBuilder();
        b.queryParam("user", "tester");
        reqCont.setRequestUri(b.build());*/
    }
    
    private void checkLiferaySession(){
        
    }
}