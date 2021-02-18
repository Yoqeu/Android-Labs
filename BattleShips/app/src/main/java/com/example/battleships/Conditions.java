package com.example.battleships;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Conditions {
    public int ID;

    protected Conditions(int ID) {
        this.ID = ID;
    }

    public abstract void sendPacket(ObjectOutputStream output) throws Exception;

    public static Conditions readPacket(ObjectInputStream input) throws Exception {
        int ID = input.readInt();

        if (ID == ConditionsID.HIT)
            return new ConditionsHit(input);
        else if (ID == ConditionsID.GAMEOVER)
            return new ConditionsGameover(input);


        return null;
    }
}
