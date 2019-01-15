import component.*;
import spark.*;
import static spark.Spark.*;
import spark.template.velocity.*;
import java.util.*;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;
import java.util.concurrent.*;


public class ContractWhistOnline {
    public static Trumps t = new Trumps();
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user
    static ArrayList<Player> players = new ArrayList();
    static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    static CompletionService<String> pool = new ExecutorCompletionService<>(threadPool);

    public static void main(String[] args){
        System.out.println("Initialising Application");

        players.add(new MaxPlayer("Max"));
        players.add(new MiniWinPlayer("Michael"));
        players.add(new RandomPlayer("Random"));

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");

        port(1816);
        staticFiles.expireTime(600);
        webSocket("/updates", WhistWebSocketHandler.class);
        init();

        // Render initial game info
        get("/", (req, res) -> renderInfo(req));
    }

    public static void startGameTask() throws InterruptedException{
        pool.submit(new gameTask());
    }

    public static void startGame() throws InterruptedException{
        System.out.println("STARTING GAME");

        int handSize = 7;

        updateGame(Integer.toString(handSize), "blank.png");
        gameResults(players, (t), handSize);
        threadPool.shutdown();
    }

    public static void phase1Update(int playerID) {
        playerID++; // Account for 0 index

        String newPlayer = Integer.toString(playerID);

        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("phase", 1)
                        .put("playerID", newPlayer)

                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void showWinner(int winnerID) {
        winnerID++; // Account for 0 index

        String winner = Integer.toString(winnerID);

        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("phase", 4)
                        .put("winnerID", winner)

                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void changeSpeed(int level){
        t.changeSpeed(level);
    }

    public static void updateGame(String cardsLeft, String topCardSrc) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("phase", 3)
                        .put("cardsLeft", cardsLeft)
                        .put("topCard", topCardSrc)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static String renderInfo(Request req) {
        Map<String, Object> model = new HashMap<>();
        ArrayList<String> playerNames = new ArrayList();

        for(int i=0; i<3;i++){
            playerNames.add(players.get(i).getName());
        }

        model.put("cardsLeft", (t.getCardsLeft()));
        model.put("player1", playerNames.get(0));
        model.put("player2", playerNames.get(1));
        model.put("player3", playerNames.get(2));

        /* model.put("filter", Optional.ofNullable(statusStr).orElse(""));
        model.put("activeCount", TodoDao.ofStatus(Status.ACTIVE).size());
        model.put("anyCompleteTodos", TodoDao.ofStatus(Status.COMPLETE).size() > 0);
        model.put("allComplete", TodoDao.all().size() == TodoDao.ofStatus(Status.COMPLETE).size());
        model.put("status", Optional.ofNullable(statusStr).orElse("")); */
        if ("true".equals(req.queryParams("ic-request"))) {
            return renderTemplate("velocity/whist.vm", model);
        }
        return renderTemplate("velocity/index.vm", model);
    }

    private static void gameResults(ArrayList<Player> players, Trumps t, int handSize) throws InterruptedException{
        int perc = 0;
        float percF;
        for(int i = 0; i<10; i++){
            t.runTrumps(handSize, players);
            percF = (float) (((float)( (float) i / (float) 100000)) * (float) 100);
            if(Math.floor(percF)>perc){
                perc++;
                // System.out.println(perc + "%");
            }
        }

        for(int i = 0; i<players.size(); i++){
            System.out.println(players.get(i).getName() + ": " + players.get(i).getPoints());
        }
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }
}
