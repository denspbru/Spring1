package org.denspbru.impl;
import org.denspbru.IMessageService;
import  org.denspbru.IPrinter;

public class ConsolePrinter implements IPrinter {
    IMessageService message;
    public ConsolePrinter() {
        // empty constructor
    }
    public ConsolePrinter(IMessageService message) {
        this.message = message;
    }
    public void print() {
        System.out.print(message.getMessage());
    }

    public IMessageService getMessage() {
        return message;
    }

    public void setMessage(IMessageService message) {
        this.message = message;
    }

    public static  IPrinter MakePrinter(IMessageService message) {
        return new ConsolePrinter(message);
    }
}
