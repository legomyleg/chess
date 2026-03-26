package client.responses;

import model.GameData;

import java.util.Collection;
import java.util.List;

public record ListGamesResponse(List<GameData> games) {}
