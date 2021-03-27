package com.guillermo.leif.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.guillermo.leif.DropGame;

import static com.guillermo.leif.utility.Constants.VIEW_HEIGHT;
import static com.guillermo.leif.utility.Constants.VIEW_WIDTH;

public class MainMenuScreen implements Screen {
    private final DropGame game;
    private MidiDropsGameScreen midiDropsGameScreen;
    private OrthographicCamera camera;

    public MainMenuScreen(final DropGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Drop!!! ", 100, 150);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            midiDropsGameScreen = new MidiDropsGameScreen(game);
            game.setScreen(midiDropsGameScreen);
            this.dispose(); // nothing to dispose of but good habit.
        }
    }

    @Override
    public void resize(int width, int height) {
        VIEW_WIDTH = width;
        VIEW_HEIGHT = height;
    }

    @Override
    public void pause() {
        if (null != midiDropsGameScreen) {
            midiDropsGameScreen.pause();
        }
    }

    @Override
    public void resume() {
        if (null != midiDropsGameScreen) {
            midiDropsGameScreen.resume();
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
