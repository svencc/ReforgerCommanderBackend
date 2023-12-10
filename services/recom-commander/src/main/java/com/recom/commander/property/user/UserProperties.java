package com.recom.commander.property.user;

import com.recom.dynamicproperties.DynamicUserProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProperties extends DynamicUserProperties {

    @Override
    public String getApplicationName() {
        return "RECOMCommander";
    }

    @Override
    public String getPropertyFileName() {
        return "user";
    }

    @Builder.Default
    private String protocol = "http";

    @Builder.Default
    private String hostname = "localhost";

    @Builder.Default
    private String port = "8080";

}
