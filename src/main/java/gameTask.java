import java.util.concurrent.*;

public final class gameTask implements Callable<String> {
    public String call() throws InterruptedException {
        ContractWhistOnline.startGame();
        return "1";
    }
}