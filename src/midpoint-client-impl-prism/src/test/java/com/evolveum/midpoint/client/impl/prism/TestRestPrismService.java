/*
 * Copyright (c) 2021 Evolveum
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
package com.evolveum.midpoint.client.impl.prism;

import com.evolveum.midpoint.xml.ns._public.common.common_3.*;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.Service;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.schema.result.OperationResult;

public class TestRestPrismService {


    private Service service;

    @BeforeClass
    public void init() throws Exception {
        service = createClient();
    }

    @AfterClass
    public void shutdown() {
        service.close();
    }

    @Test
    public void test001getUser() throws Exception {

        UserType user = service.users().oid(SystemObjectsType.USER_ADMINISTRATOR.value()).get();

        System.out.println("User : " + user.getName());

        AssertJUnit.assertEquals("administrator", user.getName().getOrig());

    }

    @Test
    public void test002getUserNotFound() throws Exception {
        try {
            service.users().oid("not-exists").get();
            AssertJUnit.fail("ObejctNotFoundException should be thrown.");
        } catch (ObjectNotFoundException e) {
            //this is expected
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void test003getSystemConfig() throws Exception {
        SystemConfigurationType systemConfigurationType = service.systemConfigurations().oid(SystemObjectsType.SYSTEM_CONFIGURATION.value()).get();
        AssertJUnit.assertNotNull(systemConfigurationType);
    }

    @Test
    public void test004getTask() throws Exception {
        TaskType taskType = service.tasks().oid(SystemObjectsType.TASK_CLEANUP.value()).get();
        AssertJUnit.assertNotNull(taskType);
    }

    private String userOid = null;
    @Test
    public void test010addUser() throws Exception {
        UserType user = new UserType().name("00clientUser").givenName("given").familyName("family");
        ObjectReference<UserType> ref = service.users().add(user).post();

        AssertJUnit.assertNotNull(ref.getOid());
        userOid = ref.getOid();
    }

    @Test
    public void test019deleteUser() throws Exception {
        service.users().oid(userOid).delete();

        try {
            UserType user = service.users().oid(userOid).get();
            AssertJUnit.fail("Unexpected user found: " + user);
        } catch (ObjectNotFoundException e) {
            //expected
        }

    }

    @Test
    public void test100testResource() throws Exception {

        OperationResultType resultType = service.resources().oid("ac5199fb-c5bd-46c3-a549-d82e8fd30dc2").test().post();
        OperationResult result = OperationResult.createOperationResult(resultType);

        AssertJUnit.assertTrue(result.isSuccess());
    }

    @Test
    public void test120testResourceDown() throws Exception {

        OperationResultType resultType = service.resources().oid("2a7c7130-7a34-11e4-bdf6-001e8c717e5b").test().post();
        OperationResult result = OperationResult.createOperationResult(resultType);

        AssertJUnit.assertTrue(result.isFatalError());
    }

    private Service createClient() throws Exception {
        RestPrismServiceBuilder builder = RestPrismServiceBuilder.create();
        return builder.username("administrator")
                .password("5ecr3t")
                .baseUrl("http://localhost:8080/midpoint/ws/rest")
                .build();
    }


}
