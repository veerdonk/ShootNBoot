package com.veerdonk.shootnboot.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.veerdonk.shootnboot.ShootNBoot;

public class GameOverScreen implements Screen {

    final ShootNBoot game;
    OrthographicCamera camera;

    public GameOverScreen(final ShootNBoot game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Game Over... ", 100, 150); //TODO make pretty

        game.font.draw(game.batch, "Tap anywhere to try again", 100, 100);
        game.batch.end();
        if(Gdx.input.isTouched()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
