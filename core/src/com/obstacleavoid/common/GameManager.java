package com.obstacleavoid.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.obstacleavoid.ObstacleAvoidGame;

public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    public static final String HIGH_SCORE_KEY = "highscore";

    private int highscore;
    private final Preferences PREFS;

    private GameManager(){
        PREFS = Gdx.app.getPreferences(ObstacleAvoidGame.class.getSimpleName());
        highscore = PREFS.getInteger(HIGH_SCORE_KEY, 0);

    }

    public void updateHighScore(int score){
        if(score<highscore){
            return;
        }
        highscore = score;
        PREFS.putInteger(HIGH_SCORE_KEY, highscore);
        PREFS.flush();
    }

    public String getHighScoreString(){
        return String.valueOf(highscore);
    }

}
