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

import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.prism.xml.ns._public.types_3.ItemPathType;

public interface AtomicFilterEntry<O extends ObjectType> {

//	AtomicFilterExit<O> all();
//	AtomicFilterExit<O> none();
//	AtomicFilterExit<O> undefined();
	ConditionEntry<O> item(ItemPathType itemPath);
	ConditionEntry<O> item(QName... qnames);
//	AtomicFilterExit<O> id(String... identifiers);
//	AtomicFilterExit<O> id(long... identifiers);
//	AtomicFilterExit<O> ownerId(String... identifiers);
//	AtomicFilterExit<O> ownerId(long... identifiers);
	AtomicFilterExit<O> isDirectChildOf(ObjectReferenceType value);
	AtomicFilterExit<O> isChildOf(ObjectReferenceType value);
	AtomicFilterExit<O> isDirectChildOf(String oid);
	AtomicFilterExit<O> isChildOf(String oid);
//	AtomicFilterExit<O> isParentOf(ObjectReferenceType value);            // reference should point to OrgType
//	AtomicFilterExit<O> isParentOf(String oid);                           // oid should be of an OrgType
////	AtomicFilterExit<O> isInScopeOf(String oid, OrgFilter.Scope scope);
////	AtomicFilterExit<O> isInScopeOf(PrismReferenceValue value, OrgFilter.Scope scope);
	AtomicFilterExit<O> isRoot() ;
//	AtomicFilterExit<O> fullText(String... words);
//	FilterEntryOrEmpty<O> block();
////	FilterEntryOrEmpty<O> type(Class<? extends Containerable> type) ;
//	FilterEntryOrEmpty<O> exists(QName... names) ;
}
