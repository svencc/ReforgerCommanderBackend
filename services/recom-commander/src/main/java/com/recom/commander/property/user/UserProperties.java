package com.recom.commander.property.user;

import com.recom.dynamicproperties.DynamicUserProperties;
import lombok.Data;

@Data
public class UserProperties extends DynamicUserProperties {

    public UserProperties() {
        super();
    }

    @Override
    public String getApplicationName() {
        return "RECOMCommander";
    }

    @Override
    public String getPropertyFileName() {
        return "user";
    }

    private String protocol = "http";
    private String hostname = "localhost";
    private String port = "8080";

}
