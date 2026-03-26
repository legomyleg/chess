package result;

import model.GameData;

import java.util.Collection;
import java.util.List;

public record GameListResult(List<GameData> games) {
}
