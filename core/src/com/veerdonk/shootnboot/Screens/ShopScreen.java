package com.veerdonk.shootnboot.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.Player;
import com.veerdonk.shootnboot.ShootNBoot;

import java.util.HashMap;
import java.util.Map;

public class ShopScreen implements Screen {

    final ShootNBoot game;
    private final Screen parent;
    OrthographicCamera camera;
    private Player player;
    private Map<String, Gun> guns;
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    MapProperties mapProperties;

    public ShopScreen(final ShootNBoot game, Screen parent, Player player, Map<String, Gun> guns) {
        this.parent = parent;
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        this.player = player;
        this.guns = guns;

        tiledMap = new TmxMapLoader().load("shopMap.tmx");
        mapProperties = tiledMap.getProperties();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        game.batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        game.font.draw(game.batch, "Welcome to the shop", 100, 250);
        //TODO use kenney UI assets to create buttons
        //player.setWeapon(guns.get("pistol"));
        game.batch.end();
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
