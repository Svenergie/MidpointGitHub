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
package com.evolveum.midpoint.client.api.scripting;

import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultStatusType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;

import java.io.Serializable;

/**
 * Abstracted result of processing a single object by midPoint script.
 *
 * @author mederly
 */
public class ObjectProcessingOutput<T extends OperationSpecificData> implements Serializable {

	/**
	 * OID of the object. May or may not exist.
	 */
	private String oid;

	/**
	 * Name of the object. May be null if it couldn't be determined.
	 */
	private String name;

	/**
	 * Object itself. May be null if it couldn't be determined or if it's eliminated to save heap.
	 * (If present, the name is present as well.)
	 */
	private ObjectType object;

	/**
	 * Status of the processing.
	 */
	private OperationResultStatusType status;

	/**
	 * Resulting detail message (if present; usually in error conditions).
	 */
	private String message;

	/**
	 * Full operation result. May be null if it's eliminated to save heap.
	 */
	private OperationResultType result;

	/**
	 * Additional data. This is specific to given operation.
	 */
	private T data;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectType getObject() {
		return object;
	}

	public void setObject(ObjectType object) {
		this.object = object;
	}

	public OperationResultStatusType getStatus() {
		return status;
	}

	public void setStatus(OperationResultStatusType status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public OperationResultType getResult() {
		return result;
	}

	public void setResult(OperationResultType result) {
		this.result = result;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ObjectProcessingOutput{" +
				"oid='" + oid + '\'' +
				", name='" + name + '\'' +
				", status=" + status +
				", message='" + message + '\'' +
				", data=" + data +
				'}';
	}
}
