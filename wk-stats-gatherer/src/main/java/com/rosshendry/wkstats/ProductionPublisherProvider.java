package com.rosshendry.wkstats;

import java.io.IOException;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;

public class ProductionPublisherProvider implements PublisherProvider<Publisher> {

	@Override
	public Publisher get() throws IOException {
		return Publisher.newBuilder(TopicName.of("wanikani-stats", "getwkstats")).build();
	}

}
