/**
 * Copyright (c) 2021, the WikiOIE AUTHORS.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Bari nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007
 *
 */
package di.uniba.it.wikioie.indexing.service;

import com.google.gson.Gson;
import di.uniba.it.wikioie.data.Config;
import di.uniba.it.wikioie.indexing.SearchDoc;
import di.uniba.it.wikioie.indexing.SearchTriple;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private static final int RS_SIZE = 250;

    private static String titlePrefix = "";

    private static String filesPath = "";

    static {
        titlePrefix = Config.getInstance().getValue("server.title.prefix");
        filesPath = Config.getInstance().getValue("files.dir");
    }

    /**
     *
     * @param query
     * @return
     */
    @GET
    @Path("/triple")
    @Produces("application/json")
    public Response triple(@QueryParam("q") String query) {
        List<SearchTriple> triples;
        try {
            triples = IndexWrapper.getInstance().getIdx().searchTriple(query, RS_SIZE);
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
    public Response tripleByDocId(@QueryParam("docid") String docid) {
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

    @GET
    @Path("/triplebyparentandtitle")
    @Produces("application/json")
    public Response tripleByParentAndTitle(@QueryParam("p") String parent, @QueryParam("t") String title) {
        List<SearchTriple> triples;
        try {
            triples = IndexWrapper.getInstance().getIdx().searchTripleByParentAndTitle(parent, title, RS_SIZE);
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
     * @param id
     * @return
     */
    @GET
    @Path("/docbyid")
    @Produces("application/json")
    public Response docbyid(@QueryParam("id") String id) {
        SearchDoc doc;
        try {
            doc = IndexWrapper.getInstance().getIdx().getDocById(id);
            if (titlePrefix.length() > 0) {
                doc.setTitle(doc.getTitle().replace(titlePrefix, ""));
            }
        } catch (IOException ex) {
            Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            doc = new SearchDoc("-1");
            doc.setDatasetId("-1");
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(doc);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    /**
     *
     * @param dsId
     * @return
     */
    @GET
    @Path("/doc")
    @Produces("application/json")
    public Response doc(@QueryParam("id") String dsId) {
        List<SearchDoc> rs;
        try {
            rs = IndexWrapper.getInstance().getIdx().getDocByDatasetId(dsId);
            if (titlePrefix.length() > 0) {
                rs.stream().forEach(e -> e.setTitle(e.getTitle().replace(titlePrefix, "")));
            }
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
            rs = IndexWrapper.getInstance().getIdx().searchDocByTitle(query, RS_SIZE);
            if (titlePrefix.length() > 0) {
                rs.stream().forEach(e -> e.setTitle(e.getTitle().replace(titlePrefix, "")));
            }
        } catch (IOException | ParseException ex) {
            Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
            rs = new ArrayList<>();
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(rs);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/download")
    public Response downloadFile(@QueryParam("id") String id) {
        try {
            File file = new File(filesPath + "/" + id);
            StreamingOutput fileStream = (java.io.OutputStream output) -> {
                try {
                    java.nio.file.Path path = Paths.get(filesPath + "/" + id);
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } catch (IOException ex) {
                    throw new WebApplicationException("File download IO error: " + ex.getMessage());
                } catch (Exception ex) {
                    throw new WebApplicationException("File download general error: " + ex.getMessage());
                }
            };
            return Response
                    .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = " + file.getName())
                    .build();
        } catch (Exception ex) {
            throw new WebApplicationException("File download general error: " + ex.getMessage());
        }
    }

}
