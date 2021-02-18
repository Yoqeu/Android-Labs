package com.example.battleships;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConditionsHit extends Conditions{
    public int X;
    public int Y;

    public ConditionsHit(ObjectInputStream input) throws IOException {
        super(ConditionsID.HIT);
        X = input.readInt();
        Y = input.readInt();
    }

    public ConditionsHit(int X, int Y) {
        super(ConditionsID.HIT);
        this.X = X;
        this.Y = Y;
    }

    @Override
    public void sendPacket(ObjectOutputStream output) throws IOException {
        output.writeInt(X);
        output.writeInt(Y);
    }

    @Override
    public String toString(){
        return "{ConditionsHit, X=" + X +", Y=" + Y + "}";
    }
}
