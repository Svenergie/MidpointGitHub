/*
 * Copyright (c) 2017-2020 Evolveum
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

import com.evolveum.midpoint.client.api.Focus;
import com.evolveum.midpoint.client.api.TaskFuture;
import com.evolveum.midpoint.client.api.exception.*;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ExecuteCredentialResetResponseType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ExecuteCredentialResetRequestType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;

import javax.ws.rs.core.Response;

/**
 *
 * @author Jakmor
 *
 */
public class RestJaxbFocusCredentialService<O extends ObjectType>  extends AbstractObjectWebResource<O> implements Focus<O>
{
    private ExecuteCredentialResetRequestType executeCredentialResetRequest;

    public RestJaxbFocusCredentialService(final RestJaxbService service, final Class<O> type, final String oid)
    {
        super(service, type, oid);
    }

    @Override
    public Focus<O> executeResetPassword(ExecuteCredentialResetRequestType executeCredentialResetRequest)
    {
        this.executeCredentialResetRequest = executeCredentialResetRequest;

        return this;
    }

    @Override
    public TaskFuture<ExecuteCredentialResetResponseType> apost() throws CommonException
    {

        String restPath = RestUtil.subUrl(Types.findType(getType()).getRestPath(), getOid()).concat("/credential");

        Response response = getService().post(restPath, executeCredentialResetRequest);

        switch (response.getStatus()) {
            case 200:
                ExecuteCredentialResetResponseType executeCredentialResetResponse = response.readEntity(ExecuteCredentialResetResponseType.class);
                return new RestJaxbCompletedFuture<>(executeCredentialResetResponse);
            case 409:
                OperationResultType operationResultType = response.readEntity(OperationResultType.class);
                throw new PolicyViolationException(operationResultType.getMessage());
            default:
                throw new UnsupportedOperationException("Implement other status codes, unsupported return status: " + response.getStatus());
        }
    }
}
