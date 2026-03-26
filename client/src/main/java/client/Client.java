package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;

import java.util.*;

import static client.State.*;
import static ui.EscapeSequences.*;
import static ui.Screens.*;

public class Client {
    private static final String LIGHT_SQUARE_BG = SET_BG_COLOR_WHITE;
    private static final String DARK_SQUARE_BG = SET_BG_COLOR_BLACK;
    private static final String WHITE_PIECE_COLOR = SET_TEXT_COLOR_RED;
    private static final String BLACK_PIECE_COLOR = SET_TEXT_COLOR_BLUE;
    private static final String COORDINATE_COLOR = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK;

    private final ServerFacade server;
    private State currentState;
    private final Scanner scanner;
    private String authToken;
    private String lastCommand;
    private List<GameData> lastListedGames;

    public Client(String url) {
        server = new ServerFacade(url);
        scanner = new Scanner(System.in);
        authToken = "";
        lastCommand = "";
        lastListedGames = new ArrayList<>();
    }

    public void run() {
        currentState = SIGNED_OUT;
        print(WHITE_KING + " Welcome to CS240 Chess Online!" + WHITE_KING);
        print(SET_TEXT_COLOR_GREEN + "Type \"help\" to get started.\n");

        while (!lastCommand.equals("quit")) {
            String input = readCommand(currentState);
            interpretCommand(input, currentState);
        }
        print("Goodbye!");
    }

    private String readCommand(State state) {
        printPrompt(state);
        String input = scanner.nextLine();
        return input.trim();
    }

    private void printPrompt(State state) {
        printInLine(SET_TEXT_ITALIC
                + SET_TEXT_COLOR_BLUE
                + "[%s]".formatted(state)
                + RESET_ALL
                + SET_TEXT_BLINKING
                + " >>> "
                + RESET_TEXT_BLINKING);
    }

    private void interpretCommand(String input, State state) {
        if (input.isBlank()) {
            return;
        }

        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();
        lastCommand = command;

        switch (state) {
            case SIGNED_OUT -> handleSignedOut(command, parts);
            case LOBBY -> handleLobby(command, parts);
            case IN_GAME -> handleInGame(command, parts);
        }

    }

    private void handleInGame(String command, String[] parts) {
        switch (command) {
            case "help" -> print("In game help menu not set up yet. Type \"exit\" to exit game.");
            case "exit" -> { clearScreen(); print("Leaving game."); currentState = LOBBY; }
            case "quit" -> {}
            default -> print("Unknown command. Type \"help\" to see commands.");
        }
    }

    private void handleLobby(String command, String[] parts) {
        switch (command) {
            case "create" -> handleCreate(parts);
            case "list" -> list();
            case "join" -> handleJoin(parts);
            case "observe" -> handleObserve(parts);
            case "help" -> printHelpScreen(LOBBY);
            case "logout" -> logout();
            case "quit" -> {}
            default -> print("Unknown command. Type \"help\" to see commands.");
        }
    }

    private void handleObserve(String[] parts) {
        if (parts.length != 2) {
            print(SET_TEXT_COLOR_RED + "Usage: observe <ID>");
            return;
        }
        int ID;
        try {
            ID = Integer.parseInt(parts[1]);
            if (ID <= 0 || ID > lastListedGames.size()) {
                print(SET_TEXT_COLOR_RED + "Invalid ID");
                return;
            }
        } catch (NumberFormatException e) {
            print(SET_TEXT_COLOR_RED + "ID must be a number.");
            return;
        }

        observe(ID);
    }

    private void observe(int ID) {
        drawBoard(lastListedGames.get(ID - 1).game(), ChessGame.TeamColor.WHITE);
    }

