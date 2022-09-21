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

import com.evolveum.midpoint.client.api.ObjectAddService;
import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.TaskFuture;
import com.evolveum.midpoint.client.api.exception.ObjectAlreadyExistsException;
import com.evolveum.midpoint.client.api.exception.SchemaException;
import com.evolveum.midpoint.schema.constants.ObjectTypes;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

import java.util.List;

public class RestPrismObjectAddService<O extends ObjectType> implements ObjectAddService<O> {

    private RestPrismService service;
    private ObjectTypes type;
    private O object;
    private List options;

    public RestPrismObjectAddService(RestPrismService service, ObjectTypes type, O object) {
        this.service = service;
        this.type = type;
        this.object = object;
    }

    public RestPrismObjectAddService setOptions(List<String> options) {
        this.options = options;
        return this;
    }

    @Override
    public TaskFuture<ObjectReference<O>> apost() throws ObjectAlreadyExistsException, SchemaException {

        String oid = service.addObject(type, object, options);

        RestPrismObjectReference<O> ref = new RestPrismObjectReference<>(oid, type.getClassDefinition());
        return new RestPrismCompletedFuture<>(ref);
    }
}
