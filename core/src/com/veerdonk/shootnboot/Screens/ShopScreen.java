package com.veerdonk.shootnboot.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.TimeUtils;
import com.veerdonk.shootnboot.ShootNBoot;

public class ShopScreen implements Screen {

    final ShootNBoot game;
    private final Screen parent;
    OrthographicCamera camera;

    public ShopScreen(final ShootNBoot game, Screen parent) {
        this.parent = parent;
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //TODO implement shop logic/view
        returnToGame();
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

    public void returnToGame(){
        game.setScreen(parent);
        parent.render(Gdx.graphics.getDeltaTime());
    }
}
