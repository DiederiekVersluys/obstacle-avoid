package com.obstacleavoid.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class GdxUtils {

    private GdxUtils() {

    }


    public static void clearScreen(Color color) {
        //clear screen
        //DRY code= don't repeat yourself
        //WET code= Write Everything Twice (Waste Everyone's Time)
        Gdx.gl.glClearColor(color.r,color.g,color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void clearScreen(){
        clearScreen(Color.BLACK);
    }
}
