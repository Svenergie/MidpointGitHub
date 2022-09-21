package com.evolveum.midpoint.client.api;

import com.evolveum.midpoint.xml.ns._public.common.common_3.ResourceType;

/**
 * @author Viliam Repan (lazyman)
 */
public interface ResourceCollectionService extends ObjectCollectionService<ResourceType> {

    @Override
    ResourceService oid(String oid);
}
