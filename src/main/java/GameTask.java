import java.io.IOException;
import java.util.concurrent.*;

public final class GameTask implements Callable<String> {
    public String call() throws InterruptedException, IOException {
        ContractWhistOnline.startGame();
        return "1";
    }
}