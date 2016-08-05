import org.denspbru.IMessageService;
import org.denspbru.IPrinter;
import org.denspbru.impl.ConsolePrinter;
import org.denspbru.impl.MessageService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;


public class Spring1Test {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void theTest() {
        IMessageService messageSrvs  = new MessageService();
        messageSrvs.setMessage("Hello");
        IPrinter printer=  new ConsolePrinter();
        printer.setMessage(messageSrvs);
        printer.print();

        org.junit.Assert.assertEquals("Hello", systemOutRule.getLog());
    }
}
