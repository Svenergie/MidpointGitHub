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

import org.apache.hc.core5.http.ProtocolException;

import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.ResourceImportService;
import com.evolveum.midpoint.client.api.ResourceOperationService;
import com.evolveum.midpoint.client.api.ResourceService;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.client.api.exception.SchemaException;
import com.evolveum.midpoint.schema.constants.ObjectTypes;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ResourceType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;

public class RestPrismResourceService extends RestPrismObjectService<ResourceType> implements ResourceService, ResourceImportService {

    private static final String TEST_RESOURCE_PATH = ObjectTypes.RESOURCE.getRestType() + "/%s/test";
    private static final String IMPORT_FROM_RESOURCE_PATH = ObjectTypes.RESOURCE.getRestType() + "/%s/import/%s";

    public RestPrismResourceService(RestPrismService service, ObjectTypes type, String oid) {
        super(service, type, oid);
    }

    @Override
    public ResourceOperationService<OperationResultType> test() throws ObjectNotFoundException {

        return new RestPrismResourceOperationService<>(getService(),
                String.format(TEST_RESOURCE_PATH, getOid()),
                (response) -> {
                    try {
                        return getService().parseOperationResult(response.getEntity());
                    } catch (SchemaException e) {
                        return null;
                    }
                });
    }

    @Override
    public ResourceImportService importFromResource() {
        return this;
    }

    @Override
    public ResourceOperationService<ObjectReference<TaskType>> objectClass(String objectClass) {
        return new RestPrismResourceOperationService<>(getService(),
                String.format(IMPORT_FROM_RESOURCE_PATH, getOid(), objectClass),
                (response) -> {
                    try {
                        String oid =  getService().getOidFromLocation(response);
                        return new RestPrismObjectReference<>(oid, TaskType.class);
                    } catch (ProtocolException e) {
                        return null;
                    }
                });
    }
}
