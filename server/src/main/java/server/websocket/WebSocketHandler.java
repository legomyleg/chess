package server.websocket;

import chess.serialization.GsonFactory;
import exception.ResponseException;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;

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
                    service.makeMove(ctx, moveCommand, connectionManager);
                }
            }
        } catch(ResponseException e) {
            ctx.send(new ErrorMessage(e.getMessage()).toString());
        } catch (Exception e) {
            ctx.send(new ErrorMessage("Server error").toString());
        }
    }
}
