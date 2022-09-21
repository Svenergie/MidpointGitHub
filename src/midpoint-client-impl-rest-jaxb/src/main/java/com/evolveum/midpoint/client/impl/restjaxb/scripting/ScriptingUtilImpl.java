package com.evolveum.midpoint.client.impl.restjaxb.scripting;

import com.evolveum.midpoint.client.api.ServiceUtil;
import com.evolveum.midpoint.client.api.scripting.ObjectProcessingOutput;
import com.evolveum.midpoint.client.api.scripting.OperationSpecificData;
import com.evolveum.midpoint.client.api.scripting.ScriptingUtil;
import com.evolveum.midpoint.client.api.scripting.ValueGenerationData;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ExecuteScriptResponseType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_3.PipelineItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author mederly
 */
public class ScriptingUtilImpl implements ScriptingUtil {

	private final ServiceUtil util;

	public ScriptingUtilImpl(ServiceUtil util) {
		this.util = util;
	}

	@Override
	public <X extends OperationSpecificData> List<ObjectProcessingOutput<X>> extractObjectProcessingOutput(
			ExecuteScriptResponseType response, Function<Object, X> operationDataExtractor) {
		List<PipelineItemType> outputItems = response.getOutput().getDataOutput().getItem();
		List<ObjectProcessingOutput<X>> extractedResults = new ArrayList<>(outputItems.size());
		for (PipelineItemType outputItem : outputItems) {
			ObjectProcessingOutput<X> extractedResult = new ObjectProcessingOutput<>();
			Object value = outputItem.getValue();
			if (value instanceof ObjectType) {
				ObjectType object = (ObjectType) value;
				extractedResult.setOid(object.getOid());
				extractedResult.setName(util.getOrig(object.getName()));
				extractedResult.setObject(object);
				if (operationDataExtractor != null) {
					extractedResult.setData(operationDataExtractor.apply(value));
				}
			} else if (value instanceof ObjectReferenceType) {
				extractedResult.setOid(((ObjectReferenceType) value).getOid());
				extractedResult.setName(util.getOrig(((ObjectReferenceType) value).getTargetName()));
			} else if (value instanceof com.evolveum.prism.xml.ns._public.types_3.ObjectReferenceType) {
				extractedResult.setOid(((com.evolveum.prism.xml.ns._public.types_3.ObjectReferenceType) value).getOid());
				extractedResult.setName(util.getOrig(((com.evolveum.prism.xml.ns._public.types_3.ObjectReferenceType) value).getTargetName()));
			} else {
				throw new IllegalStateException("Unexpected item value: " + value);
			}
			if (outputItem.getResult() != null) {
				extractedResult.setStatus(outputItem.getResult().getStatus());
				extractedResult.setMessage(outputItem.getResult().getMessage());
				extractedResult.setResult(outputItem.getResult());
			}
			extractedResults.add(extractedResult);
		}
		return extractedResults;
	}

	private class PasswordGenerationDataExtractor implements Function<Object, ValueGenerationData<String>> {
		@Override
		public ValueGenerationData<String> apply(Object object) {
			if (object instanceof UserType) {
				UserType user = (UserType) object;
				if (user.getCredentials() != null && user.getCredentials().getPassword() != null
						&& user.getCredentials().getPassword().getValue() != null) {
					ValueGenerationData<String> rv = new ValueGenerationData<>();
					rv.setValue(util.getClearValue(user.getCredentials().getPassword().getValue()));
					return rv;
				}
			}
			return null;
		}
	}

	@Override
	public List<ObjectProcessingOutput<ValueGenerationData<String>>> extractPasswordGenerationResults(
			ExecuteScriptResponseType response) {
		return extractObjectProcessingOutput(response, new PasswordGenerationDataExtractor());
	}
}
