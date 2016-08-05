package org.denspbru.impl;
import org.denspbru.IMessageService;

public class MessageService implements IMessageService {
    protected  String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message){
        this.message  = message;
    }
}
