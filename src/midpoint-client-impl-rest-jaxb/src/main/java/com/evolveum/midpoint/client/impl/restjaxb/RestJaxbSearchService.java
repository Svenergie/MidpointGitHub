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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.evolveum.midpoint.client.api.SearchResult;
import com.evolveum.midpoint.client.api.SearchService;
import com.evolveum.midpoint.client.api.exception.AuthenticationException;
import com.evolveum.midpoint.client.api.exception.AuthorizationException;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.client.api.query.FilterEntryOrEmpty;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ObjectListType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.prism.xml.ns._public.query_3.QueryType;

/**
 * @author semancik
 * @author katkav
 *
 */
public class RestJaxbSearchService<O extends ObjectType> extends AbstractObjectTypeWebResource<O> implements SearchService<O> {

	private QueryType query;

	public RestJaxbSearchService(final RestJaxbService service, final Class<O> type) {
		this(service, type, null);
	}

	public RestJaxbSearchService(final RestJaxbService service, final Class<O> type, final QueryType query) {
		super(service, type);
		this.query = query;
	}

		@Override
	public SearchResult<O> get() throws ObjectNotFoundException {
	    return get(null);
	}

    @Override
    public SearchResult<O> get(List<String> options) throws ObjectNotFoundException {
        String path = "/" + Types.findType(getType()).getRestPath() + "/search";
        Map<String, List<String>> queryParams = null;
        if (options != null && !options.isEmpty()) {
            queryParams = new HashMap<>();
            queryParams.put("options", options);
        }
        Response response = getService().post(path, query, queryParams); // TODO params

        if (Status.OK.getStatusCode() == response.getStatus()) {
            return new JaxbSearchResult<>(getSearchResultList(response));
        }

        if (Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
            throw new ObjectNotFoundException("Cannot search objects. No such service");
        }
        return null;
    }

    @Override
	public FilterEntryOrEmpty<O> queryFor(Class<O> type) {
		return new FilterBuilder<O>(getService(), getType());
	}

	@SuppressWarnings("unchecked")
	private List<O> getSearchResultList(Response response) {
		ObjectListType resultList = response.readEntity(ObjectListType.class);
		return (List<O>) resultList.getObject();
	}
}
