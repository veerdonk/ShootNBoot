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
    private TextButton shotGunButton;
    private TextButton pistolButton;
    private TextButton submachineButton;
    private TextButton machineButton;
    private TextButton increaseHealth;
    private TextButton increaseSpeed;
    private TextButton increaseDamage;
    private TextButton exitShop;
    private TextButton bombButton;
    private TextButton healButton;

    private int pistolCost = 50;
    private int subCost = 80;
    private int machineCost = 120;
    private int shotgunCost = 150;
    private int bombCost = 500;

    public ShopScreen(final ShootNBoot game, Screen parent, final Player player, final Map<String, Gun> guns) {
        this.parent = parent;
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        this.player = player;
        this.guns = guns;
        this.stage = new Stage(new FitViewport(800, 480));

        tiledMap = new TmxMapLoader().load("shopMap.tmx");
        mapProperties = tiledMap.getProperties();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        final int healCost = (int) ((player.getMaxHealth() - player.getHealth()) * 2);

        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/button_blue.atlas"));
        Skin skin = new Skin();
        skin.addRegions(buttonAtlas);
        TextButtonStyle style = new TextButtonStyle();
        style.up = skin.getDrawable("blue_button_up");
        style.down = skin.getDrawable("blue_button_down");
        style.font = game.font;
        shotGunButton = getButton(style, "Shotgun: 150gp", 0,0);
        pistolButton = getButton(style, "Pistol: 50gp", 1,0);
        submachineButton = getButton(style, "Sub-machine gun: 80gp", 0,1);
        machineButton = getButton(style, "Machine gun: 120gp", 1,1);
        exitShop = getButton(style, "Exit Shop", 1,3);
        increaseHealth = getButton(style, "Health ++: 1 Att", 2,0);
        increaseSpeed = getButton(style, "Speed ++: 1 Att", 2,1);
        increaseDamage = getButton(style, "Damage ++: 1 Att", 2, 2);
        bombButton = getButton(style, "Bomb: 500gp", 0,2);
        healButton = getButton(style, "Heal to full: " + healCost + "gp", 1,2);

        stage.addActor(shotGunButton);
        stage.addActor(pistolButton);
        stage.addActor(submachineButton);
        stage.addActor(machineButton);
        stage.addActor(exitShop);
        stage.addActor(increaseHealth);
        stage.addActor(increaseDamage);
        stage.addActor(increaseSpeed);
        stage.addActor(bombButton);
        stage.addActor(healButton);
        
        Gdx.input.setInputProcessor(stage);

        shotGunButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.getMoney() >= shotgunCost){
                    player.setWeapon(guns.get("shotgun"));
                    player.sc.playDing();
                    player.setMoney(player.getMoney() - shotgunCost);
                }else{
                    player.sc.playError();
                }

            }
        });

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

        submachineButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.getMoney() >= subCost){
                    player.setWeapon(guns.get("submachine"));
                    player.sc.playDing();
                    player.setMoney(player.getMoney() - subCost);
                 }else{
                    player.sc.playError();
                 }

            }
        });
        machineButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.getMoney() >= machineCost){
                    player.setWeapon(guns.get("machine"));
                    player.sc.playDing();
                    player.setMoney(player.getMoney() - machineCost);
                }else{
                    player.sc.playError();
                }

            }
        });
        pistolButton.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                if(player.getMoney() >= pistolCost){
                    player.setWeapon(guns.get("pistol"));
                    player.sc.playDing();
                    player.setMoney(player.getMoney() - pistolCost);
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

    public TextButton getButton(TextButtonStyle style, String text, int col, int row){
        TextButton button = new TextButton(text, style);
        float xpos = 0;
        float ypos = 0;
        switch(col){
            case 0:
                xpos = 50;
                break;
            case 1:
                xpos = 300;
                break;
            case 2:
                xpos = 550;
                break;
        }

        switch(row){
            case 0:
                ypos = 375;
                break;
            case 1:
                ypos = 275;
                break;
            case 2:
                ypos = 175;
                break;
            case 3:
                ypos = 85;
                break;
        }
        button.setPosition(xpos, ypos);
        return button;
    }

    public void returnToGame(){
        game.setScreen(parent);
        parent.show();
    }
}
