package com.example.battleships;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConditionsGameover extends Conditions{
    public boolean weWon;

    public ConditionsGameover(ObjectInputStream input) throws IOException {
        super(ConditionsID.GAMEOVER);
        weWon = input.readBoolean();
    }

    public ConditionsGameover(boolean isWin){
        super(ConditionsID.GAMEOVER);
        this.weWon = isWin;
    }

    @Override
    public void sendPacket(ObjectOutputStream output) throws IOException {
        output.writeBoolean(weWon);
    }
}
