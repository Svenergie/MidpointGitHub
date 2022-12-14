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
package com.evolveum.midpoint.client.api.query;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.prism.xml.ns._public.types_3.ItemPathType;

public interface FilterExit<O extends ObjectType> extends QueryExit<O>{

	AtomicFilterExit<O> endBlock();
    FilterExit<O> asc(QName... names);
    FilterExit<O> asc(ItemPathType path);
    FilterExit<O> desc(QName... names);
    FilterExit<O> desc(ItemPathType path);
    FilterExit<O> offset(Integer n);
    FilterExit<O> maxSize(Integer n);

}
