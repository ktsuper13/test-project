package org.game.tictactoe.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.game.tictactoe.exceptions.GameBadRequestException;
import org.game.tictactoe.exceptions.GameNotFoundException;
import org.game.tictactoe.model.Game;
import org.game.tictactoe.model.Location;
import org.game.tictactoe.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;

@RestController
@RequestMapping("api/v1/")
public class TicTacToeController {

    private GameService gameService;

    @Inject
    public TicTacToeController(GameService gameService) {
        this.gameService = gameService;
    }

    @ApiOperation (
            value = "Get all games."
    )
    @RequestMapping (
            value = "/games",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseStatus (HttpStatus.OK)
    public Game[] getAllGames() {
        return gameService.getGames();
    }

    @ApiOperation(
            value = "Start a new game."
    )
    @RequestMapping(
            value = "/games",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = "application/json"
    )
    @ResponseStatus (HttpStatus.CREATED)
    public Location startNewGame(
            @RequestBody Game game) {

        Game createdGame;
        try {
            createdGame = gameService.createGame(game);
        } catch (GameBadRequestException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        Location location = new Location();
        location.setLocation("http://localhost:2180/game?board=" + createdGame.getBoard() + "&game_id=" + createdGame.getId());
        return location;
    }

    @ApiOperation(
            value = "Get a game by ID."
    )
    @RequestMapping(
            value = "/games/{game_id}",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseStatus (HttpStatus.OK)
    public Game getGameById(
            @ApiParam(required = true, value = "ID of the Game to get")
            @PathVariable ("game_id")
                    String gameId) {
        try {
            return gameService.getGameById(gameId);
        } catch (GameNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Game Not Found");
        }
    }

    @ApiOperation(
            value = "Post a new move to a game."
    )
    @RequestMapping(
            value = "/games/{game_id}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = "application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    public Game makeMove(
            @ApiParam(required = true, value = "ID of the Game to be updated") @PathVariable("game_id")
                    String gameId,
            @ApiParam(required = true, value = "A game body to update existing game") @RequestBody
                    Game game) {
        try {
            return gameService.makeMove(gameId, game);
        } catch (GameNotFoundException notFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Game Not Found");
        } catch (GameBadRequestException badRequest) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, badRequest.getMessage());
        }
    }

    @ApiOperation(value = "Delete a game.")
    @RequestMapping(
            value = "/games/{game_id}",
            method = RequestMethod.DELETE,
            produces = "application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    public void deleteGame(
            @ApiParam(required = true, value = "ID of the Game to be deleted") @PathVariable("game_id")
                    String gameId) {
        try {
            gameService.deleteGame(gameId);
        } catch (GameNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Game Not Found");
        }
    }
}
