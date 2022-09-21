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
package com.evolveum.midpoint.client.impl.prism.handler;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.http.HttpEntity;

import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.util.exception.SchemaException;

public class HttpClientPrismResponseHandler<T> extends AbstractHttpClientResponseHandler<T> {

    private PrismContext prismContext;

    public HttpClientPrismResponseHandler(PrismContext ctx) {
        super();
        this.prismContext = ctx;
    }

    @Override
    public T handleEntity(HttpEntity entity) throws IOException {
        try {
            return (T) prismContext.parserFor(entity.getContent()).language("xml").parseRealValue();
        } catch (SchemaException e) {
            throw new IOException(e);
        }
    }

}
