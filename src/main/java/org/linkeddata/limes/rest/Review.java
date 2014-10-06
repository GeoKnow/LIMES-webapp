package org.linkeddata.limes.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

@Path("/review")
public class Review {

    private static final Logger log = Logger.getLogger(Review.class);

    @GET
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response review(@PathParam("uuid") String uuid,
	    @Context ServletContext context) {

	String filePath = context.getRealPath(File.separator);

	BufferedReader br = null;
	List<String> review = new ArrayList<String>();
	List<String> accepted = new ArrayList<String>();

	try {

	    String sCurrentLine;

	    br = new BufferedReader(new FileReader(filePath + "result"
		    + File.separator + uuid + "_accepted.nt"));

	    while ((sCurrentLine = br.readLine()) != null) {
		accepted.add(sCurrentLine);
	    }

	    br.close();

	    br = null;
	    br = new BufferedReader(new FileReader(filePath + "result"
		    + File.separator + uuid + "_reviewme.nt"));

	    while ((sCurrentLine = br.readLine()) != null) {
		review.add(sCurrentLine);
	    }

	} catch (IOException e) {
	    log.error(e);
	    e.printStackTrace();
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		    .entity(e.getMessage())
		    .header("Access-Control-Allow-Origin", "*")
		    .header("Access-Control-Allow-Methods", "GET").build();
	} finally {
	    try {
		if (br != null)
		    br.close();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	}

	Gson gson = new Gson();

	Map<String, List> map = new HashMap<String, List>();
	map.put("review", review);
	map.put("accepted", accepted);
	gson.toJson(review.toArray());

	String json = gson.toJson(map);

	return Response.ok(json).header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Methods", "GET").build();
    }
}
