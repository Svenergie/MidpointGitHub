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

import com.evolveum.midpoint.client.api.TaskOperationService;
import com.evolveum.midpoint.client.api.TaskService;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;

public class RestJaxbTaskService extends RestJaxbObjectService<TaskType> implements TaskService {

    private static final String PATH_SUSPEND = "suspend";
    private static final String PATH_RESUME = "resume";
    private static final String PATH_RUN = "run";

    public RestJaxbTaskService(RestJaxbService service, String oid) {
        super(service, TaskType.class, oid);
    }

    @Override
    public TaskOperationService suspend() {
        return new RestJaxbTaskOperationService(getService(), PATH_SUSPEND, getOid());
    }

    @Override
    public TaskOperationService resume() {
        return new RestJaxbTaskOperationService(getService(), PATH_RESUME, getOid());
    }

    @Override
    public TaskOperationService run() {
        return new RestJaxbTaskOperationService(getService(), PATH_RUN, getOid());
    }
}
