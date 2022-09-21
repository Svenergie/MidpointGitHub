/*
 * Copyright (c) 2020 Evolveum
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
package com.evolveum.midpoint.client.impl.restjaxb;

import java.util.function.Function;
import javax.ws.rs.core.Response;

import com.evolveum.midpoint.client.api.ResourceOperationService;
import com.evolveum.midpoint.client.api.TaskFuture;
import com.evolveum.midpoint.client.api.exception.CommonException;

public class RestJaxbResourceOperationService<T> implements ResourceOperationService<T> {

    private RestJaxbService service;
    private String path;

    private Function<Response, T> responseHandler;

    public RestJaxbResourceOperationService(RestJaxbService service, String path, Function<Response, T> responseHandler) {
        this.service = service;
        this.path = path;
        this.responseHandler = responseHandler;
    }

    @Override
    public TaskFuture<T> apost() throws CommonException {

        Response response = service.post(path, null);

        T object = responseHandler.apply(response);
        return new RestJaxbCompletedFuture<>(object);

    }


}
