package com.recom.dynamicproperties;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class TestProperties extends DynamicProperties {

    @Override
    public String getApplicationName() {
        return "RECOMCommander";
    }

    @Override
    public Path getPropertiesBasePath() {
        return PropertiesTest.TEST_BASE_PATH;
    }

    @Override
    String getPropertyFileName() {
        return "file";
    }

    private String protocol = "http";
    private String hostname = "localhost";
    private String port = "8080";

}
