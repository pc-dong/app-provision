package cn.dpc.provision.domain.exception;

public class StatusNotMatchException extends RuntimeException{

    public StatusNotMatchException(String id) {
        super("Operation denied for current status id: ["  + id + "]");
    }
}
