import component.*;
import spark.*;
import static spark.Spark.*;
import spark.template.velocity.*;
import java.util.*;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;
import java.util.concurrent.*;

@SuppressWarnings("Duplicates")
public class ContractWhistOnline {
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static Map<String, Session> userSessionMap = new ConcurrentHashMap<>();
    static Map<Session, ContractWhistRunner> sessionGames = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user
    private static ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private static CompletionService<String> pool = new ExecutorCompletionService<>(threadPool);

    public static void main(String[] args){
        System.out.println("Initialising Application");

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");

        port(1816);
        staticFiles.expireTime(900);
        webSocket("/updates", WhistWebSocketHandler.class);
        init();

        // Render initial game info
        get("/", (req, res) -> renderInfo(req));
    }

    // Creates a new thread to run the game
    static void startGameTask(Session user) {
        ContractWhistRunner c = new ContractWhistRunner();
        ContractWhistOnline.sessionGames.put(user, c);
    }

    // Starts the game
    static void startGameAgents(Session s, String agents) {
        System.out.println("STARTING GAME");
        pool.submit(new GameTask(s, agents));
    }

    static void addAgents(Session s, String agents) throws InterruptedException {
        ContractWhistRunner c = sessionGames.get(s);

        c.addAgents(agents, ContractWhistOnline.userUsernameMap.get(s));

        c.playContractWhist();
    }

    static void phase1Update(int playerID, int trump, String cardsLeft, Session s) {
        playerID++; // Account for 0 index
        String trumpString;

        switch(trump){
            case 1:  trumpString = "♣";
                break;
            case 2:  trumpString = "♥";
                break;
            case 3:  trumpString = "♦";
                break;
            case 4:  trumpString = "♠";
                break;
            default:
                trumpString = "#";
        }

        String newPlayer = Integer.toString(playerID);

        try {
            s.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("phase", 1)
                    .put("cardsLeft", cardsLeft)
                    .put("playerID", newPlayer)
                    .put("trump", trumpString)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void phase2Update(int playerID, int cardID, Session s) {
        playerID++; // Account for 0 index
        cardID++;

        String playerStr = Integer.toString(playerID);
        String cardStr = Integer.toString(cardID);

        try {
            s.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("phase", 2)
                    .put("playerID", playerStr)
                    .put("cardID", cardStr)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void showWinner(int winnerID, Session s) {
        winnerID++; // Account for 0 index

        String winner = Integer.toString(winnerID);

        try {
            s.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("phase", 4)
                    .put("winnerID", winner)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void changeSpeed(Session s, int level){
        ContractWhistRunner c = sessionGames.get(s);
        c.changeSpeed(level);
    }

    static void newHand(Player player, Session s) {
        ArrayList<Card> hand;

        hand = player.getHand();
        int pID = player.getID() + 1; // 0 index accounting

        String playerStr = Integer.toString(pID);
        String[] handPaths = new String[hand.size()];
        int i = 0;

        for (Card c : hand) {
            handPaths[i] = c.getFilename();
            i++;
        }

        try {
            s.getRemote().sendStringByFuture(String.valueOf(new JSONObject()
                    .put("phase", 5)
                    .put("playerID", playerStr)
                    .put("hand", handPaths)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void makePrediction(int playerID, int prediction, Session s) {
        playerID++; // Account for 0 index

        String playerStr = Integer.toString(playerID);
        String predictStr = Integer.toString(prediction);

        try {
            s.getRemote().sendStringByFuture(String.valueOf(new JSONObject()
                    .put("phase", 6)
                    .put("playerID", playerStr)
                    .put("prediction", predictStr)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void updateCurrentHands(int playerID, int currentHands, Session s) {
        playerID++; // Account for 0 index

        String playerStr = Integer.toString(playerID);
        String currentHandsStr = Integer.toString(currentHands);

        try {
            s.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("phase", 7)
                    .put("playerID", playerStr)
                    .put("current", currentHandsStr)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void updateScore(int playerID, int score, Session s) {
        playerID++; // Account for 0 index

        String playerStr = Integer.toString(playerID);
        String scoreStr = Integer.toString(score);

        try {
            s.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("phase", 8)
                    .put("playerID", playerStr)
                    .put("score", scoreStr)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void updateGame(String topCardSrc, int playerID, ArrayList hand, Session s) {
        //Account for 0 index
        playerID++;

        String playerStr = Integer.toString(playerID);
        String[] handPaths = new String[hand.size()];
        int i = 0;

        for (Object o : hand) {
            Card c = (Card) o;
            handPaths[i] = c.getFilename();
            i++;
        }

        try {
            s.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("phase", 3)
                    .put("topCard", topCardSrc)
                    .put("playerID", playerStr)
                    .put("hand", handPaths)
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Session getSession(String user){
        return userSessionMap.get(user);
    }

    private static String renderInfo(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("cardsLeft", "7");

        if ("true".equals(req.queryParams("ic-request"))) {
            return renderTemplate("velocity/whist.vm", model);
        }
        return renderTemplate("velocity/index.vm", model);
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }
}
