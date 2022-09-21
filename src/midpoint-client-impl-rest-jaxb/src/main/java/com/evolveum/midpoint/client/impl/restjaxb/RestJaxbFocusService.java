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

import com.evolveum.midpoint.client.api.FocusPolicyService;
import com.evolveum.midpoint.client.api.FocusService;
import com.evolveum.midpoint.client.api.Focus;
import com.evolveum.midpoint.client.api.ValidateGenerateRpcService;
import com.evolveum.midpoint.xml.ns._public.common.common_3.CredentialsPolicyType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.FocusType;

public class RestJaxbFocusService<F extends FocusType> extends RestJaxbObjectService<F> implements FocusService<F> {

    public RestJaxbFocusService(RestJaxbService service, Class<F> type, String oid) {
        super(service, type, oid);
    }

    @Override
    public Focus<F> credential(){
        return new RestJaxbFocusCredentialService<>(getService(),  getType(), getOid());
    }


    @Override
    public ValidateGenerateRpcService generate() {
        String restPath = RestUtil.subUrl(Types.findType(getType()).getRestPath(), getOid());
        restPath += "/generate";
        return new RestJaxbValidateGenerateRpcService(getService(), restPath);
    }

    @Override
    public ValidateGenerateRpcService validate() {
        String restPath = RestUtil.subUrl(Types.findType(getType()).getRestPath(), getOid());
        restPath += "/validate";
        return new RestJaxbValidateGenerateRpcService(getService(), restPath);
    }

    @Override
    public FocusPolicyService<CredentialsPolicyType> credentialsPolicy() {
        String restPath = RestUtil.subUrl(Types.findType(getType()).getRestPath(), getOid(), "policy");
        return new RestJaxbFocusPolicyService<>(getService(), restPath, CredentialsPolicyType.class);
    }
}
