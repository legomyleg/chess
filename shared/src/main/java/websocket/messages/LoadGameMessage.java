package websocket.messages;

import chess.ChessGame;
import chess.serialization.GsonFactory;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    @Override
    public String toString() {
        return GsonFactory.create().toJson(this);
    }
}
