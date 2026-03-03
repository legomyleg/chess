package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import request.RegisterRequest;
import result.RegisterResult;
import service.ClearService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final ClearService clearService;

    public Server() {
        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();
        var userDAO = new MemoryUserDAO();
        userService = new UserService(authDAO, userDAO);
        clearService = new ClearService(gameDAO, authDAO, userDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/login", this::login)
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

    private void login(Conte)

    private void deleteAllData(Context ctx) {
        clearService.deleteAllData();
        ctx.status(204);
    }
}
