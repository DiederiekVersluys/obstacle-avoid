package com.obstacleavoid;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.obstacleavoid.assets.AssetDescriptors;
import com.obstacleavoid.assets.AssetPaths;
import com.obstacleavoid.screen.game.GameScreen;
import com.obstacleavoid.screen.loading.LoadingScreen;

public class ObstacleAvoidGame extends Game {

	private AssetManager assetManager;
	private SpriteBatch batch;
	private Music music;


	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		assetManager = new AssetManager();
		assetManager.getLogger().setLevel(Logger.DEBUG);

		batch = new SpriteBatch();
		music = Gdx.audio.newMusic(Gdx.files.internal(AssetPaths.MUSIC_SOUND));
		music.setLooping(true);
		music.setVolume(1);
		music.play();

		setScreen(new LoadingScreen(this));
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		batch.dispose();

	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
