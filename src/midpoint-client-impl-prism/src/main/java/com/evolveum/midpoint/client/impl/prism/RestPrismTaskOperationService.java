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

import org.apache.hc.core5.http.HttpResponse;

import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.TaskFuture;
import com.evolveum.midpoint.client.api.TaskOperationService;
import com.evolveum.midpoint.client.api.exception.CommonException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;

public class RestPrismTaskOperationService implements TaskOperationService {

    private final String oid;
    private final String path;
    private final RestPrismService service;

    public RestPrismTaskOperationService(RestPrismService service, String oid, String path) {
        this.oid = oid;
        this.path = path;
        this.service = service;
    }

    @Override
    public TaskFuture<ObjectReference<TaskType>> apost() throws CommonException {

        String fullPath = "/tasks/" + oid + "/" + path;
        HttpResponse response = service.httpPost(fullPath, null);

        switch (response.getCode()) {
            case 200:
            case 202:
            case 204:
                RestPrismObjectReference<TaskType> task = new RestPrismObjectReference<>(oid, TaskType.class);
                return new RestPrismCompletedFuture<>(task);
            default:
                throw new UnsupportedOperationException("Implement other status codes, unsupported return status: " + response.getCode());
        }
    }
}
