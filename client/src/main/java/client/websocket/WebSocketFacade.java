package client.websocket;

import chess.ChessMove;
import chess.serialization.GsonFactory;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

public class WebSocketFacade extends Endpoint {

    private final URI socketURI;
    private final ServerMessageHandler serverMessageHandler;
    private Session session;
    private String activeAuthToken;
    private Integer activeGameID;
    private boolean sessionConnectedToGame;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        this.serverMessageHandler = serverMessageHandler;
        try {
            url = url.replace("http", "ws");
            this.socketURI = new URI(url + "/ws");
            openSession();
        } catch (URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        this.sessionConnectedToGame = false;
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage base = GsonFactory.create().fromJson(message, ServerMessage.class);

                switch (base.getServerMessageType()) {
                    case LOAD_GAME -> serverMessageHandler.handleMessage(
                            GsonFactory.create().fromJson(message, LoadGameMessage.class));
                    case NOTIFICATION -> serverMessageHandler.handleMessage(
                            GsonFactory.create().fromJson(message, NotificationMessage.class));
                    case ERROR -> serverMessageHandler.handleMessage(
                            GsonFactory.create().fromJson(message, ErrorMessage.class));
                }
            }
        });
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.session = null;
        this.sessionConnectedToGame = false;
    }

    @Override
    public void onError(Session session, Throwable thr) {
        this.sessionConnectedToGame = false;
    }

    public void connect(String authToken, Integer gameID) throws ResponseException {
        try {
            ensureOpenSession();
            activeAuthToken = authToken;
            activeGameID = gameID;
            sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
            sessionConnectedToGame = true;
        } catch (IOException | IllegalStateException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        try {
            ensureOpenSession();
            sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
            if (Objects.equals(activeAuthToken, authToken) && Objects.equals(activeGameID, gameID)) {
                activeAuthToken = null;
                activeGameID = null;
                sessionConnectedToGame = false;
            }
        } catch (IOException | IllegalStateException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            ensureConnectedToGame(authToken, gameID);
            sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
        } catch (IOException | IllegalStateException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            ensureConnectedToGame(authToken, gameID);
            sendCommand(new MakeMoveCommand(authToken, gameID, move));
        } catch (IOException | IllegalStateException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void ensureOpenSession() throws ResponseException {
        if (session == null || !session.isOpen()) {
            openSession();
        }
    }

    private void openSession() throws ResponseException {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
        } catch (DeploymentException | IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void ensureConnectedToGame(String authToken, Integer gameID) throws ResponseException, IOException {
        ensureOpenSession();
        if (!sessionConnectedToGame || !Objects.equals(activeAuthToken, authToken) || !Objects.equals(activeGameID, gameID)) {
            activeAuthToken = authToken;
            activeGameID = gameID;
            sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
            sessionConnectedToGame = true;
        }
    }

    private void sendCommand(UserGameCommand command) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }
}
