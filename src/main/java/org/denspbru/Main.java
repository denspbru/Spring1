package org.denspbru;

import org.denspbru.*;
import org.denspbru.impl.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        /* v.1
        IMessageService messageSrvs  = new MessageService();
        messageSrvs.setMessage("Hello");
        IPrinter printer=  new ConsolePrinter();
        printer.setMessage(messageSrvs);
        printer.print();
        */

        ApplicationContext ctx = new FileSystemXmlApplicationContext("web/config.xml");
        IPrinter printer =  ctx.getBean("consolePrinter", ConsolePrinter.class);
        printer.print();

    }
}
