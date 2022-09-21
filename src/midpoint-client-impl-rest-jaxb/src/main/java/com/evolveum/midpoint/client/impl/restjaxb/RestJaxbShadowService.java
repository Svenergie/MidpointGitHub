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

import com.evolveum.midpoint.client.api.PolicyService;
import com.evolveum.midpoint.client.api.ShadowService;
import com.evolveum.midpoint.client.api.exception.AuthenticationException;
import com.evolveum.midpoint.client.api.exception.AuthorizationException;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.FocusType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ShadowType;
import org.apache.cxf.jaxrs.client.WebClient;

import javax.ws.rs.core.Response;

/**
 * @author Viliam Repan (lazyman)
 */
public class RestJaxbShadowService extends RestJaxbObjectService<ShadowType> implements PolicyService<ShadowType>, ShadowService {

	public RestJaxbShadowService(final RestJaxbService service, final String oid) {
		super(service, ShadowType.class, oid);
	}

	@Override
	public OperationResultType importShadow() throws ObjectNotFoundException {
		String urlPrefix = RestUtil.subUrl(Types.findType(ShadowType.class).getRestPath(), getOid(), "import");

		Response response = getService().post(urlPrefix, null);

		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return response.readEntity(OperationResultType.class);
		}

		if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
			throw new ObjectNotFoundException("Cannot import shadow. No such object");
		}

		return null;
	}

	@Override
	public FocusType owner() throws ObjectNotFoundException {
		// todo implement
		return null;
	}
}
