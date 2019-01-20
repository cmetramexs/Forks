package com.example.celineyee.forks;

public class Player {
    private String name;
    private Integer score;

    public Player(){
    }

    public Player(String name){
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }
    public Integer getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
}
