package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import java.util.HashMap;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

//import com.att.echo.EchoGroovy;

public class EchoTest extends CamelTestSupport {

    /*@Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("direct:a")
                .bean(EchoGroovy.class, "process")
                .to("mock:a");
            }
        };
    }*/
@Disabled
    @Test
    public void echoGroovyProcessor() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:a");

        String expectedValue = "Echo... Successfully Tested Echo Service Version : 100.0.0";
        mock.expectedMessageCount(1);
        mock.allMessages().header("responseMessage").isEqualTo(expectedValue);

        HashMap<String, Object> headers = new HashMap<String, Object>();
        headers.put("serviceName", "Echo");
        headers.put("serviceVersion", "100.0.0");
        template.sendBodyAndHeaders("direct:a", "Test Body", headers);

        assertMockEndpointsSatisfied();
    }
}
