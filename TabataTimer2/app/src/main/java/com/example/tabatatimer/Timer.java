package com.example.tabatatimer;

public class Timer {
    private  String title;
    private int id, prepTime, workTime, restTime, cyclesAmount, setsAmount, rest_sets, color;

    Timer(int id, String title, int prepTime, int workTime, int restTime, int cyclesAmount,
          int setsAmount, int rest_sets, int color) {
        this.id = id;
        this.title = title;
        this.prepTime = prepTime;
        this.workTime = workTime;
        this.restTime = restTime;
        this.cyclesAmount = cyclesAmount;
        this.setsAmount = setsAmount;
        this.rest_sets = rest_sets;
        this.color = color;
    }

    public void setId(int id0) {
        id = id0;
    }

    public void setTitle(String title0) {
        title = title0;
    }


    public void setColor(int color0) {
        color = color0;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public int getWorkTime() {
        return workTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public int getCyclesAmount() {
        return cyclesAmount;
    }

    public int getSetsAmount() {
        return setsAmount;
    }

    public int getRest_sets() {
        return rest_sets;
    }

    public int getColor() {
        return color;
    }

}