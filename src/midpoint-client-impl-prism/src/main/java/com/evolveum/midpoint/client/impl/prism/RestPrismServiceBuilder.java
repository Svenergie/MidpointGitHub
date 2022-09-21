/*
 * Copyright (c) 2021 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.midpoint.client.impl.prism;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.xml.sax.SAXException;

import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.util.PrismContextFactory;
import com.evolveum.midpoint.schema.MidPointPrismContextFactory;
import com.evolveum.midpoint.util.exception.SchemaException;

public class RestPrismServiceBuilder {

    private PrismContext prismContext;
    private String baseUrl;
    private String username;
    private String password;

    public static RestPrismServiceBuilder create() throws FileNotFoundException, com.evolveum.midpoint.client.api.exception.SchemaException {
        RestPrismServiceBuilder builder = new RestPrismServiceBuilder();
        PrismContextFactory pcf = new MidPointPrismContextFactory();
        try {
            PrismContext existingPrism = PrismContext.get();
            if (existingPrism != null) {
                builder.prismContext = PrismContext.get();
            } else {
                builder.prismContext = pcf.createPrismContext();
                builder.prismContext.initialize();
            }
        } catch (SchemaException | SAXException | IOException e) {
            throw new com.evolveum.midpoint.client.api.exception.SchemaException(e);
        }

        return builder;
    }

    public RestPrismServiceBuilder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public RestPrismServiceBuilder username(String username) {
        this.username = username;
        return this;
    }

    public RestPrismServiceBuilder password(String password) {
        this.password = password;
        return this;
    }

    public RestPrismService build() {

        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofSeconds(5))
                        .build())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setConnectionTimeToLive(TimeValue.ofMinutes(1L))
                .build();

        BasicCredentialsProvider provider = new BasicCredentialsProvider();

        URI uri = URI.create(baseUrl);
        HttpHost httpHost = HttpHost.create(uri);
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password.toCharArray());
        provider.setCredentials(new AuthScope(httpHost), usernamePasswordCredentials);
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultCredentialsProvider(provider)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setAuthenticationEnabled(true)
                        .setConnectTimeout(Timeout.ofSeconds(60))
                        .setResponseTimeout(Timeout.ofSeconds(60))
                        .build())
                .build();
        RestPrismService service = new RestPrismService(client, baseUrl, prismContext);
        return service;
    }



}
