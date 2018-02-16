package com.rosshendry.wkstats;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.utils.SystemProperty;

@Singleton
public final class IsFromCron implements Filter {
	private boolean isProd = false;
	private static final Logger log = Logger.getLogger(IsFromCron.class.getName());

	public IsFromCron() {
		isProd = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException 
	{
		if (isProd && ((HttpServletRequest) request).getHeader("X-Appengine-Cron") == null) {
			((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
			log.info("Rejecting non-cron request");
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// No-op
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Cron filter initiated");
	}
}
