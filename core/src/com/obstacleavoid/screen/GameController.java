package com.obstacleavoid.screen;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.obstacleavoid.config.DifficultyLevel;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;

public class GameController {
    //constants
    private static final Logger log = new Logger(GameController.class.getName(),Logger.DEBUG);

    //attributes (fields)

    private Player player;
    private Array<Obstacle> obstacles = new Array<>();
    private float obstacleTimer;
    private float scoreTimer;
    private int score;
    private int displayScore;
    private DifficultyLevel difficultyLevel = DifficultyLevel.HARD;
    private int lives = GameConfig.LIVES_START;

    //constructor
    public GameController() {
    }

    //init
    private void init(){
        //create player
        player = new Player();


        float startPlayerX = GameConfig.WORLD_WIDTH / 2f;
        float startPlayerY = 1;


        player.setPosition(startPlayerX, startPlayerY);

    }
}
