package com.att.sales.framework.filters;


import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.controller.NexxusController;
 
@Component
public class JerseyConfig extends ResourceConfig 
{
    public JerseyConfig() 
    {
        register(NexxusController.class);
    }
}