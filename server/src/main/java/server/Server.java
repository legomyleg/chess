package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.GameListResult;
import result.RegisterResult;
import service.*;

public class Server {

    private static final Gson GSON = new Gson();

    private final Javalin javalin;
    private final RegisterService registerService;
    private final ClearService clearService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ListGamesService listGamesService;
    private final CreateGameService createGameService;
    private final JoinGameService joinGameService;

    // TODO: Make these SQL DAOs
    public Server() {
        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();
        var userDAO = new MemoryUserDAO();
        registerService = new RegisterService(authDAO, userDAO);
        clearService = new ClearService(gameDAO, authDAO, userDAO);
        loginService = new LoginService(userDAO, authDAO);
        logoutService = new LogoutService(authDAO);
        listGamesService = new ListGamesService(authDAO, gameDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
                .get("/game", this::listGames)
                .delete("/session", this::logout)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::deleteAllData)
                .exception(ResponseException.class, this::exceptionHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.getHttpStatusCode());
        ctx.result(ex.toJson());
    }

    private void register(Context ctx) throws ResponseException {

        RegisterRequest request = GSON.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = registerService.register(request);
        ctx.result(GSON.toJson(result));
    }

    private void login(Context ctx) throws ResponseException {

        LoginRequest request = GSON.fromJson(ctx.body(), LoginRequest.class);
        var result = loginService.login(request);
        ctx.result(GSON.toJson(result));

    }

    private void logout(Context ctx) throws ResponseException {

        String authToken = ctx.header("authorization");
        logoutService.logout(authToken);
        ctx.status(200);

    }

    private void listGames(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        GameListResult result = listGamesService.listGames(authToken);
        ctx.result(GSON.toJson(result));
    }

    private void createGame(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        var request = GSON.fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameResult result = createGameService.createGame(request, authToken);

        ctx.result(GSON.toJson(result));
    }

    private void joinGame(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        var request = GSON.fromJson(ctx.body(), JoinGameRequest.class);
        joinGameService.joinGame(request, authToken);
        ctx.status(200);
    }

    private void deleteAllData(Context ctx) {
        clearService.deleteAllData();
        ctx.status(200);
    }
}
