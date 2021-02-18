package com.example.battleships;

public class Room {
    private String id;
    private String idCreator;
    private String idOpponent;

    private Room() {}

    public Room(String id, String idCreator) {
        this.id = id;
        this.idCreator = idCreator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCreator() {
        return idCreator;
    }

    public String getIdOpponent() {
        return idOpponent;
    }

    public void setIdOpponent(String idOpponent) {
        this.idOpponent = idOpponent;
    }
}
