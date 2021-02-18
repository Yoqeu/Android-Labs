package com.example.battleships;

import android.graphics.Color;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Ship implements Serializable {
    public static List<Ship> getShips() {
        List<Ship> ships = new ArrayList<>();
        ships.add(new Ship("Aircraft Carrier", 5, Color.BLACK));
        ships.add(new Ship("Battleship", 4, Color.RED));
        ships.add(new Ship("Frigate", 3, Color.YELLOW));
        ships.add(new Ship("Submarine", 3, Color.GREEN));
        ships.add(new Ship("Minesweeper", 2, Color.MAGENTA));
        return ships;
    }

    public String name;
    public final int size;
    public int health;
    public List<Vector2> placesOwned;
    public Direction direction;
    public int preferredColor;

    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.direction = Direction.Horizontal;
        this.preferredColor = Color.GREEN;

        this.health = size;
        placesOwned = new ArrayList<>();
    }

    public Ship(String name, int size, int preferredColor) {
        this(name, size);
        this.preferredColor = preferredColor;
    }

    public void flipDirection() {
        if (direction == Direction.Horizontal)
            direction = Direction.Vertical;
        else
            direction = Direction.Horizontal;
    }

    public void reset() {
        health = size;
    }

    public void hit() {
        Log.d("Debug", toString() + ", decreasing health to " + health);
        health--;
        if (health < 0)
            health = 0;
    }

    public boolean isDestroyed() {
        return health == 0;
    }

    public String toString() {
        return String.format("{Name=%s, Size=%s, Health=%s}", name, size, health);
    }
}
