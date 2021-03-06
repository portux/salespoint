/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.salespointframework.util;

import net.minidev.json.JSONArray;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

/**
 * Little helper to build a changelog from the tickets of a particular milestone.
 * 
 * @author Oliver Gierke
 */
class ChangelogCreator {

	private static final String MILESTONE_ID = "14";
	private static final String URI_TEMPLATE = "https://api.github.com/repos/st-tu-dresden/salespoint/issues?milestone={id}&state=closed&sort=updated";
	private static final String TICKET_TEMPLATE = "- {linkbase}/%s[#%s] - %s";

	public static void main(String... args) throws Exception {

		RestTemplate template = setUpRestTemplate();

		String response = template.getForObject(URI_TEMPLATE, String.class, MILESTONE_ID);

		JsonPath titlePath = JsonPath.compile("$[*].title");
		JsonPath idPath = JsonPath.compile("$[*].number");

		JSONArray titles = titlePath.read(response);
		Iterator<Object> ids = ((JSONArray) idPath.read(response)).iterator();

		String date = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());
		String milestone = JsonPath.read(response, "$[1].milestone.title").toString();

		System.out.println(":numbered!:");
		System.out.println(String.format("[%s]", milestone.replace(" ", "-")));
		System.out.println(String.format("== %s\n", milestone));
		System.out.println(String.format("Release date: %s\n", date));

		for (Object title : titles) {

			Object id = ids.next();

			String ticket = String.format(TICKET_TEMPLATE, id, id, title);
			ticket = ticket.endsWith(".") ? ticket : ticket.concat(".");

			System.out.println(ticket);
		}
	}

	private static RestTemplate setUpRestTemplate() throws IOException {

		FileSystemResource resource = new FileSystemResource("env.properties");
		ResourcePropertySource propertySource = new ResourcePropertySource(resource);
		String username = propertySource.getProperty("github.username").toString();
		String password = propertySource.getProperty("github.password").toString();

		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

		AuthCache authCache = new BasicAuthCache();
		authCache.put(new HttpHost("api.github.com", 443, "https"), new BasicScheme());

		final HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credentialsProvider);
		context.setAuthCache(authCache);

		ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()) {

			@Override
			protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
				return context;
			}
		};

		RestTemplate template = new RestTemplate();
		template.setRequestFactory(factory);

		return template;
	}
}
