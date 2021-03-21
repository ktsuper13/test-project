package org.game.tictactoe.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameController {

    @ApiOperation (
            value = "Get a game."
    )
    @RequestMapping (
            value = "/game",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    public String game(@RequestParam String board, @RequestParam(name = "game_id") String gameId) {
        return "game";
    }
}
