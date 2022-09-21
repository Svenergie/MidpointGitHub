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
package com.evolveum.midpoint.client.impl.restjaxb;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.helpers.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.evolveum.midpoint.client.api.ServiceUtil;
import com.evolveum.midpoint.client.api.exception.SchemaException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.AssignmentType;
import com.evolveum.prism.xml.ns._public.types_3.ItemDeltaType;
import com.evolveum.prism.xml.ns._public.types_3.ItemPathType;
import com.evolveum.prism.xml.ns._public.types_3.ObjectDeltaType;
import com.evolveum.prism.xml.ns._public.types_3.PolyStringType;
import com.evolveum.prism.xml.ns._public.types_3.ProtectedStringType;

/**
 * @author semancik
 *
 */
public class RestJaxbServiceUtil implements ServiceUtil {

	private static final String F_CLEAR_VALUE = "clearValue";

	private static DatatypeFactory df;

	private JAXBContext jaxbContext;

	public RestJaxbServiceUtil(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}

	static {
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException("Exception while obtaining Datatype Factory instance", dce);
        }
    }

	@Override
	public PolyStringType createPoly(String orig) {
		PolyStringType poly = new PolyStringType();
		poly.getContent().add(orig);
		return poly;
	}

	@Override
	public String getOrig(PolyStringType poly) {
		if (poly == null) {
			return null;
		}
		for (Object content: poly.getContent()) {
			if (content instanceof String) {
				return (String)content;
			}
			// TODO: DOM elements and JAXB elements
		}
		return null;
	}

	@Override
	public ItemPathType createItemPathType(QName... qname) {
        String itemPathValue = Arrays.stream(qname).map(item -> item.getLocalPart()).collect(Collectors.joining("/"));
	    ItemPathType itemPathType = new ItemPathType();
	    itemPathType.setValue(itemPathValue);
	    return itemPathType;
	}

	@Override
	public XMLGregorianCalendar asXMLGregorianCalendar(Date date) {
		if (date == null) {
			return null;
		} else {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(date.getTime());
			return df.newXMLGregorianCalendar(gc);
		}
	}

	@Override
	public String getClearValue(ProtectedStringType protectedString) {
		if (protectedString == null) {
			return null;
		}
		boolean hasSubelements = false;
		StringBuilder textContentBuilder = new StringBuilder();
		for (Object o : protectedString.getContent()) {
			if (o instanceof JAXBElement) {
				JAXBElement element = (JAXBElement) o;
				if (F_CLEAR_VALUE.equals(element.getName().getLocalPart())) {
					return (String) element.getValue();
				}
				hasSubelements = true;
			} else if (o instanceof Element) {
				Element element = (Element) o;
				if (F_CLEAR_VALUE.equals(element.getLocalName())) {
					return element.getTextContent();
				}
				hasSubelements = true;
			} else if (o instanceof String) {
				textContentBuilder.append((String) o);
			}
		}
		if (!hasSubelements) {
			String textContent = textContentBuilder.toString();
			// This is probably not quite correct because it treats alternative representation of blank password as no password.
			// But there's no way how to distinguish these cases.
			if (StringUtils.isNotBlank(textContent)) {
				return textContent;
			}
		}
		return null;
	}

	@Override
	public <T> T parse(Class<T> type, String xml) throws SchemaException {

		if (AssignmentType.class.equals(type)) {
			try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			JAXBElement<AssignmentType> objectDeltaTypeJaxb = (JAXBElement<AssignmentType>) unmarshaller.unmarshal(reader);
			return (T) objectDeltaTypeJaxb.getValue();
		} catch (JAXBException e) {
			throw new SchemaException("Cannot parse: " + xml + ". Reason: " + e.getMessage(), e);
		}
		}


		if (!ObjectDeltaType.class.isAssignableFrom(type)) {
			throw new UnsupportedOperationException("Unsupported type to parse: " + type);
		}

		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			JAXBElement<ObjectDeltaType> objectDeltaTypeJaxb = (JAXBElement<ObjectDeltaType>) unmarshaller.unmarshal(reader);

			ObjectDeltaType objectDeltaType = objectDeltaTypeJaxb.getValue();
			List<ItemDeltaType> parsedItemDeltas = new ArrayList<>();
			for (ItemDeltaType itemDelta : objectDeltaType.getItemDelta()) {
				ItemDeltaType parsedItemDelta = new ItemDeltaType();
				parsedItemDelta.setModificationType(itemDelta.getModificationType());
				parsedItemDelta.setPath(itemDelta.getPath());

				parsedItemDelta.getEstimatedOldValue().addAll(parseValue(objectDeltaType.getObjectType(), itemDelta.getPath(), itemDelta.getEstimatedOldValue(), unmarshaller));

				parsedItemDelta.getValue().addAll(parseValue(objectDeltaType.getObjectType(), itemDelta.getPath(), itemDelta.getValue(), unmarshaller));
				parsedItemDeltas.add(parsedItemDelta);
			}

			objectDeltaType.getItemDelta().clear();
			objectDeltaType.getItemDelta().addAll(parsedItemDeltas);
			return (T) objectDeltaType;
		} catch (JAXBException e) {
			throw new SchemaException("Cannot parse: " + xml + ". Reason: " + e.getMessage(), e);
		}


	}

	private List<Object> parseValue(QName objectType, ItemPathType itemPath, List<Object> values, Unmarshaller unmarshaller) throws JAXBException {

		List<Object> parsedValues = new ArrayList<>();
		for (Object o : values) {
			if (Node.class.isAssignableFrom(o.getClass())) {

				Node node = (Node) o;
				List<QName> namedSegments = getPathNamedSegments(itemPath.getValue());
				Types type = Types.findType(objectType.getLocalPart());
				Class<?> jaxbType = findType(type.getClazz(), namedSegments);

				JAXBElement jaxb = null;
				if (jaxbType == null) {
					 jaxb = (JAXBElement) unmarshaller.unmarshal(node, String.class);
				} else {
					jaxb = unmarshaller.unmarshal(node, jaxbType);
				}

				Object value = jaxb.getValue();
				parsedValues.add(value);

			} else {
				parsedValues.add(o);
			}

		}

		return parsedValues;
	}

	private Class<?> findType(Class<?> superClass, List<QName> pathSegment) {
		for (int i = 0; i < pathSegment.size(); i++) {

			String localName = pathSegment.get(i).getLocalPart();
			Field field = findFiled(superClass, localName);

			if (field == null) {
				return null;
			}

			if (field.getName().equals(pathSegment.get(i).getLocalPart())) {
				List<QName> pathSegmentRemaining = pathSegment.subList(i + 1, pathSegment.size());

				if (pathSegmentRemaining.size() > 0) {
					return findType(getTypeClass(field), pathSegmentRemaining);
				}

				return getTypeClass(field);

			}
		}

		return null;

	}

	private Class<?> getTypeClass(Field field) {
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			Type[] actualType = ((ParameterizedType) type).getActualTypeArguments();
			return (Class<?>) actualType[0];
		}

		return field.getType();
	}

	private Field findFiled(Class<?> clazz, String localName) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(localName)) {
				return field;
			}
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return null;
		}

		return findFiled(superClass, localName);

	}

	private List<QName> getPathNamedSegments(String path) {

		String[] segments = path.split("/");
		List<QName> namedSegments = new ArrayList<>();
		int j=0;
		for (int i=0; i < segments.length; i++) {
			if (segments[i].contains("[")) {
				j--;
				continue;
			}

			String[] parsed = segments[i].split(":");
			if (parsed.length == 1) {
				String s = parsed[0];
				namedSegments.add(new QName(s));
			} else {
				namedSegments.add(new QName(parsed[1]));
			}
			j++;
		}
		return namedSegments;
	}

	private boolean hasChildren(Node node) {
		boolean hasChildren = false;
		for (int i = 0; i < node.getChildNodes().getLength() -1; i++ ) {
			Node n = node.getChildNodes().item(i);
			if (n.getNodeType() != Node.ATTRIBUTE_NODE && n.getNodeType() != Node.TEXT_NODE) {
				hasChildren = true;
			}
		}

		return hasChildren;
	}

	private QName getQName(ItemPathType itemPath) {
		String pathAsString = itemPath.getValue();
		String[] segments = pathAsString.split("/");
		String lastSegment = segments[segments.length-1];
		if (lastSegment.contains("[")) {
			lastSegment = segments[segments.length -2];
		}

		String[] qnameSplitted = lastSegment.split(":");
		String localPart = "";
		if (qnameSplitted.length == 2) {
			localPart = qnameSplitted[1];
		} else {
			localPart = qnameSplitted[0];
		}

		return DOMUtils.convertStringToQName(localPart);
	}

	private void cloneAttributes(Node from, Element to) {
		for (int i=0; i < from.getAttributes().getLength() -1; i++) {
			to.setAttributeNode((Attr) from.getAttributes().item(i).cloneNode(true));
		}
	}

	private void prettyPrint(Node node) {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		//initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(node);
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);
		} catch (TransformerException | TransformerFactoryConfigurationError e) {
			//TODO:
		}
	}
}
