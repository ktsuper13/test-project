package org.game.tictactoe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (code = HttpStatus.NOT_FOUND, reason = "Game Not Found")
public class GameNotFoundException extends RuntimeException {
}
