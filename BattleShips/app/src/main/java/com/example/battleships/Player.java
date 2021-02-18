package com.example.battleships;

import java.io.Serializable;


public class Player implements Serializable {

    public Board board;
    public boolean isAllowedMultipleShots;

    public Player(Board board, boolean isAllowedMultipleShots){
        this.board = board;
        this.isAllowedMultipleShots = isAllowedMultipleShots;
    }

    public Vector2 onOwnTurn(){
        return null;
    }
}
