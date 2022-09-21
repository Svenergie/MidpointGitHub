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

import com.evolveum.midpoint.client.api.ServiceUtil;
import com.evolveum.midpoint.xml.ns._public.common.common_3.AssignmentType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectReferenceType;
import com.evolveum.prism.xml.ns._public.types_3.ItemDeltaType;
import com.evolveum.prism.xml.ns._public.types_3.ModificationTypeType;
import com.evolveum.prism.xml.ns._public.types_3.ObjectDeltaType;
import com.evolveum.prism.xml.ns._public.types_3.ProtectedStringType;

import org.apache.cxf.helpers.IOUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

/**
 * @author mederly
 *
 */
public class TestServiceUtil extends AbstractTest {
	
	private static final String DATA_DIR = "src/test/resources/other";

	public TestServiceUtil() throws JAXBException, IOException {

	}

	@Test
	public void testProtectedStringWithClearValue() throws Exception {
		testProtectedString("protected-string-with-clear-value.xml", "nbusr123");
	}

	@Test
	public void testProtectedStringSimple() throws Exception {
		testProtectedString("protected-string-simple.xml", "init1234");
	}

	@Test
	public void testProtectedStringEncrypted() throws Exception {
		testProtectedString("protected-string-encrypted.xml", null);
	}

	@Test
	public void testProtectedStringEmpty() throws Exception {
		testProtectedString("protected-string-empty.xml", null);
	}

	@Test
	public void testObjectDeltaString() throws Exception {
		File file = new File(DATA_DIR, "object-delta.xml");
		String deltaAsString = IOUtils.toString(new FileInputStream(file));
		
		
		ObjectDeltaType objectDetla = getServiceUtils().parse(ObjectDeltaType.class, deltaAsString);
		List<ItemDeltaType> itemDeltaType = objectDetla.getItemDelta();
		assertTrue(itemDeltaType.size() != 0, "Unexpected item detla size: " + itemDeltaType.size());
		
		for (ItemDeltaType itemDelta : itemDeltaType) {
			List<Object> values = itemDelta.getValue();
			assertTrue(values.size() != 0, "Unexpected values in itemDelta");
			for (Object value : values) {
				if (value instanceof JAXBElement) {
					fail("Unexpected jaxb element value among delta values.");
				}
			}
		}
	}
	
	@Test
	public void testObjectDeltaAssignment() throws Exception {
		File file = new File(DATA_DIR, "object-delta-assignment.xml");
		String deltaAsString = IOUtils.toString(new FileInputStream(file));
		
		
		ObjectDeltaType objectDetla = getServiceUtils().parse(ObjectDeltaType.class, deltaAsString);
		List<ItemDeltaType> itemDeltaTypes = objectDetla.getItemDelta();
		assertTrue(itemDeltaTypes.size() == 1, "Unexpected item detla size: " + itemDeltaTypes.size());
		
		ItemDeltaType itemDeltaType = itemDeltaTypes.iterator().next();
		assertEquals(itemDeltaType.getModificationType(), ModificationTypeType.REPLACE);
		List<Object> values = itemDeltaType.getValue();
		assertEquals(values.size(), 1, "Unexpected item detla size: " + itemDeltaTypes.size());
		
		Object o = values.iterator().next();
		
		assertEquals(o.getClass(), AssignmentType.class);
		
		AssignmentType assignment = (AssignmentType) o;
		assertEquals(assignment.getTargetRef().getOid(), "123123123123");
		assertEquals(assignment.getTargetRef().getType(), Types.ROLES.getTypeQName());
	}
	
