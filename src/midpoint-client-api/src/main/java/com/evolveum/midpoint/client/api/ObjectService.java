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
package com.evolveum.midpoint.client.api;

import com.evolveum.midpoint.client.api.exception.AuthenticationException;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.client.api.verb.Delete;
import com.evolveum.midpoint.client.api.verb.Get;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

import java.util.List;

/**
 * @author semancik
 *
 */
public interface ObjectService<O extends ObjectType> extends Get<O>, Delete<O> {

	ObjectModifyService<O> modify() throws ObjectNotFoundException;

	//TODO cleanup interface, options/include/exclude should be probably handled also in fluent style.
    O get(List<String> options) throws ObjectNotFoundException;

    O get(List<String> options, List<String> include, List<String> exclude) throws ObjectNotFoundException;
}
