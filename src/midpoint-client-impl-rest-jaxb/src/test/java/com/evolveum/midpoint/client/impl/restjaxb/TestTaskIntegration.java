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

import java.io.IOException;
import java.util.Collections;

import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.Service;
import com.evolveum.midpoint.client.impl.restjaxb.constants.Channel;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;

import static org.testng.AssertJUnit.*;

/**
 * Won't work with empty deplyment. At least, some users has to exists
 * so if the task starts, it will take a while to finish
 */
public class TestTaskIntegration extends AbstractTest {

    private static final String ENDPOINT_ADDRESS = "http://localhost:8080/midpoint/ws/rest";

    Service service;

    @BeforeClass
    public void initService() throws Exception {
        service = getService();
    }

    String recomputeTaskOid;

    @Test
    public void test100addRecomputeTask() throws Exception {
        TaskType recomputeTask = createRecomputeTask(TaskExecutionStateType.RUNNABLE, TaskRecurrenceType.SINGLE);

        ObjectReference<TaskType> taskType = service.tasks().add(recomputeTask).post();
        recomputeTaskOid = taskType.getOid();

        TaskType taskAfter = service.tasks().oid(recomputeTaskOid).get(null, Collections.singletonList("nodeAsObserved"), null);

        assertNotNull("Node as observed should be set (task is running)", taskAfter.getNodeAsObserved());
        assertEquals("Unexpected execution status", TaskExecutionStateType.RUNNING, taskAfter.getExecutionState());
    }

    @Test
    public void test110suspendRecomputeTask() throws Exception {
        service.tasks().oid(recomputeTaskOid).suspend().post();

        // wait a bit so the task manager can do its job
        Thread.sleep(5000);

        TaskType taskAfter = service.tasks().oid(recomputeTaskOid).get();
        assertEquals("Unexpected execution status", TaskExecutionStateType.SUSPENDED, taskAfter.getExecutionState());
    }

    @Test
    public void test120resumeRecomputeTask() throws Exception {
        service.tasks().oid(recomputeTaskOid).resume().post();

        // wait a bit so the task manager can do its job
        Thread.sleep(5000);

        TaskType taskAfter = service.tasks().oid(recomputeTaskOid).get(null, Collections.singletonList("nodeAsObserved"), null);
        assertEquals("Unexpected execution status", TaskExecutionStateType.RUNNING, taskAfter.getExecutionState());
        assertNotNull("Node as observed should be set (task is running)", taskAfter.getNodeAsObserved());
    }

    @Test
    public void test130deleteRecomputeTask() throws Exception {
        service.tasks().oid(recomputeTaskOid).delete();

        // wait a bit so the task manager can do its job
        Thread.sleep(5000);

        try {
            service.tasks().oid(recomputeTaskOid).get(null, Collections.singletonList("nodeAsObserved"), null);
            fail("Unexpected task found");
        } catch (ObjectNotFoundException e) {
            //expected
        }
    }

    private TaskType createRecomputeTask(TaskExecutionStateType status, TaskRecurrenceType recurrenceType) {
        TaskType taskType = new TaskType();
        taskType.setName(service.util().createPoly("Recompute task (java client)"));
        taskType.setChannel(Channel.RECOMPUTATION.getUri());
        taskType.setOwnerRef(createObjectRef(SystemObjectsType.USER_ADMINISTRATOR.value(), Types.USERS));
        AssignmentType recomputeArchetype = new AssignmentType();
        recomputeArchetype.setTargetRef(createObjectRef(SystemObjectsType.ARCHETYPE_RECOMPUTATION_TASK.value(), Types.ARCHETYPES));
        taskType.getAssignment().add(recomputeArchetype);
        taskType.setExecutionState(status);
        taskType.setRecurrence(recurrenceType);
        return taskType;
    }

    private ObjectReferenceType createObjectRef(String oid, Types type) {
        ObjectReferenceType ref = new ObjectReferenceType();
        ref.setType(type.getTypeQName());
        ref.setOid(oid);
        return ref;
    }

    private RestJaxbService getService() throws IOException {
        return (RestJaxbService) getService(ADMIN, ADMIN_PASS, ENDPOINT_ADDRESS);
    }
}
