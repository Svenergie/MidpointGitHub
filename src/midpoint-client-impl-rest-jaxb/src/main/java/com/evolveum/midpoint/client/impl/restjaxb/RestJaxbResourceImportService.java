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

import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.ResourceImportService;
import com.evolveum.midpoint.client.api.ResourceOperationService;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;

public class RestJaxbResourceImportService implements ResourceImportService {

    private RestJaxbService service;
    private String path;

    public RestJaxbResourceImportService(RestJaxbService service, String path) {
        this.service = service;
        this.path = path;
    }

    @Override
    public ResourceOperationService<ObjectReference<TaskType>> objectClass(String objectClass) {
        path = RestUtil.subUrl(path, objectClass);
        return new RestJaxbResourceOperationService<>(service, path, response -> handleResponse(response));
    }

    private ObjectReference<TaskType> handleResponse(Response response) {
        if (Response.Status.SEE_OTHER.getStatusCode() == response.getStatus()) {
            String oid = RestUtil.getOidLastFromLocation(response);
            return new RestJaxbObjectReference<>(service, TaskType.class, oid);
        }
        //TODO improve error handling and other status handling
        throw new IllegalStateException("Something unexpected happened");
    }

}
