package com.evolveum.midpoint.client.impl.restjaxb;

import com.evolveum.midpoint.client.api.SearchResult;
import com.evolveum.midpoint.client.api.Service;
import com.evolveum.midpoint.client.api.exception.AuthenticationException;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.client.api.exception.PolicyViolationException;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ExecuteCredentialResetRequestType;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.PolicyItemDefinitionType;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.PolicyItemsDefinitionType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.SecurityPolicyType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ValuePolicyType;
import com.evolveum.prism.xml.ns._public.types_3.ItemPathType;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javax.xml.namespace.QName;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertNotNull;

public class TestEducation extends AbstractTest {

    private static final String ENDPOINT_ADDRESS = "http://mpdev1.its.uwo.pri:8080/midpoint/ws/rest";



    private Service getService() throws IOException {
        return getService(ADMIN, ADMIN_PASS, ENDPOINT_ADDRESS);
    }

    @Test
    public void test010UserSearch() throws Exception {
        Service service = getService();

        ObjectReferenceType serviceRoleReferenceType = new ObjectReferenceType();
        serviceRoleReferenceType.setOid("westernu-0005-0000-0000-000000000015");
        serviceRoleReferenceType.setType(new QName("RoleType"));

        ItemPathType uwoContactPathType = new ItemPathType();
        uwoContactPathType.setValue("extension/uwoContact");

        ItemPathType rolePathType = new ItemPathType();
        rolePathType.setValue("roleMembershipRef");

        String jmorr32Oid ="1bae776f-4939-4071-92e2-8efd5bd57799";

        SearchResult<UserType> result =  service.users().search()
                .queryFor(UserType.class)
                //jmorr32's oid
                .item(uwoContactPathType).eq(jmorr32Oid)
                .and()
                .item(rolePathType).ref(serviceRoleReferenceType)
                .maxSize(1000)
                .get();

        // THEN
        assertEquals(result.size(), 0);
    }

    @Test
    public void test201modifyGenerate() throws Exception
    {
        Service service = getService();
        PolicyItemsDefinitionType policyItemsDefinition = service.users().oid("876").generate()
                .items()
                .item()
                .path("givenName")
                .execute()
                .build()
                .post();

        assertEquals(1, policyItemsDefinition.getPolicyItemDefinition().size());
        PolicyItemDefinitionType policyitemDefinition = policyItemsDefinition.getPolicyItemDefinition().iterator().next();
        assertNotNull(policyitemDefinition.getValue());

        UserType user = service.users().oid("876").get();
        assertNotNull(service.util().getOrig(user.getGivenName()));
    }

    @Test
    public void test202policyGenerate() throws Exception
    {
        Service service = getService();

        PolicyItemsDefinitionType policyItemsDefinition = service.rpc().generate()
                .items()
                .item()
                .policy("00000000-0000-0000-0000-000000000003")
                .build()
                .post();
        assertEquals(1, policyItemsDefinition.getPolicyItemDefinition().size());
        PolicyItemDefinitionType policyitemDefinition = policyItemsDefinition.getPolicyItemDefinition().iterator().next();
        assertNotNull(policyitemDefinition.getValue());

    }

    @Test
    public void test207userValidatePwdHistory() throws Exception {

        try {
            Service service = getService();
            service.users().oid("0b26b7cb-8086-4cf3-9a2e-692c5c2fbc38").validate()
                    .items()
                    .item()
                    .path("credentials/password/value")
                    .value("asd123")
                    .build()
                    .post();
            AssertJUnit.fail("Expected Policy violation exception, but didn't get one");
        } catch (PolicyViolationException e) {
            //this is expected
        }
    }

    @Test
    public void test208userValidatePwdHistory() throws Exception {

        Service service = getService();
        service.users().oid("c27e5ef1-4181-47ef-942c-00103caa4dd3").validate()
                .items()
                .item()
                .path("credentials/password/value")
                .value("asdASD123*")
                .build()
                .post();

    }

    @Test
    public void test210rpcGenerate() throws Exception {
        Service service = getService();
        service.rpc().generate()
                .items()
                .item()
                .policy("00000000-0000-0000-0000-p00000000001")
                .path("name")
                .build()
                .post();
    }

    @Test
    public void test213UserCredentialsReset() throws Exception{
        // SETUP
        Service service = getService();

        ExecuteCredentialResetRequestType executeCredentialResetRequest = new ExecuteCredentialResetRequestType();

        executeCredentialResetRequest.setResetMethod("passwordReset");
        executeCredentialResetRequest.setUserEntry("secret");

        // WHEN
        try{
            service.users().oid("1bae776f-4939-4071-92e2-8efd5bd57799").credential().executeResetPassword(executeCredentialResetRequest).post();
        }catch(ObjectNotFoundException e){
            fail("Cannot delete user, user not found");
        }
    }

    @Test
    public void test013SelfImpersonate() throws Exception {
        Service service = getService();

        UserType loggedInUser = null;

        try {
            loggedInUser = service.impersonate("44af349b-5a0c-4f3a-9fe9-2f64d9390ed3").self();

        } catch (AuthenticationException ex) {
            fail("should authenticate user successfully");
        }

        assertEquals(service.util().getOrig(loggedInUser.getName()), "impersonate");
    }

}
