package org.game.tictactoe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties (ignoreUnknown = true)
public class Location {
    @JsonProperty("location")
    private String location;

    public Location setLocation(String location) {
        this.location = location;
        return this;
    }
}
