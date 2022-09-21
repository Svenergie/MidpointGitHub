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

import com.evolveum.midpoint.client.api.TaskCollectionService;
import com.evolveum.midpoint.client.api.TaskService;
import com.evolveum.midpoint.schema.constants.ObjectTypes;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;

public class RestPrismTaskCollectionService extends RestPrismObjectCollectionService<TaskType> implements TaskCollectionService {

    public RestPrismTaskCollectionService(RestPrismService service) {
        super(service, ObjectTypes.TASK);
    }

    @Override
    public TaskService oid(String oid) {
        return new RestPrismTaskService(getService(), oid);
    }
}
