package com.recom.model.configuration.descriptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.model.configuration.ConfigurationType;
import com.recom.service.provider.StaticObjectMapperProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

public class RegisteredListConfigurationValueDescriptor<TYPE> extends BaseRegisteredConfigurationValueDescriptable {

    public RegisteredListConfigurationValueDescriptor(String namespace, String name, String defaultValue, Boolean enabled, List<TYPE> listValue) {
        super(namespace, name, defaultValue, enabled);
    }

    public static <TYPE> RegisteredListConfigurationValueDescriptorBuilder<TYPE> builder() {
        return new RegisteredListConfigurationValueDescriptorBuilder<TYPE>();
    }

    @NonNull
    @Override
    public ConfigurationType getType() {
        return ConfigurationType.LIST;
    }


    @RequiredArgsConstructor
    public static class RegisteredListConfigurationValueDescriptorBuilder<TYPE> {
        private String namespace;
        private String name;
        private Boolean enabled;
        private ConfigurationType typeHint;
        private List<TYPE> listValue;

        @SneakyThrows
        public RegisteredListConfigurationValueDescriptor<TYPE> build() {
            final String defaultValue = StaticObjectMapperProvider.provide().writeValueAsString(listValue);
            return new RegisteredListConfigurationValueDescriptor<TYPE>(namespace, name, defaultValue, enabled, listValue);
        }

        public RegisteredListConfigurationValueDescriptorBuilder<TYPE> namespace(@NonNull final String namespace) {
            this.namespace = namespace;
            return this;
        }

        public RegisteredListConfigurationValueDescriptorBuilder<TYPE> name(@NonNull final String name) {
            this.name = name;
            return this;
        }

        public RegisteredListConfigurationValueDescriptorBuilder<TYPE> enabled(@NonNull final Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public RegisteredListConfigurationValueDescriptorBuilder<TYPE> typeHint(@NonNull final ConfigurationType configurationType) {
            this.typeHint = configurationType;
            return this;
        }

        public RegisteredListConfigurationValueDescriptorBuilder<TYPE> listValue(@NonNull final List<TYPE> listValue) {
            this.listValue = listValue;
            return this;
        }

    }

}