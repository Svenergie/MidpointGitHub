package com.evolveum.midpoint.client.impl.restjaxb;

import com.evolveum.midpoint.client.api.Service;
import com.evolveum.midpoint.client.api.ServiceUtil;
import com.evolveum.midpoint.client.impl.restjaxb.service.AuthenticationProvider;
import com.evolveum.midpoint.client.impl.restjaxb.service.MidpointMockRestService;
import com.evolveum.midpoint.xml.ns._public.model.scripting_3.ExecuteScriptType;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.transport.local.LocalConduit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AbstractTest {

    public static final String ADMIN = "administrator";
    public static final String ADMIN_PASS = "5ecr3t";

    private RestJaxbServiceUtil util;
    private Unmarshaller unmarshaller;

    public Service getService(String username, String password, String endpoint) throws IOException {
        RestJaxbServiceBuilder serviceBuilder = new RestJaxbServiceBuilder().password(password);
        Service service = getService(serviceBuilder, AuthenticationType.BASIC, username, endpoint);
        return service;

    }

    public Service getService(String username, String endpoint) throws IOException {
        RestJaxbServiceBuilder serviceBuilder = new RestJaxbServiceBuilder();
        Service service = getService(serviceBuilder, null, username, endpoint);
        return service;

    }


    public Service getService(String username, String endpoint, List<SecurityQuestionAnswer> answer) throws IOException {
        RestJaxbServiceBuilder serviceBuilder = new RestJaxbServiceBuilder().authenticationChallenge(answer);
        return getService(serviceBuilder, AuthenticationType.SECQ, username, endpoint);

    }

    private Service getService(RestJaxbServiceBuilder serviceBuilder, AuthenticationType authnType, String username, String endpoint) throws IOException {
        RestJaxbService service = serviceBuilder.authentication(authnType).username(username).url(endpoint).build();
        service.getClientConfiguration().getRequestContext().put(LocalConduit.DIRECT_DISPATCH, Boolean.TRUE);
        return service;
    }


    public Server startServer(String endpoint) throws IOException {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(MidpointMockRestService.class);

        JAXBContext jaxbCtx = createJaxbContext();
        sf.setProviders(Arrays.asList(new JaxbXmlProvider<>(jaxbCtx), new AuthenticationProvider()));

        sf.setResourceProvider(MidpointMockRestService.class,
                new SingletonResourceProvider(new MidpointMockRestService(jaxbCtx), true));
        sf.setAddress(endpoint);


        Server server = sf.create();
        return server;
    }

    public <T> T unmarshallFromFile(Class<T> type, File file) throws IOException, JAXBException {
        return ((JAXBElement<T>) getUnmarshaller().unmarshal(file)).getValue();
    }

    public Unmarshaller getUnmarshaller() throws IOException, JAXBException {
        if (unmarshaller == null) {
            unmarshaller = createJaxbContext().createUnmarshaller();
        }
        return unmarshaller;
    }

    public RestJaxbServiceUtil getServiceUtils() throws IOException {
        if (util == null) {
            util = new RestJaxbServiceUtil(createJaxbContext());
        }
        return util;
    }

    private JAXBContext createJaxbContext() throws IOException {
        try {
            return JAXBContext.newInstance("com.evolveum.midpoint.xml.ns._public.common.api_types_3:"
                    + "com.evolveum.midpoint.xml.ns._public.common.audit_3:"
                    + "com.evolveum.midpoint.xml.ns._public.common.common_3:"
                    + "com.evolveum.midpoint.xml.ns._public.connector.icf_1.connector_extension_3:"
                    + "com.evolveum.midpoint.xml.ns._public.connector.icf_1.connector_schema_3:"
                    + "com.evolveum.midpoint.xml.ns._public.connector.icf_1.resource_schema_3:"
                    + "com.evolveum.midpoint.xml.ns._public.model.extension_3:"
                    + "com.evolveum.midpoint.xml.ns._public.model.scripting_3:"
                    + "com.evolveum.midpoint.xml.ns._public.model.scripting.extension_3:"
                    + "com.evolveum.midpoint.xml.ns._public.report.extension_3:"
                    + "com.evolveum.midpoint.xml.ns._public.resource.capabilities_3:"
                    + "com.evolveum.midpoint.xml.ns._public.task.jdbc_ping.handler_3:"
                    + "com.evolveum.midpoint.xml.ns._public.task.noop.handler_3:"
                    + "com.evolveum.prism.xml.ns._public.annotation_3:"
                    + "com.evolveum.prism.xml.ns._public.query_3:"
                    + "com.evolveum.prism.xml.ns._public.types_3");
        } catch (JAXBException e) {
            throw new IOException(e);
        }

    }
}
