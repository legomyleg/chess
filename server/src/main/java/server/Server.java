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

    private final Javalin javalin;
    private final RegisterService userService;
    private final ClearService clearService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ListGamesService listGamesService;
    private final CreateGameService createGameService;
    private final JoinGameService joinGameService;

    public Server() {
        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();
        var userDAO = new MemoryUserDAO();
        userService = new RegisterService(authDAO, userDAO);
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

        // Register your endpoints and exception handlers here.

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

        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        ctx.result(new Gson().toJson(result));
    }

    private void login(Context ctx) throws ResponseException {

        LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);
        var result = loginService.login(request);
        ctx.result(new Gson().toJson(result));

    }

    private void logout(Context ctx) throws ResponseException {

        String authToken = ctx.header("authorization");
        logoutService.logout(authToken);
        ctx.status(200);

    }

    private void listGames(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        GameListResult result = listGamesService.listGames(authToken);
        ctx.result(new Gson().toJson(result));
    }

    private void createGame(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        var request = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameResult result = createGameService.createGame(request, authToken);

        ctx.result(new Gson().toJson(result));
    }

    private void joinGame(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        var request = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
        joinGameService.joinGame(request, authToken);
        ctx.status(200);
    }

    private void deleteAllData(Context ctx) {
        clearService.deleteAllData();
        ctx.status(204);
    }
}
