package com.rosshendry.wkstats;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

public class StatsRequestPublisher {

	private static final Logger log = Logger.getLogger(StatsRequestPublisher.class.getName());
	private final PublisherProvider<Publisher> publisher;
	
	@Inject
	StatsRequestPublisher(PublisherProvider<Publisher> publisher) {
		this.publisher = publisher;
	}
	
	void publishStatsRequest(String apiKey) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(apiKey));
		Publisher p = null;
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode keyNode = objectMapper.createObjectNode();
		keyNode.put("apiKey", apiKey);

		try {
			p = publisher.get();
			String messageString = objectMapper.writeValueAsString(keyNode);
			ByteString data = ByteString.copyFromUtf8(messageString);
			PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();			
			log.log(Level.FINEST, String.format("Sending message: %s", messageString));					
			publisher.get().publish(pubsubMessage);
		} catch (JsonProcessingException e) {			
			log.log(Level.INFO, "Cannot construct JSON for pubsub", e);
		} catch (IOException e) {
			log.log(Level.INFO, "Cannot get pubsub publisher", e);			
		} finally {
			if (p != null) {
				try {
					p.shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
