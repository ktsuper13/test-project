package org.game.tictactoe.service;

import org.game.tictactoe.model.Game;

public interface GameService {

    Game[] getGames();

    Game createGame(Game game);

    Game getGameById(String gameId);

    Game makeMove(String gameId, Game game);

    void deleteGame(String gameId);
}
