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

import java.util.Collection;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

public interface ConditionEntry<O extends ObjectType> {

	MatchingRuleEntry<O> eq(Object... values);
	public MatchingRuleEntry<O> eq();
	public MatchingRuleEntry<O> eqPoly(String orig, String norm);
	public MatchingRuleEntry<O> eqPoly(String orig);
	public MatchingRuleEntry<O> gt(Object value);
	public AtomicFilterExit<O> gt();
	public MatchingRuleEntry<O> ge(Object value);
	public AtomicFilterExit<O> ge();
	public MatchingRuleEntry<O> lt(Object value);
	public AtomicFilterExit<O> lt();
	public MatchingRuleEntry<O> le(Object value);
	public AtomicFilterExit<O> le();
	public MatchingRuleEntry<O> startsWith(Object value);
	public MatchingRuleEntry<O>  startsWithPoly(String orig, String norm);
	public MatchingRuleEntry<O>  startsWithPoly(String orig);
	public MatchingRuleEntry<O>  endsWith(Object value);
	public MatchingRuleEntry<O>  endsWithPoly(String orig, String norm);
	public MatchingRuleEntry<O>  endsWithPoly(String orig);
	public MatchingRuleEntry<O>  contains(Object value);
	public MatchingRuleEntry<O>  containsPoly(String orig, String norm);
	public MatchingRuleEntry<O>  containsPoly(String orig);
	public AtomicFilterExit<O>  ref(QName relation);
	public AtomicFilterExit<O>  ref(ObjectReferenceType... value);
	public AtomicFilterExit<O>  ref(Collection<ObjectReferenceType> values);
	public AtomicFilterExit<O>  ref(String... oid);
	public AtomicFilterExit<O>  ref(String oid, QName targetTypeName);
	public AtomicFilterExit<O>  isNull();
}
