package com.rosshendry.wkstats;

import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.inject.Provider;

public class ApiKeysProvider implements Provider<List<String>> {

	@Override
	public List<String> get() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		PreparedQuery pq = datastore.prepare(new Query("ApiKey"));
		List<Entity> results = pq.asList(FetchOptions.Builder.withChunkSize(5));
		
		return results.stream().map((e) -> (String) e.getProperty("key")).collect(Collectors.toList());		
	}
}
