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

import com.evolveum.midpoint.client.api.Focus;
import com.evolveum.midpoint.client.api.FocusPolicyService;
import com.evolveum.midpoint.client.api.FocusService;
import com.evolveum.midpoint.client.api.ValidateGenerateRpcService;
import com.evolveum.midpoint.schema.constants.ObjectTypes;
import com.evolveum.midpoint.xml.ns._public.common.common_3.CredentialsPolicyType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.FocusType;

public class RestPrismFocusService<F extends FocusType> extends RestPrismObjectService<F> implements FocusService<F> {


    public RestPrismFocusService(RestPrismService service, ObjectTypes type, String oid) {
        super(service, type, oid);
    }

    @Override
    public Focus<F> credential() {
        return null;
    }

    @Override
    public ValidateGenerateRpcService generate() {
        return null;
    }

    @Override
    public ValidateGenerateRpcService validate() {
        return null;
    }

    @Override
    public FocusPolicyService<CredentialsPolicyType> credentialsPolicy() {
        return null;
    }


}
