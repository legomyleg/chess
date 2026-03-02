package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        userService = new UserService(new MemoryAuthDAO(), new MemoryUserDAO());

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register);

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) throws DataAccessException {

        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);

        ctx.result(new Gson().toJson(result));
    }
}
