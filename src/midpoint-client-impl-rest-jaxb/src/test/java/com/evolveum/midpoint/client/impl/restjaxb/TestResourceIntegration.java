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
package com.evolveum.midpoint.client.impl.restjaxb;

import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.Service;

import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultStatusType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;

import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskExecutionStateType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.TaskType;

import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Test operations on resource, to run, import the Resource HR from Hogwarts demo
 * and change path to file, so the connection works properly
 */
public class TestResourceIntegration extends AbstractTest {

    private static final String ENDPOINT_ADDRESS = "http://localhost:8080/midpoint/ws/rest";
    private static final String RESOURCE_OID = "ef2bc95b-76e0-48e2-86d6-3d4f02d3fafe"; // Resource HR oid from Hogwarts demo
    private static final String ACCOUNT_OBJECT_CLASS = "AccountObjectClass";

    Service service;

    @BeforeClass
    public void initService() throws Exception {
        service = getService();
    }

    @Test
    public void test100testResource() throws Exception {
        OperationResultType status = service.resources().oid(RESOURCE_OID).test().post();
        assertEquals("Expected that resource connection is successfull", status.getStatus(), OperationResultStatusType.SUCCESS);
        //TODO check subresults
    }

    String taskOid = null;
    @Test
    public void test110importFromResource() throws Exception {
        ObjectReference<TaskType> importTask = service.resources()
                .oid(RESOURCE_OID)
                .importFromResource()
                .objectClass(ACCOUNT_OBJECT_CLASS)
                .post();

        taskOid = importTask.getOid();
        TaskType task = service.tasks().oid(taskOid).get();
        assertEquals("Expected that the task is running", task.getExecutionState(), TaskExecutionStateType.RUNNING);
    }

    //cleanup environment
   @Test
    public void test120deleteTask() throws Exception {
        service.tasks().oid(taskOid).delete();

        try {
            service.tasks().oid(taskOid).get();
            fail("Unexpected task exists");
        } catch (ObjectNotFoundException e) {
            // expected
        }
    }

    private RestJaxbService getService() throws IOException {
        return (RestJaxbService) getService(ADMIN, ADMIN_PASS, ENDPOINT_ADDRESS);
    }
}
