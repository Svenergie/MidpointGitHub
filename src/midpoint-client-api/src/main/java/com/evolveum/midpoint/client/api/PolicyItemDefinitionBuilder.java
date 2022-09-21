package com.evolveum.midpoint.client.api;

public interface PolicyItemDefinitionBuilder {

	PolicyItemsDefinitionBuilder value(Object value);
	PolicyItemsDefinitionBuilder execute();
	
	PolicyItemDefinitionEntryOrExitBuilder item();
	
	ValidateGenerateRpcService build();
}
