package cn.dpc.provision.domain.exception;

public class ConfigurationNotFoundException extends RuntimeException{

    public ConfigurationNotFoundException(String id) {
        super("Configuration not found id: ["  + id + "]");
    }
}
