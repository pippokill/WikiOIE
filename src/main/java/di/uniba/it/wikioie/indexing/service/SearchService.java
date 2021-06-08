/*
 * Copyright (C) 2020 pierpaolo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package di.uniba.it.wikioie.indexing.service;

import com.google.gson.Gson;
import di.uniba.it.wikioie.indexing.SearchDoc;
import di.uniba.it.wikioie.indexing.SearchTriple;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author pierpaolo
 */
@Path("search")
public class SearchService {

    /**
     *
     * @param query
     * @return
     */
    @GET
    @Path("/triple")
    @Produces("application/json")
    public Response triple(@DefaultValue("Italia") @QueryParam("q") String query) {
        List<SearchTriple> triples;
        try {
            triples = IndexWrapper.getInstance().getIdx().searchTriple(query, 100);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            triples = new ArrayList<>();
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(triples);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    /**
     *
     * @param docid
     * @return
     */
    @GET
    @Path("/triplebydocid")
    @Produces("application/json")
    public Response tripleByDocId(@DefaultValue("-1") @QueryParam("docid") String docid) {
        List<SearchTriple> triples;
        try {
            triples = IndexWrapper.getInstance().getIdx().getTriplesByDocid(docid);
        } catch (IOException ex) {
            Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            triples = new ArrayList<>();
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(triples);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    /**
     *
     * @param id
     * @return
     */
    @GET
    @Path("/docbyid")
    @Produces("application/json")
    public Response docbyid(@DefaultValue("-1") @QueryParam("id") String id) {
        SearchDoc doc;
        try {
            doc = IndexWrapper.getInstance().getIdx().getDocById(id);
        } catch (IOException ex) {
            Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            doc = new SearchDoc("-1");
            doc.setWikiId("-1");
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(doc);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    /**
     *
     * @param wikiId
     * @return
     */
    @GET
    @Path("/doc")
    @Produces("application/json")
    public Response doc(@DefaultValue("-1") @QueryParam("id") String wikiId) {
        List<SearchDoc> rs;
        try {
            rs = IndexWrapper.getInstance().getIdx().getDocByWikiId(wikiId);
        } catch (IOException ex) {
            Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            rs = new ArrayList<>();
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(rs);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    /**
     *
     * @param query
     * @return
     */
    @GET
    @Path("/docbytitle")
    @Produces("application/json")
    public Response docByTitle(@DefaultValue("Italia") @QueryParam("q") String query) {
        List<SearchDoc> rs;
        try {
            rs = IndexWrapper.getInstance().getIdx().searchDocByTitle(query, 100);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            rs = new ArrayList<>();
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(rs);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

}
