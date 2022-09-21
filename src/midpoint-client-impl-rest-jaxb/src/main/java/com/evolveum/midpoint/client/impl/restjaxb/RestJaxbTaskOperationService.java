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
import com.evolveum.midpoint.client.api.TaskOperationService;
import com.evolveum.midpoint.client.api.TaskFuture;
import com.evolveum.midpoint.client.api.exception.CommonException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;

public class RestJaxbTaskOperationService implements TaskOperationService {

    private RestJaxbService service;
    private String path;
    private String oid;

    public RestJaxbTaskOperationService(RestJaxbService service, String path, String oid) {
        this.service = service;
        this.path = path;
        this.oid = oid;
    }

    @Override
    public TaskFuture<ObjectReference<TaskType>> apost() throws CommonException {

        String restPath = RestUtil.subUrl(Types.findType(TaskType.class).getRestPath(), oid, path);
        Response response = service.post(restPath, null);

        switch (response.getStatus()) {
            case 200:
            case 202:
            case 204:
                RestJaxbObjectReference<TaskType> task = new RestJaxbObjectReference<>(service, TaskType.class, oid);
                return new RestJaxbCompletedFuture<>(task);
            default:
                throw new UnsupportedOperationException("Implement other status codes, unsupported return status: " + response.getStatus());
        }

    }
}
