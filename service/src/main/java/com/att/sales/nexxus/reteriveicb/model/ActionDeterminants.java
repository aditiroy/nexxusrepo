package com.att.sales.nexxus.reteriveicb.model;

/*
 * @Author: Akash Arya
 * 
 * 
 */
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;



/**
 * The Class ActionDeterminants.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ActionDeterminants {
	
	/** The activity. */
	private String activity;

    /** The component. */
    private List<String> component;

    /**
     * Sets the activity.
     *
     * @param activity the new activity
     */
    public void setActivity(String activity){
        this.activity = activity;
    }
    
    /**
     * Gets the activity.
     *
     * @return the activity
     */
    public String getActivity(){
        return this.activity;
    }
    
    /**
     * Sets the component.
     *
     * @param component the new component
     */
    public void setComponent(List<String> component){
        this.component = component;
    }
    
    /**
     * Gets the component.
     *
     * @return the component
     */
    public List<String> getComponent(){
        return this.component;
    }
}
