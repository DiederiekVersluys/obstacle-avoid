package com.obstacleavoid.screen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.obstacleavoid.ObstacleAvoidGame;
import com.obstacleavoid.assets.AssetDescriptors;
import com.obstacleavoid.assets.AssetPaths;
import com.obstacleavoid.common.GameManager;
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
    private int lives = GameConfig.LIVES_START;
    private Pool<Obstacle> obstaclePool;
    private Sound hit;
    private Music music;

    private final ObstacleAvoidGame game;
    private final AssetManager assetManager;


    private final float startPlayerX = GameConfig.WORLD_WIDTH / 2f - GameConfig.PLAYER_SIZE/2;
    private final float startPlayerY = 1-GameConfig.PLAYER_SIZE/2;

    //constructor
    public GameController(ObstacleAvoidGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
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

        //sounds
        hit = assetManager.get(AssetDescriptors.HIT_SOUND);
        music = Gdx.audio.newMusic(Gdx.files.internal(AssetPaths.MUSIC_SOUND));
    }

    //public methods

    public void update(float delta) {
        if (isGameOver()) {

            return;
        }
        music.setLooping(true);
        music.play();
        updatePlayer(delta);
        updateObstacles(delta);
        updateScore(delta);
        updateDisplayScore(delta);
        if (isPlayerCollidingWithObstacle()) {
            log.debug("collision detected");
            lives--;

            if (isGameOver()) {
                music.stop();
                log.debug("Game over man! Game over!");
                GameManager.INSTANCE.updateHighScore(score);
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

    public boolean isGameOver() {
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
                hit.play();
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
            DifficultyLevel difficultyLevel = GameManager.INSTANCE.getDifficultyLevel();
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
