import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.*;

// Game task to run on a thread
// Runs an instance of a contract whist runner for a specific session
public final class GameTask implements Callable<String> {
    private Session s;
    private String agents;

    public GameTask(Session s, String agents){
        this.s = s;
        this.agents = agents;
    }

    public String call() throws InterruptedException, IOException {
        ContractWhistOnline.addAgents(s, agents);
        return "1";
    }
}