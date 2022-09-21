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

import com.evolveum.midpoint.client.api.TaskOperationService;
import com.evolveum.midpoint.client.api.TaskService;
import com.evolveum.midpoint.schema.constants.ObjectTypes;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;

public class RestPrismTaskService extends RestPrismObjectService<TaskType> implements TaskService {

    private static final String SUSPEND_TASK = "suspend";
    private static final String RESUME_TASK = "resume";
    private static final String RUN_TASK = "run";

    public RestPrismTaskService(RestPrismService service, String oid) {
        super(service, ObjectTypes.TASK, oid);
    }

    @Override
    public TaskOperationService suspend() {
        return new RestPrismTaskOperationService(getService(), getOid(), SUSPEND_TASK);
     }

    @Override
    public TaskOperationService resume() {
        return new RestPrismTaskOperationService(getService(), getOid(), RESUME_TASK);
    }

    @Override
    public TaskOperationService run() {
        return new RestPrismTaskOperationService(getService(), getOid(), RUN_TASK);
    }
}
