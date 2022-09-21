/*
 * Copyright (c) 2017-2018 Evolveum
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

import com.evolveum.midpoint.client.api.ExecuteScriptRpcService;
import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.TaskFuture;
import com.evolveum.midpoint.client.api.exception.CommonException;
import com.evolveum.midpoint.client.api.exception.ConfigurationException;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.client.api.exception.PolicyViolationException;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ExecuteScriptResponseType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_3.ExecuteScriptType;

import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Preliminary implementation. Adapt as necessary.
 *
 * @author mederly
 */
public class RestJaxbExecuteScriptRpcService<T> implements ExecuteScriptRpcService<T> {

	private RestJaxbService service;
	private String path;

	private ExecuteScriptType script;
	private boolean asynchronous;

	public RestJaxbExecuteScriptRpcService(RestJaxbService service, String path, ExecuteScriptType script, boolean asynchronous) {
		this.service = service;
		this.path = path;
		this.script = script;
		this.asynchronous = asynchronous;
	}

	@Override
	public TaskFuture<T> apost() throws CommonException {

		Map<String, List<String>> queryParams = null;
		if (asynchronous) {
			queryParams = new HashMap<>();
			queryParams.put("asynchronous", Collections.singletonList(String.valueOf(true)));
		}

		Response response = service.post(path, script, queryParams);

		switch (response.getStatus()) {
			case 200:
				ExecuteScriptResponseType executeScriptResponse = response.readEntity(ExecuteScriptResponseType.class);
				return new RestJaxbCompletedFuture<>((T) executeScriptResponse);
			case 201:
			    if (!asynchronous) {
			        throw new ConfigurationException("Location not present when executing script synchronously");
                }
				String oid = RestUtil.getOidFromLocation(response, path);
				RestJaxbObjectReference<TaskType> taskRef = new RestJaxbObjectReference<>(service, TaskType.class, oid);
				return new RestJaxbCompletedFuture<>((T) taskRef);
			case 409:
				OperationResultType operationResultType = response.readEntity(OperationResultType.class);
				throw new PolicyViolationException(operationResultType.getMessage());
			default:
				throw new UnsupportedOperationException("Implement other status codes, unsupported return status: " + response.getStatus());
		}

	}
}
