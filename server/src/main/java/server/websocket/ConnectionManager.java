package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void addGame(Integer gameID) {
        connections.put(gameID, new ArrayList<>());
    }

    public void addSessionToGame(Integer gameID, Session session) {
        connections.get(gameID).add(session);
    }

    public void removeSessionFromGame(Integer gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcastMessageToGame(Integer gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        List<Session> sessionsInGame = connections.get(gameID);
        for (Session session : sessionsInGame) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                session.getRemote().sendString(msg);
            }
        }
    }

}
