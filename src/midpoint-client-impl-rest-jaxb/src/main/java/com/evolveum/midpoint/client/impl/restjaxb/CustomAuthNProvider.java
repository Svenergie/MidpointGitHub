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

import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.AuthenticationException;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import com.evolveum.midpoint.client.api.AuthenticationChallenge;
import com.evolveum.midpoint.client.api.AuthenticationManager;
import com.evolveum.midpoint.client.api.exception.SchemaException;

public class CustomAuthNProvider<T extends AuthenticationChallenge> extends AbstractPhaseInterceptor<Message> {

	private static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	private AuthenticationManager<T> authenticationManager;
	private RestJaxbService service;

	public CustomAuthNProvider(AuthenticationManager<T> authenticationManager, RestJaxbService service) {
		super(Phase.UNMARSHAL);
		this.authenticationManager = authenticationManager;
		this.service = service;
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		Map<String, Object> headers = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));

		if (!isApplicable(headers)) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<String> header = (List<String>) headers.get(WWW_AUTHENTICATE);

		if (authenticationManager != null) {
			prepareChallenge(header);
			return;
		}

        setupSupportedAuthentications(header);
	}

    private void prepareChallenge(List<String> header) {
        String authenticationHeader = header.stream()
                .filter(h -> currentAuthenticationMatched(h))
                .findFirst().orElse(null);

        if (authenticationHeader == null) {
            return;
        }

        try {
            String challenge = findChallenge(authenticationHeader);
            if (challenge != null) {
                authenticationManager.setAuthenticationChallenge(challenge);
            }
        } catch (SchemaException | Base64Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
    }

    private boolean currentAuthenticationMatched(String header) {
        String[] challenge = header.split(" ");
        return authenticationManager.getType().equals(challenge[0]);
    }

    private String findChallenge(String authenticationHeader) throws Base64Exception {
        String[] authNConfig = authenticationHeader.split(" ");

        if (authNConfig.length < 2) {
            return null;
        }

        if (!AuthenticationType.SECQ.getType().equals(authNConfig[0])) {
            return null;
        }

        String challenge = authNConfig[1];
        if (challenge.startsWith("realm")) {
            return null;
        }

        if (challenge != null) {
            challenge = new String(Base64Utility.decode(challenge));
        }

        return challenge;
    }

    private void setupSupportedAuthentications(List<String> header) {
        for (String auhtN : header) {
            String[] authNType = auhtN.split(",");

            for (String s : authNType) {
                String[] type = s.split(" ");
                try {
                    AuthenticationType supportedAuthentication = AuthenticationType.getAuthenticationType(type[0]);
                    service.getSupportedAuthenticationsByServer().add(supportedAuthentication);
                } catch (SchemaException e) {
                    throw new Fault(e);
                }
            }
        }
    }

	private boolean isApplicable(Map<?, ?> headers) {
        return headers != null && headers.containsKey(WWW_AUTHENTICATE);
    }
}
