package server.websocket;

import chess.serialization.GsonFactory;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private SQLGameDAO gameDAO;
    private SQLAuthDAO authDAO;

    public WebSocketHandler()

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
            case LEAVE -> leaveGame()

        }
    }

    private void connectToGame(WsMessageContext ctx, UserGameCommand command) {
        assert command.getCommandType() == UserGameCommand.CommandType.CONNECT;

        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();
        var game = getGameByGameID
    }
}
