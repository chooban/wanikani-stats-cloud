package com.rosshendry.wkstats;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

@WebServlet(name = "WanikaniStats", urlPatterns = { "/tasks/stats" })
public class HelloAppEngine extends HttpServlet {

	private static final long serialVersionUID = -703400938671598052L;
	private static final Logger log = Logger.getLogger(HelloAppEngine.class.getName());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request.getHeader("X-Appengine-Cron") == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Publisher publisher = Publisher.newBuilder(TopicName.of("wanikani-stats", "getwkstats")).build();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			PreparedQuery pq = datastore.prepare(new Query("ApiKey"));
			List<Entity> results = pq.asList(FetchOptions.Builder.withChunkSize(5));
			
			log.log(Level.INFO, String.format("Got %d keys", results.size()));
			
			for (Entity e : results) {
				ObjectNode apiKey = objectMapper.createObjectNode();
				apiKey.put("apiKey", (String) e.getProperty("key"));

				String messageString = objectMapper.writeValueAsString(apiKey);
				ByteString data = ByteString.copyFromUtf8(messageString);
				
				log.log(Level.INFO, String.format("Sending %s to %s", messageString, publisher.getTopicName().toString()));
				
				PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
				publisher.publish(pubsubMessage);
			}
		} finally {
			if (publisher != null) {
				try {
					publisher.shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
}