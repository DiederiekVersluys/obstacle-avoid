package com.obstacleavoid.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Pool;
import com.obstacleavoid.config.GameConfig;

public class Obstacle extends GameObjectBase implements Pool.Poolable {




    private boolean hit;



    private float ySpeed = GameConfig.MEDIUM_OBSTACLE_SPEED;


    public Obstacle() {


        super(GameConfig.OBSTACLE_BOUNDS_RADIUS);
        setSize(GameConfig.OBSTACLE_SIZE, GameConfig.OBSTACLE_SIZE);
    }


    public void update() {
        setY(getY() - ySpeed);

    }




    public boolean isPlayerColliding(Player player){
        Circle playerBounds = player.getBounds();
        //check if playerBounds overlap obstacle bounds
        boolean overlaps = Intersector.overlaps(playerBounds, getBounds());
        hit = overlaps;
//        this is the same as:
//        if(overlaps){
//            hit = true;
//        }

        return overlaps;
    }

    public boolean isNotHit(){
        return !hit;
    }

    public void setYSpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    @Override
    public void reset() {
        hit = false;
    }
}
