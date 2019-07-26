package com.veerdonk.shootnboot.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.Player;
import com.veerdonk.shootnboot.ShootNBoot;
import com.veerdonk.shootnboot.Util.ButtonUtil;

import java.util.Map;

import jdk.nashorn.internal.runtime.PropertyListeners;

public class ShopScreen implements Screen {

    final ShootNBoot game;
    private final Screen parent;
    private Stage stage;
    OrthographicCamera camera;
    private Player player;
    private Map<String, Gun> guns;
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    MapProperties mapProperties;
    private TextButton upgradeGuns;
    private TextButton increaseHealth;
    private TextButton increaseSpeed;
    private TextButton increaseDamage;
    private TextButton exitShop;
    private TextButton bombButton;
    private TextButton healButton;

    private int bombCost = 500;

    public ShopScreen(final ShootNBoot game, Screen parent, final Player player, final Map<String, Gun> guns) {
        this.parent = parent;
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        this.player = player;
        this.guns = guns;
        this.stage = new Stage(new FitViewport(800, 480));
        ButtonUtil bu = new ButtonUtil(game);
        tiledMap = new TmxMapLoader().load("shopMap.tmx");
        mapProperties = tiledMap.getProperties();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        final int healCost = (int) ((player.getMaxHealth() - player.getHealth()) * 2);
        exitShop = bu.getButton("Exit Shop", 1,3);
        increaseHealth = bu.getButton("Health ++: 1 Att", 2,0);
        increaseSpeed = bu.getButton("Speed ++: 1 Att", 2,1);
        increaseDamage = bu.getButton("Damage ++: 1 Att", 2, 2);
        bombButton = bu.getButton("Bomb: 500gp", 0,2);
        healButton = bu.getButton("Heal to full: " + healCost + "gp", 2,3);
        upgradeGuns = bu.getButton("Gun upgrades", 0, 1);

        stage.addActor(exitShop);
        stage.addActor(increaseHealth);
        stage.addActor(increaseDamage);
        stage.addActor(increaseSpeed);
        stage.addActor(bombButton);
        stage.addActor(healButton);
        stage.addActor(upgradeGuns);
        
        Gdx.input.setInputProcessor(stage);

        bombButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.bombs < 3 && player.getMoney() >= bombCost){
                    player.setBombs(player.bombs + 1);
                    player.sc.playDing();
                    player.setMoney(player.getMoney() - bombCost);
                }else{
                    player.sc.playError();
                }

            }
        });

        healButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.getMoney() >= healCost){
                    player.setHealth(player.getMaxHealth());
                    player.sc.playDing();
                    player.setMoney(player.getMoney() - healCost);
                }else{
                    player.sc.playError();
                }

            }
        });

        increaseHealth.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.increaseAttHealth()){
                    player.sc.playDing();
                }else{
                    player.sc.playError();
                }
            }
        });
        increaseSpeed.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.increaseAttSpeed()){
                    player.sc.playDing();
                }else{
                    player.sc.playError();
                }
            }
        });
        increaseDamage.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.increaseAttDamage()){
                    player.sc.playDing();
                }else{
                    player.sc.playError();
                }
            }
        });

        upgradeGuns.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                game.setScreen(new WeaponUpgradeScreen(game, ShopScreen.this, player, guns));
            }
        });

        exitShop.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                returnToGame();
                return true;
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        game.batch.begin();
        game.batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        stage.act();
        stage.draw();
        game.batch.end();
        game.batch.begin();
        game.font.draw(game.batch, "Level: " + Integer.toString(player.getLevel()), 50, 75);
        game.font.draw(game.batch, "Att points: " + Integer.toString(player.getAttPoints()),200, 75);
        game.font.draw(game.batch, "gold: " + Integer.toString(player.getMoney()),400, 75);

        game.batch.end();
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
        parent.show();
    }
}
