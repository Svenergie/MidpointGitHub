package com.evolveum.midpoint.client.impl.prism;

import com.evolveum.midpoint.client.api.ObjectReference;
import com.evolveum.midpoint.client.api.Service;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ResourceType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.RoleType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ServiceType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class MidpointClient2 {


    private String userOid = null;
    private Service service;
    private PrismContext context;

    RoleType role;

    ServiceType serviceType;

    ResourceType resourceType;


    List<String> e=new ArrayList<>();


    UserType user = new UserType().name("00clientUser").givenName("given").familyName("family");


    @Test
    public void test() throws Exception {
       String c="add resource";
        String arg;
        service = createClient();
        context = PrismContext.get();
        while (c!=null) { // NOPMD
            switch (c) {
                case "add user":

                     //user = new UserType().name("01clientUser").givenName("test").familyName("family");
                     String userstr= "<user xmlns=\"http://midpoint.evolveum.com/xml/ns/public/common/common-3\" xmlns:c=\"http://midpoint.evolveum.com/xml/ns/public/common/common-3\" xmlns:icfs=\"http://midpoint.evolveum.com/xml/ns/public/connector/icf-1/resource-schema-3\" xmlns:org=\"http://midpoint.evolveum.com/xml/ns/public/common/org-3\" xmlns:q=\"http://prism.evolveum.com/xml/ns/public/query-3\" xmlns:ri=\"http://midpoint.evolveum.com/xml/ns/public/resource/instance-3\" xmlns:t=\"http://prism.evolveum.com/xml/ns/public/types-3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" oid=\"AA51231A-D38F-4F13-863D-91BAB145FB2C\" >\n"
                             + "    <name>bminus</name> \n"
                             +"</user>";
                     user =  (UserType) context.parserFor(userstr).language("xml").parseRealValue();
                    ObjectReference<UserType> ref = service.users().add(user).post();

                    AssertJUnit.assertNotNull(ref.getOid());

                    userOid = ref.getOid();
                    System.out.println("User : " + userOid);
                    c=null;
                    break;
                case "update user":


                    UserType ref2 = service.users().oid("5f5578b3-b225-4cf5-8fee-678efa181cc9").get();

                    System.out.println("User : " + ref2.getName());

                    //user.setName(PolyStringType.fromOrig("Hans"));
                    //service.users().add(user).post();
                    //System.out.println("User : " + user.getName());

                    c=null;

                    break;
                case "Delete User":
                    service.users().oid(userOid).delete();

                    try {
                         user = service.users().oid(userOid).get();
                        AssertJUnit.fail("Unexpected user found: " + user);
                    } catch (ObjectNotFoundException e) {
                        //expected
                    }
                    c=null;

                    break;
                case "add role":

                    role = new RoleType().name("Testrolle").displayName("TR");

                    service.roles().add(role).post();



                    c=null;

                    break;

                case "add service":

                    serviceType = new ServiceType().name("serviceTest").displayName("ST").description("Das ist eine Test");




                    c = null;

                    break;


                case "add resource":

                    resourceType = new ResourceType().name("serviceTest").description("Das ist eine Test");

                    service.resources().add(resourceType).post();

                    c = null;

                    break;
    }

        }
    //Todo Test Connection










    //Todo Add User
    //Todo update User
    //Todo Add Role
    //Todo Update Role
}
    private Service createClient() throws Exception {
        RestPrismServiceBuilder builder = RestPrismServiceBuilder.create();
        return builder.username("administrator")
                .password("5ecr3t")
                .baseUrl("http://localhost:8080/midpoint/ws/rest")
                .build();
    }
}
