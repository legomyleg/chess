package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.Nullable;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void addGame(Integer gameID) {
        connections.put(gameID, new CopyOnWriteArrayList<>());
    }

    public void addSessionToGame(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, ignored -> new CopyOnWriteArrayList<>()).add(session);
    }

    public void removeSessionFromGame(Integer gameID, Session session) {
        List<Session> sessionsInGame = connections.get(gameID);
        if (sessionsInGame != null) {
            sessionsInGame.remove(session);
        }
    }

    public void broadcastMessageToGame(Integer gameID, @Nullable Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        List<Session> sessionsInGame = connections.get(gameID);
        if (sessionsInGame == null) {
            return;
        }
        for (Session session : sessionsInGame) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                session.getRemote().sendString(msg);
            }
        }
    }
}
