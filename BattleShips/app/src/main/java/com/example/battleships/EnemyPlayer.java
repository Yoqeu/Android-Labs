package com.example.battleships;

public class EnemyPlayer extends Player{
    public EnemyPlayer(Board board, boolean isAllowedMultipleShots) {
        super(board, isAllowedMultipleShots);
    }

    @Override
    public Vector2 onOwnTurn() {
        return super.onOwnTurn();
    }
}

