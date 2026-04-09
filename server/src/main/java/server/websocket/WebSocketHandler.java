package server.websocket;

import chess.serialization.GsonFactory;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import exception.BadRequestException;
import exception.DatabaseErrorException;
import exception.ResponseException;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final WebSocketService service;

    public WebSocketHandler(WebSocketService service) {
        this.service = service;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {

    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        String json = ctx.message();
        UserGameCommand base = GsonFactory.create().fromJson(json, UserGameCommand.class);

        try {
            switch (base.getCommandType()) {
                case CONNECT -> service.connect(ctx, base, connectionManager);
                case LEAVE -> service.leave(ctx, base, connectionManager);
                case RESIGN -> service.resign(ctx, base, connectionManager);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = GsonFactory.create().fromJson(json, MakeMoveCommand.class);
                    makeMove(ctx, moveCommand);
                }
            }
        } catch(ResponseException e) {
            ctx.send(new ErrorMessage(e.getMessage()).toString());
        } catch (Exception e) {
            ctx.send(new ErrorMessage("Server error").toString());
        }
    }

    private void makeMove(WsMessageContext ctx, MakeMoveCommand command) {
        var move = command.getMove();

    }
}
