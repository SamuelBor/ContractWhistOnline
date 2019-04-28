import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;

// Web socket handler to connect the front end to the game engine
@WebSocket
public class WhistWebSocketHandler {

    // Handles a new connection to the web server
    @OnWebSocketConnect
    public void onConnect(Session user) {
        String username = "User" + ContractWhistOnline.nextUserNumber++;
        System.out.println(username + " connected.");
        ContractWhistOnline.userUsernameMap.put(user, username);
        ContractWhistOnline.userSessionMap.put(username, user);
        ContractWhistOnline.startGameTask(user);
    }

    // Handles a terminated connection from the web server
    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        System.out.println(ContractWhistOnline.userUsernameMap.get(user) + " disconnected.");
        System.out.println("Status Code: " + statusCode);
        System.out.println("Reason: " + reason);
        ContractWhistOnline.userUsernameMap.remove(user);
    }

    // Handles a message from the front end to the back end game engine
    @OnWebSocketMessage
    public void onMessage(Session user, String message) throws IOException, InterruptedException {
        System.out.println(ContractWhistOnline.userUsernameMap.get(user) + " sent [" + message + "]");
        String[] parts = message.split(":");
        // Breaks up the message received and sends it to the correct method in the Contract Whist Online class
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