    private void handleJoin(String[] parts) {
        if (parts.length != 3) {
            print(SET_TEXT_COLOR_RED + "Usage: join <ID> <WHITE|BLACK>");
            return;
        }
        if (lastListedGames.isEmpty()) {
            print(SET_TEXT_COLOR_RED + "Error: You either have not listed games or there are none created.");
            return;
        }

        int ID;
        try {
            ID = Integer.parseInt(parts[1]);
            if (ID <= 0 || ID > lastListedGames.size()) {
                print(SET_TEXT_COLOR_RED + "Invalid ID");
                return;
            }
        } catch (NumberFormatException e) {
            print(SET_TEXT_COLOR_RED + "ID must be a number.");
            return;
        }

        ChessGame.TeamColor color;
        try {
            color = ChessGame.TeamColor.valueOf(parts[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            print(SET_TEXT_COLOR_RED + "Color must be WHITE or BLACK.");
            return;
        }

        join(ID, color);
    }

    private void handleCreate(String[] parts) {
        if (parts.length != 2) {
            print(SET_TEXT_COLOR_RED + "Usage: create <NAME>");
            return;
        }
        createGame(parts[1]);
    }

    private void createGame(String name) {
        try {
            server.createGame(name, authToken);
            print("Game created! Type \"list\" to see all games.");
        } catch (ResponseException e) {
            printResponseError("create the game", e);
        }
    }

    private void join(int ID, ChessGame.TeamColor color) {
        try {
            server.joinGame(color.toString(), lastListedGames.get(ID - 1).gameID(), authToken);
            currentState = IN_GAME;
            print(SET_TEXT_COLOR_BLUE + "JOINED!");
            drawBoard(lastListedGames.get(ID - 1).game(), color);
        } catch (ResponseException e) {
            printResponseError("join the game", e);
        }
    }

    private void list() {
        try {
            var gameList = server.listGames(authToken);
            lastListedGames.clear();
            for (int i = 0; i < gameList.games().size(); i++) {
                GameData game = gameList.games().get(i);
                lastListedGames.add(game);

                var whitePlayer = game.whiteUsername() == null ? "EMPTY" : game.whiteUsername();
                var blackPlayer = game.blackUsername() == null ? "EMPTY" : game.blackUsername();
                var gameName = game.gameName();

                print(SET_TEXT_COLOR_MAGENTA + (i+1) + ": " + gameName);
                print(WHITE_KING + ": " + SET_TEXT_COLOR_BLUE + whitePlayer);
                print(BLACK_KING + ": " + SET_TEXT_COLOR_BLUE + blackPlayer);
                print(SET_TEXT_COLOR_DARK_GREY + SEPERATOR);
            }
        } catch (ResponseException e) {
            printResponseError("list games", e);
        }
    }

    private void handleSignedOut(String command, String[] parts) {
        switch (command) {
            case "register" -> handleRegister(parts);
            case "login" -> handleLogin(parts);
            case "help" -> printHelpScreen(currentState);
            case "quit" -> {}
            default -> print("Unknown command. Type \"help\" to see commands.");
        }
    }

    private void handleLogin(String[] parts) {
        if (parts.length != 3) {
            print(SET_TEXT_COLOR_RED + "Usage: login <USERNAME> <PASSWORD>");
            return;
        }
        login(parts[1], parts[2]);
    }

    private void handleRegister(String[] parts) {
        if (parts.length != 4) {
            print(SET_TEXT_COLOR_RED + "Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }
        register(parts[1], parts[2], parts[3]);
    }

    private void register(String username, String password, String email) {
        try {
            var registerResponse = server.register(username, password, email);
            authToken = registerResponse.authToken();
            currentState = LOBBY;
            print("Logged in!");
        } catch (ResponseException e) {
            printResponseError("register", e);
        }
    }

    private void login(String username, String password) {
        try {
            var loginResponse = server.login(username, password);
            authToken = loginResponse.authToken();
            currentState = LOBBY;
            print("Logged in!");
        } catch (ResponseException e) {
            printResponseError("login", e);
        }
    }

    private void logout() {
        try {
            server.logout(authToken);
            authToken = "";
            currentState = SIGNED_OUT;
            print("Logged out.");
        } catch (ResponseException e) {
            printResponseError("logout", e);
        }
    }

    private void printHelpScreen(State state) {
        switch (state) {
            case SIGNED_OUT -> printInLine(LOGGED_OUT_HELP_SCREEN);
            case LOBBY -> printInLine(LOGGED_IN_HELP_SCREEN);
            case IN_GAME -> {}
        }
    }

    private void drawBoard(ChessGame game, ChessGame.TeamColor perspective) {
        ChessBoard board = game.getBoard();
        int[] rowOrder = perspective == ChessGame.TeamColor.BLACK
                ? new int[]{1, 2, 3, 4, 5, 6, 7, 8}
                : new int[]{8, 7, 6, 5, 4, 3, 2, 1};
        int[] colOrder = perspective == ChessGame.TeamColor.BLACK
                ? new int[]{8, 7, 6, 5, 4, 3, 2, 1}
                : new int[]{1, 2, 3, 4, 5, 6, 7, 8};

        clearScreen();
        print(renderColumnLabels(colOrder));

        for (int row : rowOrder) {
            StringBuilder line = new StringBuilder();
            line.append(COORDINATE_COLOR).append(" ").append(row).append(" ").append(RESET_ALL);

            for (int col : colOrder) {
                line.append(renderSquare(board, row, col));
            }

            line.append(COORDINATE_COLOR).append(" ").append(row).append(" ").append(RESET_ALL);
            print(line.toString());
        }

        print(renderColumnLabels(colOrder));
    }

    private String renderColumnLabels(int[] colOrder) {
        StringBuilder labels = new StringBuilder();
        labels.append(COORDINATE_COLOR).append("   ");

        for (int col : colOrder) {
            labels.append(" ").append((char) ('a' + col - 1)).append(" ");
        }

        return labels.append(RESET_ALL).toString();
    }

    private String renderSquare(ChessBoard board, int row, int col) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        String squareColor = ((row + col) % 2 == 0) ? DARK_SQUARE_BG : LIGHT_SQUARE_BG;
        String pieceColor = piece == null
                ? RESET_TEXT_COLOR
                : (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PIECE_COLOR : BLACK_PIECE_COLOR);

        return squareColor + pieceColor + pieceSymbol(piece) + RESET_ALL;
    }

    private String pieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        };
    }

    private void clearScreen() {
        printInLine("\u001b[3J" + ERASE_SCREEN);
        System.out.flush();
    }

    private void printResponseError(String action, ResponseException e) {
        String message;

        if (action.equals("login") && e.getHttpStatusCode() == 401) {
            message = "Username or password was incorrect.";
        } else if (action.equals("register") && e.getHttpStatusCode() == 403) {
            message = "That username is already taken.";
        } else if (action.equals("join the game") && e.getHttpStatusCode() == 403) {
            message = "That color is already taken.";
        } else {
            message = switch (e.getHttpStatusCode()) {
                case 400 -> "Unable to " + action + ". Please check your input.";
                case 401 -> "You must be logged in to " + action + ".";
                case 403 -> "You are not allowed to " + action + ".";
                default -> "Unable to " + action + " right now. Please try again.";
            };
        }

        print(SET_TEXT_COLOR_RED + message);
    }

    private void print(String string) {
        System.out.println(string + RESET_ALL);
    }

    private void printInLine(String string) {
        System.out.print(string);
    }
}
