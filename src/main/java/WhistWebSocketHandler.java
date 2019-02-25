import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;

@WebSocket
public class WhistWebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) {
        String username = "User" + ContractWhistOnline.nextUserNumber++;
        System.out.println(username + " connected.");
        ContractWhistOnline.userUsernameMap.put(user, username);
        ContractWhistOnline.startGameTask(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        System.out.println(ContractWhistOnline.userUsernameMap.get(user) + " disconnected.");
        System.out.println("Status Code: " + statusCode);
        System.out.println("Reason: " + reason);
        ContractWhistOnline.userUsernameMap.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) throws IOException, InterruptedException {
        System.out.println(ContractWhistOnline.userUsernameMap.get(user) + " sent [" + message + "]");
        String[] parts = message.split(":");
        switch(parts[0]){
            case "SPEED":
                ContractWhistOnline.changeSpeed(user, Integer.parseInt(parts[1]));
                break;
            case "AGENTS":
                ContractWhistOnline.startGameAgents(user, parts[1]);
                break;
        }
    }
}