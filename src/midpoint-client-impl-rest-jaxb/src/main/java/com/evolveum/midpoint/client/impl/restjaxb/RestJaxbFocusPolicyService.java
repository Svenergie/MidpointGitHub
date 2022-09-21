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

import javax.ws.rs.core.Response;

import com.evolveum.midpoint.client.api.FocusPolicyService;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;

public class RestJaxbFocusPolicyService<T> implements FocusPolicyService<T> {

    private RestJaxbService service;
    private String path;
    private Class<T> expectedType;

    public RestJaxbFocusPolicyService(RestJaxbService service, String path, Class<T> expectedType) {
        this.service = service;
        this.path = path;
        this.expectedType = expectedType;
    }
    @Override
    public T get() throws ObjectNotFoundException {
        Response response = service.get(path);

        switch (response.getStatus()) {
            case 200:
                return response.readEntity(expectedType);
            default:
                throw new IllegalStateException("Unexpected value: " + response.getStatus());
        }
    }
}
