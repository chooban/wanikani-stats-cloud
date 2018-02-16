package com.rosshendry.wkstats;

import java.io.IOException;

import com.google.inject.throwingproviders.CheckedProvider;

public interface PublisherProvider<T> extends CheckedProvider<T> {

	@Override
	T get() throws IOException;

}
