package client;

import chess.serialization.ChessGameAdapter;
import client.bodytypes.LoginBody;
import client.bodytypes.RegisterBody;
import client.responses.ErrorResponse;
import client.responses.ListGamesResponse;
import client.responses.LoginResponse;
import client.responses.RegisterResponse;
import com.google.gson.Gson;
import exception.ResponseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResponse register(String username, String password, String email) throws ResponseException {
        var registerBody = new RegisterBody(username, password, email);
        var request = buildRequest("POST", "/user", registerBody, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResponse.class);
    }

    public LoginResponse login(String username, String password) throws ResponseException {
        var loginBody = new LoginBody(username, password);
        var request = buildRequest("POST", "/session", loginBody, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResponse.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        sendRequest(request);
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResponse.class, ChessGameAdapter.createSerializer());
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object body) {
        if (body == null) {
            return BodyPublishers.noBody();
        }
        return BodyPublishers.ofString(new Gson().toJson(body));
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            var response = client.send(request, BodyHandlers.ofString());

            if (isUnsuccessful(response.statusCode())) {
                var error = new Gson().fromJson(response.body(), ErrorResponse.class);
                var message = error != null && error.message() != null
                        ? error.message()
                        : "Request failed";

                throw new ResponseException(response.statusCode(), message);
            }
            return response;
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseException(500, "Request interrupted");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType, Gson serializer) throws ResponseException {
        var status = response.statusCode();
        if (isUnsuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(response.body());
            }

            throw new ResponseException(status, "Other failure: " + status);
        }

        if (responseType == null) {
            return null;
        }
        if (serializer == null) {
            return new Gson().fromJson(response.body(), responseType);
        }
        return serializer.fromJson(response.body(), responseType);
    }

    private boolean isUnsuccessful(int status) {
        return (status / 100) != 2;
    }

}
