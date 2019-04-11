import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.Callable;

public class heartbeatTask implements Callable<String> {
    public String call() throws InterruptedException, IOException {
        do{
            Thread.sleep(120000);
            ContractWhistOnline.pulse();
        } while(true);
    }
}
