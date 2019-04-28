import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.Callable;

// Class used to continually poll the web app to ensure it does not timeout
// Mostly only needed for training the classifier, but only pulsing every 2 minutes it doesn't hurt to keep it running
public class heartbeatTask implements Callable<String> {
    public String call() throws InterruptedException, IOException {
        do{
            Thread.sleep(120000);
            ContractWhistOnline.pulse();
        } while(true);
    }
}
