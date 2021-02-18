package com.example.battleships;

import java.io.Serializable;


public class Place implements Serializable {
    private Vector2 position;
    private boolean hit;
    private Ship ship;

    public enum State
    {
        Empty,
        Ship,
        Hit,
        Miss;

        State(){}
    }
    State state;

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public Place(int x, int y) {
        position = new Vector2(x, y);
        hit = false;
        ship = null;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public Ship getShip() {
        return ship;
    }

    public boolean hasShip() {
        return ship != null;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setHit(boolean isHit) {
        hit = isHit;

        if (isHit == true && ship != null) {
            ship.hit();
        }
    }

    public boolean isHit() {
        return hit;
    }

    public String toString() {
        return "{Place}";
    }
}
