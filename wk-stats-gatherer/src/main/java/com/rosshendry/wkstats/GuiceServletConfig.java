package com.rosshendry.wkstats;

import java.util.List;

import javax.servlet.annotation.WebListener;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.ServletScopes;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

@WebListener
public class GuiceServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ServletModule() {
			
			@Override
			protected void configureServlets() {
				filter("/*").through(IsFromCron.class);
				
				serve("/tasks/stats").with(UpdateStats.class);
			}
		}, new AbstractModule() {

			@Override
			protected void configure() {
				bind(new TypeLiteral<List<String>>() {}).annotatedWith(Names.named("API Keys")).toProvider(ApiKeysProvider.class).in(ServletScopes.REQUEST);
				
				ThrowingProviderBinder.create(binder())
					.bind(PublisherProvider.class, Publisher.class)
					.to(ProductionPublisherProvider.class)
					.in(ServletScopes.REQUEST);
			}
		});
	}
}
