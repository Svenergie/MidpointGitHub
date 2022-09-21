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

import java.util.function.Function;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import com.evolveum.midpoint.client.api.ResourceOperationService;
import com.evolveum.midpoint.client.api.TaskFuture;
import com.evolveum.midpoint.client.api.exception.CommonException;

public class RestPrismResourceOperationService<T> implements ResourceOperationService<T> {

    private RestPrismService service;
    private String path;
    private Function<CloseableHttpResponse, T> responseHandler;


    public RestPrismResourceOperationService(RestPrismService service, String path, Function<CloseableHttpResponse, T> responseHandler) {
        this.service = service;
        this.path = path;
        this.responseHandler = responseHandler;
    }


    @Override
    public TaskFuture<T> apost() throws CommonException {
        CloseableHttpResponse response = service.httpPost(path, null);
        T parsedResponse = responseHandler.apply(response);
        return new RestPrismCompletedFuture<>(parsedResponse);

    }
}
