package com.obstacleavoid.screen.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.obstacleavoid.assets.AssetDescriptors;
import com.obstacleavoid.assets.RegionNames;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Background;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;
import com.obstacleavoid.util.GdxUtils;
import com.obstacleavoid.util.ViewportUtils;
import com.obstacleavoid.util.debug.DebugCameraController;

public class GameRenderer implements Disposable {

    //attributes
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private OrthographicCamera hudCamera;
    private Viewport hudViewport;
    private SpriteBatch batch;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private DebugCameraController debugCameraController;
    private final GameController gameController;
    private final AssetManager assetManager;
    private TextureRegion playerRegion;
    private TextureRegion obstacleRegion;
    private TextureRegion backgroundRegion;


    //constructor


    public GameRenderer(AssetManager assetManager, GameController gameController) {
        this.assetManager = assetManager;
        this.gameController = gameController;

        init();
    }

    private void init() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, hudCamera);
        batch = new SpriteBatch();
        font = assetManager.get(AssetDescriptors.FONT);

        //create CameraDebugController
        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(GameConfig.WORLD_CENTER_X, GameConfig.WORLD_CENTER_Y);

        //giving things a texture
        TextureAtlas gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);

        playerRegion = gamePlayAtlas.findRegion(RegionNames.PLAYER);
        obstacleRegion = gamePlayAtlas.findRegion(RegionNames.OBSTACLE);
        backgroundRegion = gamePlayAtlas.findRegion(RegionNames.BACKGROUND);
    }

    //public methods

    public void render(float delta) {
        batch.totalRenderCalls = 0;

        //not wrapped inside 'alive' so I retain control of the camera
        debugCameraController.handleDebugInput(delta);
        debugCameraController.applyTo(camera);


        // clear screen
        GdxUtils.clearScreen();


        //render gameplay
        renderGamePlay();

        //render ui/hud
        renderUi();


        //render debug graphics
        renderDebug();
        System.out.println("total render calls = " + batch.totalRenderCalls);
    }


    @Override
    public void dispose() {
        renderer.dispose();
        batch.dispose();


    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
        ViewportUtils.debugPixelPerUnit(viewport);
    }

    //private methods

    private void renderGamePlay() {
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //draw background
        Background background = gameController.getBackground();
        batch.draw(backgroundRegion, background.getX(), background.getY(), background.getWidth(), background.getHeight());

        //draw player
        Player player = gameController.getPlayer();
        batch.draw(playerRegion, player.getX(), player.getY(), player.getWidth(), player.getHeight());

        //draw obstacles
        for (Obstacle obstacle : gameController.getObstacles()) {
            batch.draw(obstacleRegion, obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());


        }
        batch.end();


    }


    private void renderUi() {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        String livesText = "LIVES= " + gameController.getLives();
        layout.setText(font, livesText);
        font.draw(batch, livesText, 20, GameConfig.HUD_HEIGHT - layout.height);

        String scoreText = "SCORE= " + gameController.getDisplayScore();
        layout.setText(font, scoreText);
        font.draw(batch, scoreText, GameConfig.HUD_WIDTH - layout.width - 20, GameConfig.HUD_HEIGHT - layout.height);

        batch.end();
    }

    private void renderDebug() {
        viewport.apply();

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();
        renderer.end();

        ViewportUtils.drawGrid(viewport, renderer);
    }

    private void drawDebug() {
        gameController.getPlayer().drawDebug(renderer);
        for (Obstacle obstacle : gameController.getObstacles()) {
            obstacle.drawDebug(renderer);
        }


    }
}
