/*
 * Copyright (c) 2017-2020 Evolveum
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.midpoint.client.impl.restjaxb;

import com.evolveum.midpoint.client.api.ResourceCollectionService;
import com.evolveum.midpoint.client.api.ResourceService;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ResourceType;

/**
 * @author Viliam Repan (lazyman)
 */
public class RestJaxbResourceCollectionService extends RestJaxbObjectCollectionService<ResourceType> implements ResourceCollectionService {

	public RestJaxbResourceCollectionService(final RestJaxbService service) {
		super(service, Types.RESOURCES.getRestPath(), ResourceType.class);
	}

	@Override
	public ResourceService oid(String oid) {
		return new RestJaxbResourceService(getService(), oid);
	}
}
