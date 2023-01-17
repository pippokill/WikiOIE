/*
 * Questo software è stato sviluppato dal gruppo di ricerca SWAP del Dipartimento di Informatica dell'Università degli Studi di Bari.
 * Tutti i diritti sul software appartengono esclusivamente al gruppo di ricerca SWAP.
 * Il software non può essere modificato e utilizzato per scopi di ricerca e/o industriali senza alcun permesso da parte del gruppo di ricerca SWAP.
 * Il software potrà essere utilizzato a scopi di ricerca scientifica previa autorizzazione o accordo scritto con il gruppo di ricerca SWAP.
 * 
 * Bari, Marzo 2014
 */
package di.uniba.it.wikioie.indexing.service;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author pierpaolo
 */
@Provider
public class ResponseCorsFilter implements ContainerResponseFilter {

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Headers",
                "CSRF-Token, X-Requested-By, Authorization, Content-Type");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }

}