	@Test
	public void testObjectDeltaPassword() throws Exception {
		File file = new File(DATA_DIR, "object-delta-password.xml");
		String deltaAsString = IOUtils.toString(new FileInputStream(file));
		
		
		ObjectDeltaType objectDetla = getServiceUtils().parse(ObjectDeltaType.class, deltaAsString);
		List<ItemDeltaType> itemDeltaTypes = objectDetla.getItemDelta();
		assertTrue(itemDeltaTypes.size() == 1, "Unexpected item detla size: " + itemDeltaTypes.size());
		
		ItemDeltaType itemDeltaType = itemDeltaTypes.iterator().next();
		assertEquals(itemDeltaType.getModificationType(), ModificationTypeType.REPLACE);
		List<Object> values = itemDeltaType.getValue();
		assertEquals(values.size(), 1, "Unexpected item detla size: " + itemDeltaTypes.size());
		
		Object o = values.iterator().next();
		
		assertEquals(o.getClass(), ProtectedStringType.class);
		
		ProtectedStringType password = (ProtectedStringType) o;
		assertEquals(getServiceUtils().getClearValue(password), "asd123");
	}
	
	@Test
	public void testObjectDeltaPolicySituation() throws Exception {
		File file = new File(DATA_DIR, "object-delta-policySituation.xml");
		String deltaAsString = IOUtils.toString(new FileInputStream(file));
		
		
		ObjectDeltaType objectDetla = getServiceUtils().parse(ObjectDeltaType.class, deltaAsString);
		List<ItemDeltaType> itemDeltaTypes = objectDetla.getItemDelta();
		assertTrue(itemDeltaTypes.size() == 1, "Unexpected item detla size: " + itemDeltaTypes.size());
		
		ItemDeltaType itemDeltaType = itemDeltaTypes.iterator().next();
		assertEquals(itemDeltaType.getModificationType(), ModificationTypeType.REPLACE);
		List<Object> values = itemDeltaType.getValue();
		assertEquals(values.size(), 2, "Unexpected item detla size: " + itemDeltaTypes.size());
		
	}
	
	@Test
	public void testObjectDeltaParentOrgRef() throws Exception {
		File file = new File(DATA_DIR, "object-delta-parentOrgRef.xml");
		String deltaAsString = IOUtils.toString(new FileInputStream(file));
		
		
		ObjectDeltaType objectDetla = getServiceUtils().parse(ObjectDeltaType.class, deltaAsString);
		List<ItemDeltaType> itemDeltaTypes = objectDetla.getItemDelta();
		assertTrue(itemDeltaTypes.size() == 1, "Unexpected item detla size: " + itemDeltaTypes.size());
		
		ItemDeltaType itemDeltaType = itemDeltaTypes.iterator().next();
		assertEquals(itemDeltaType.getModificationType(), ModificationTypeType.REPLACE);
		List<Object> values = itemDeltaType.getValue();
		assertEquals(values.size(), 1, "Unexpected item detla size: " + itemDeltaTypes.size());
		
		Object o = values.iterator().next();
		
		assertEquals(o.getClass(), ObjectReferenceType.class);
		
		ObjectReferenceType parentOrgRef = (ObjectReferenceType) o;
		assertEquals(parentOrgRef.getOid(), "00000000-8888-6666-0000-100000000003");
		assertEquals(parentOrgRef.getType(), Types.ORGS.getTypeQName());
	}
	
	@Test
	public void testAssignment() throws Exception {
		File file = new File(DATA_DIR, "assignment.xml");
		String deltaAsString = IOUtils.toString(new FileInputStream(file));
		
		
		AssignmentType assignment = getServiceUtils().parse(AssignmentType.class, deltaAsString);
		System.out.println("Assignemnt: " + assignment);
		ObjectReferenceType ort = new ObjectReferenceType();
		ort.setOid("123123");
		ort.setType(Types.ROLES.getTypeQName());
		assertEquals(assignment.getTargetRef().getOid(), ort.getOid());
		assertEquals(assignment.getTargetRef().getType(), Types.ROLES.getTypeQName());
	}
	
	private void testProtectedString(String filename, String expectedValue) throws Exception {
		//noinspection unchecked
		ProtectedStringType ps = ((JAXBElement<ProtectedStringType>) getUnmarshaller().unmarshal(new File(DATA_DIR, filename))).getValue();
		String clearValue = getServiceUtils().getClearValue(ps);
		AssertJUnit.assertEquals("Unexpected clear value", expectedValue, clearValue);
	}

}
