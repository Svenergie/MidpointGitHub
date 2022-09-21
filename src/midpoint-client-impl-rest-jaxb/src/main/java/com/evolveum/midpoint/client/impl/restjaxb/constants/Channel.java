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
package com.evolveum.midpoint.client.impl.restjaxb.constants;

import org.apache.commons.lang3.StringUtils;

public enum Channel {

    LIVE_SYNC("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#liveSync"),
    RECONCILIATION("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#reconciliation"),
    RECOMPUTATION("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#recompute"),
    DISCOVERY("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#discovery"),
    OBJECT_IMPORT("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#objectImport"),
    REST("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#rest"),
    INIT("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#init"),
    USER("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#user"),
    SELF_REGISTRATION("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#selfRegistration"),
    RESET_PASSWORD("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#resetPassword"),
    IMPORT("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#import"),
    ASYNC_UPDATE("http://midpoint.evolveum.com/xml/ns/public/cpommon/channels-3#asyncUpdate"),
    REMEDIATION("http://midpoint.evolveum.com/xml/ns/public/common/channels-3#remediation");


    private final String uri;

    Channel(String uri) {
        this.uri = uri;
    }

    /**
     * Current channel URI.
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return Channel for the URI (matching current, not compatibility URIs); or null if it does not exist.
     */
    public static Channel findChannel(String uri) {
        if (StringUtils.isEmpty(uri)) {
            return null;
        }
        for (Channel channel : values()) {
            if (uri.equals(channel.getUri())) {
                return channel;
            }
        }
        return null;
    }

}
