package com.dxfeed.atlassian;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;

public class CustomAsynchronousJiraRestClientFactory extends AsynchronousJiraRestClientFactory {

    public JiraRestClient createCustom(
            final URI serverUri,
            final AuthenticationHandler authenticationHandler,
            int socketTimeoutInSec) {
        final DisposableHttpClient httpClient = new CustomAsynchronousHttpClientFactory()
                .createClientCustom(serverUri, authenticationHandler,socketTimeoutInSec);
        return new AsynchronousJiraRestClient(serverUri, httpClient);
    }

    public JiraRestClient createWithBasicHttpAuthenticationCustom (
            final URI serverUri,
            final String username,
            final String password,
            final int socketTimeoutInSec) {
        return createCustom(serverUri, new BasicHttpAuthenticationHandler(username, password),socketTimeoutInSec);
    }
}
