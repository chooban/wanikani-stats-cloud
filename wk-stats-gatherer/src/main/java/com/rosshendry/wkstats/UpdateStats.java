package com.rosshendry.wkstats;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Provider;

@Singleton
public class UpdateStats extends HttpServlet {

	private static final long serialVersionUID = -703400938671598052L;
	private static final Logger log = Logger.getLogger(UpdateStats.class.getName());
	private final Provider<List<String>> keysProvider;
	private final StatsRequestPublisher publisher;

	@Inject
	UpdateStats(
			@Named("API Keys") Provider<List<String>> keysProvider,
			StatsRequestPublisher publisher
	) {
		this.keysProvider = keysProvider;
		this.publisher = publisher;
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<String> keys = this.keysProvider.get();
		log.info(String.format("%s keys found", keys.isEmpty() ? "No" : keys.size()));
		
		for (String e : this.keysProvider.get()) {
			log.log(Level.FINE, String.format("Getting stats for %s", e));
			this.publisher.publishStatsRequest(e);
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
}