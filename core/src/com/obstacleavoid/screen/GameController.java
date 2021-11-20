package com.obstacleavoid.screen;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.obstacleavoid.config.DifficultyLevel;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Background;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;

public class GameController {
    //constants
    private static final Logger log = new Logger(GameController.class.getName(),Logger.DEBUG);

    //attributes (fields)

    private Background background;
    private Player player;
    private Array<Obstacle> obstacles = new Array<>();
    private float obstacleTimer;
    private float scoreTimer;
    private int score;
    private int displayScore;
    private DifficultyLevel difficultyLevel = DifficultyLevel.HARD;
    private int lives = GameConfig.LIVES_START;
    private Pool<Obstacle> obstaclePool;
    private final float startPlayerX = GameConfig.WORLD_WIDTH / 2f - GameConfig.PLAYER_SIZE/2;
    private final float startPlayerY = 1-GameConfig.PLAYER_SIZE/2;

    //constructor
    public GameController() {
        init();
    }

    //init
    private void init(){
        //create player
        player = new Player();


        //position player
        player.setPosition(startPlayerX, startPlayerY);

        //create the obstacle pool
        obstaclePool = Pools.get(Obstacle.class, 40);

        //create background
        background = new Background();

        background.setPosition(0,0);
        background.setSize(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

    }

    //public methods

    public void update(float delta) {
        if (isGameOver()) {

            return;
        }
        updatePlayer(delta);
        updateObstacles(delta);
        updateScore(delta);
        updateDisplayScore(delta);
        if (isPlayerCollidingWithObstacle()) {
            log.debug("collision detected");
            lives--;

            if (isGameOver()) {
                log.debug("Game over man! Game over!");
            }
            else{
                restart();
            }
        }

    }

    private void restart() {
        obstaclePool.freeAll(obstacles);
        obstacles.clear();
        player.setPosition(startPlayerX, startPlayerY);
    }

    //private methods

    private boolean isGameOver() {
        return lives <= 0;
    }

    private void updateScore(float delta) {
        scoreTimer += delta;
        if (scoreTimer >= GameConfig.SCORE_MAX_TIME) {
            score += MathUtils.random(1, 5);
            scoreTimer = 0.0f;
        }

    }

    private void updateDisplayScore(float delta) {
        if (displayScore < score) {
            displayScore = (int) Math.min(score, displayScore + 60 * delta);
        }

    }

    private boolean isPlayerCollidingWithObstacle() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isNotHit() && obstacle.isPlayerColliding(player)) {
                return true;
            }
        }
        return false;
    }

    private void updateObstacles(float delta) {
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
        }
        createNewObstacles(delta);
        removePastObstacles();
    }

    private void removePastObstacles() {
        if(obstacles.size > 0 ){
            Obstacle first = obstacles.first();
            float minObstacleY = -GameConfig.OBSTACLE_SIZE;

            if(first.getY() < minObstacleY){
                obstacles.removeValue(first, true);
                obstaclePool.free(first);
            }
        }


    }

    private void createNewObstacles(float delta) {
        obstacleTimer += delta;

        if (obstacleTimer > GameConfig.OBSTACLE_SPAWN_TIME) {
            float min = 0;
            float max = GameConfig.WORLD_WIDTH - GameConfig.OBSTACLE_SIZE;
            float obstacleX = MathUtils.random(min, max);
            float obstacleY = GameConfig.WORLD_HEIGHT;

            Obstacle obstacle = obstaclePool.obtain();
            obstacle.setYSpeed(difficultyLevel.getObstacleSpeed());
            obstacle.setPosition(obstacleX, obstacleY);
            obstacles.add(obstacle);
            obstacleTimer = 0f;
        }
    }

    private void updatePlayer(float delta) {

        player.update();
        blockPlayerFromLeavingTheWorld();
    }

    private void blockPlayerFromLeavingTheWorld() {
        float playerX = MathUtils.clamp(player.getX(), 0, GameConfig.WORLD_WIDTH - player.getWidth());

        player.setPosition(playerX, player.getY());


    }

    public Player getPlayer() {
        return player;
    }

    public Array<Obstacle> getObstacles() {
        return obstacles;
    }

    public Background getBackground(){
        return background;
    }

    public int getDisplayScore() {
        return displayScore;
    }

    public int getLives() {
        return lives;
    }
}
