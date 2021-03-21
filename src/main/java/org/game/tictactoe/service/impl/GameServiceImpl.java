package org.game.tictactoe.service.impl;

import org.game.tictactoe.exceptions.GameBadRequestException;
import org.game.tictactoe.exceptions.GameNotFoundException;
import org.game.tictactoe.model.Game;
import org.game.tictactoe.model.enums.GameStatus;
import org.game.tictactoe.service.GameService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class GameServiceImpl implements GameService {

    private static final int MAX_NUMBER_CELLS = 9;
    private static final int NUMBER_OF_COLS = 3;
    private static final char X_SYMBOL = 'X';
    private static final char O_SYMBOL = 'O';

    // Indexes of board state(as string) win combinations
    private static int[][] winCombinationsIndexes =
            {{0, 1, 2},
                    {3, 4, 5},
                    {6, 7, 8},
                    {0, 3, 6},
                    {1, 4, 7},
                    {2, 5, 8},
                    {0, 4, 8},
                    {2, 4, 6}};

    private ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();

    @Override
    public Game[] getGames() {
        return games.values().toArray(new Game[0]);
    }

    @Override
    public Game createGame(Game game) {
        checkInput(game.getBoard());
        char[] board = game.getBoard().toCharArray();

        boolean isXWon = checkWinner(board, X_SYMBOL);
        boolean isOWon = checkWinner(board, O_SYMBOL);
        checkBoardValidity(board, isXWon, isOWon);
        applyCorrectStatus(game, isXWon, isOWon);

        byte xCounter = getSymbolCount(board, X_SYMBOL);
        byte oCounter = getSymbolCount(board, O_SYMBOL);

        if (game.getStatus().equals(GameStatus.RUNNING) && (xCounter > oCounter)) {
            serverMakeMove(game);
        }

        String id = UUID.randomUUID().toString();
        game.setId(id);
        game.setBoard(game.getBoard());
        games.put(id, game);
        return game;
    }

    @Override
    public Game getGameById(String gameId) {
        if (!games.containsKey(gameId)) {
            throw new GameNotFoundException();
        }
        return games.get(gameId);
    }

    @Override
    public Game makeMove(String gameId, Game newMoveGame) {
        checkInput(newMoveGame.getBoard());
        if (!games.containsKey(gameId)) {
            throw new GameNotFoundException();
        }
        Game prevGame = games.get(gameId);
        if (prevGame.getStatus().equals(GameStatus.RUNNING)) {
            char[] board = newMoveGame.getBoard().toCharArray();
            boolean isXWon = checkWinner(board, X_SYMBOL);
            boolean isOWon = checkWinner(board, O_SYMBOL);
            checkBoardValidity(board, isXWon, isOWon);
            applyCorrectStatus(newMoveGame, isXWon, isOWon);

            if (newMoveGame.getStatus().equals(GameStatus.RUNNING)) {
                serverMakeMove(newMoveGame);
            }

            prevGame.setBoard(newMoveGame.getBoard());
            prevGame.setStatus(newMoveGame.getStatus());
        }

        return prevGame;
    }

    @Override
    public void deleteGame(String gameId) {
        if (!games.containsKey(gameId)) {
            throw new GameNotFoundException();
        }
        games.remove(gameId);
    }

    private void checkBoardValidity(char[] board, boolean isXWon, boolean isOWon) {
        byte oCounter = getSymbolCount(board, O_SYMBOL);
        byte xCounter = getSymbolCount(board, X_SYMBOL);

        if (xCounter == oCounter || xCounter == oCounter + 1) {
            if (isOWon) {
                if (isXWon) {
                    throw new GameBadRequestException("Invalid board. Both players can't be winners at the same time.");
                }

                if (xCounter != oCounter) {
                    throw new GameBadRequestException("Invalid board. O player can't win, when amount of each symbols differs.");
                }
            }

            if (isXWon && xCounter != oCounter + 1) {
                throw new GameBadRequestException("Invalid board. X player can't win, if X marks amount is les equal than O.");
            }
        } else {
            throw new GameBadRequestException("Invalid board. Incorrect amount of symbols on board. Should be X = O OR X = O + 1.");
        }
    }

    private byte getSymbolCount(char[] board, char symbol) {
        byte counter = 0;
        for (int i = 0; i < MAX_NUMBER_CELLS; i++) {
            if (board[i] == symbol) {
                counter++;
            }
        }
        return counter;
    }

    private boolean checkWinner(char[] board, char boardSymbol) {
        for (int i = 0; i < MAX_NUMBER_CELLS - 1; i++) {
            if (board[winCombinationsIndexes[i][0]] == boardSymbol
                    && board[winCombinationsIndexes[i][1]] == boardSymbol
                    && board[winCombinationsIndexes[i][2]] == boardSymbol) {
                return true;
            }
        }
        return false;
    }

    private void applyCorrectStatus(Game game, boolean isXWon, boolean isOWon) {
        if (isXWon) {
            game.setStatus(GameStatus.X_WON);
        } else if(isOWon) {
            game.setStatus(GameStatus.O_WON);
        } else if (!game.getBoard().contains("-")) {
            game.setStatus(GameStatus.DRAW);
        } else {
            game.setStatus(GameStatus.RUNNING);
        }
    }

    /* Server can make moves in two ways:
        1. By checking possible win combinations like X-X, XX- etc. in rows, columns, diagonals
        and put into free cell O symbol to block win combination for X player.
        2. If there are no winning combination yet, O symbol will be placed randomly in free cell.
    */
    private void serverMakeMove(Game game) {
        char[] board = game.getBoard().toCharArray();
        int appearanceCount = 0;
        int blockWinIndex = -1;
        boolean winMoveBlocked = false;
        for (int i = 0; i < MAX_NUMBER_CELLS - 1; i++) {
            for (int j = 0; j < NUMBER_OF_COLS; j++) {
                if (board[winCombinationsIndexes[i][j]] == X_SYMBOL) {
                    appearanceCount++;
                } else {
                    blockWinIndex = board[winCombinationsIndexes[i][j]] == '-' ? winCombinationsIndexes[i][j] : blockWinIndex;
                }
            }
            if (appearanceCount == 2 && blockWinIndex > -1) {
                board[blockWinIndex] = O_SYMBOL;
                winMoveBlocked = true;
                break;
            }
            appearanceCount = 0;
            blockWinIndex = -1;
        }

        if (!winMoveBlocked) {
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < board.length; i++) {
                if (board[i] == '-') {
                    indexes.add(i);
                }
            }
            if (indexes.size() > 1) {
                Collections.shuffle(indexes);
            }
            board[indexes.get(0)] = O_SYMBOL;
        }
        if (checkWinner(board, O_SYMBOL)) {
            game.setStatus(GameStatus.O_WON);
        }
        game.setBoard(new String(board));
    }

    // Validation of input values
    private void checkInput(String board) {
        if (board.length() != MAX_NUMBER_CELLS) {
            throw new GameBadRequestException("Invalid number of symbols. Should be 9.");
        }
        String regex = "[OX-]*";
        if (!Pattern.matches(regex, board)) {
            throw new GameBadRequestException("Invalid symbols. Should be O,X or -.");
        }
    }
}
