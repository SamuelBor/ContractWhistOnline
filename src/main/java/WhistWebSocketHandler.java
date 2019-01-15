import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class WhistWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + ContractWhistOnline.nextUserNumber++;
        System.out.println(username + " connected.");
        ContractWhistOnline.userUsernameMap.put(user, username);
        ContractWhistOnline.startGameTask();
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        ContractWhistOnline.userUsernameMap.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        System.out.println(user + " sent " + message);
        String[] parts = message.split(":");
        switch(parts[0]){
            case "SPEED":
                ContractWhistOnline.changeSpeed(Integer.parseInt(parts[1]));
                break;
        }
    }
}