package server.websocket;

import chess.serialization.GsonFactory;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import exception.BadRequestException;
import exception.ResponseException;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final SQLGameDAO gameDAO;
    private final SQLAuthDAO authDAO;
    private final WebSocketService service;

    public WebSocketHandler(SQLGameDAO gameDAO, SQLAuthDAO authDAO, WebSocketService service) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.service = service;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {

    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        String json = ctx.message();
        UserGameCommand base = GsonFactory.create().fromJson(json, UserGameCommand.class);

        switch (base.getCommandType()) {
            case CONNECT -> connectToGame(ctx, base);
            case LEAVE -> leaveGame(ctx, base);
            case RESIGN -> resignGame(ctx, base);
        }
    }

    private void connectToGame(WsMessageContext ctx, UserGameCommand command) throws ResponseException {
        assert command.getCommandType() == UserGameCommand.CommandType.CONNECT;

        String authToken = command.getAuthToken();
        String username = service.getUsername(authToken);
        Integer gameID = command.getGameID();

        service.validateAuthToken(authToken);
        service.validateGameExists(gameID);
        boolean observing = !service.isPlayerInGame(gameID, authToken);

        connectionManager.connections.putIfAbsent(gameID, new ArrayList<>());
        connectionManager.addSessionToGame(gameID, ctx.session);

        ctx.send(new LoadGameMessage(service.getGameData(gameID).game()).toString());

        String role = observing ? "an observer" : "a player";
        var notification = new NotificationMessage(username + " joined the game as " + role + ".");
        try {
            connectionManager.broadcastMessageToGame(gameID, ctx.session, notification);
        } catch (IOException e) {
            throw new ResponseException(500, "Server suffered an error.");
        }
    }

    private void leaveGame(WsMessageContext ctx, UserGameCommand command) throws ResponseException {
        assert command.getCommandType().equals(UserGameCommand.CommandType.LEAVE);

        String authToken = command.getAuthToken();
        String username = service.getUsername(authToken);
        Integer gameID = command.getGameID();

        service.validateAuthToken(authToken);
        service.validateGameExists(gameID);

        connectionManager.removeSessionFromGame(gameID, ctx.session);

        var notification = new NotificationMessage(username + " has left the game.");

        try {
            connectionManager.broadcastMessageToGame(gameID, null, notification);
        } catch (IOException e) {
            throw new ResponseException(500, "Error: Server could not broadcast notification.");
        }
    }

    private void resignGame(WsMessageContext ctx, UserGameCommand command) throws ResponseException {
        assert command.getCommandType().equals(UserGameCommand.CommandType.RESIGN);

        String authToken = command.getAuthToken();
        String username = service.getUsername(authToken);
        Integer gameID = command.getGameID();

        service.validateAuthToken(authToken);
        service.validateGameExists(gameID);

        if (!service.isPlayerInGame(gameID, authToken)) {
            throw new BadRequestException();
        }


    }
}
