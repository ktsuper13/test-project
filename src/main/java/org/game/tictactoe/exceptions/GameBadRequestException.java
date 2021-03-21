package org.game.tictactoe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (code = HttpStatus.BAD_REQUEST, reason = "Bad Request")
public class GameBadRequestException extends RuntimeException {

    public GameBadRequestException(String message) {
        super(message);
    }
}
