package com.veerdonk.shootnboot.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.Player;
import com.veerdonk.shootnboot.ShootNBoot;
import com.veerdonk.shootnboot.Util.ButtonUtil;

import java.util.Map;

public class WeaponUpgradeScreen implements Screen {

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
    private TextButton backToShop;

    private TextButton increasePistolDamage;
    private TextButton increaseSubmachineDamage;
    private TextButton increaseMachineDamage;
    private TextButton increaseShotgunDamage;

    private TextButton pistolAmmo;
    private TextButton submachineAmmo;
    private TextButton machineAmmo;
    private TextButton shotgunAmmo;

    private int pistolCost = 50;
    private int subCost = 1;
    private int machineCost = 1;
    private int shotgunCost = 1;

    private int pistolAmmoCost = 50; //TODO create maxammo(and buy to max)
    private int subAmmoCost = 50;
    private int machineAmmoCost = 70;
    private int shotgunAmmoCost = 70;

    public WeaponUpgradeScreen(final ShootNBoot game, Screen parent, final Player player, final Map<String, Gun> guns) {
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


        pistolButton = bu.getButton("Pistol: 50gp", 0,0);
        submachineButton = bu.getButton("SMG: 80gp", 1,0);
        machineButton = bu.getButton("LMG: 120gp", 2,0);
        shotGunButton = bu.getButton("Shotgun: 150gp", 3,0);

        increasePistolDamage = bu.getButton("Pistol damage ++: 1 Att", 0, 1);
        increaseSubmachineDamage = bu.getButton("SMG damage ++: 1 Att", 1, 1);
        increaseMachineDamage = bu.getButton("LMG damage ++: 1 Att", 2, 1);
        increaseShotgunDamage = bu.getButton("Shotgun damage ++: 1 Att", 3, 1);

        pistolAmmo = bu.getButton("Pistol ammo: " + pistolAmmoCost, 0, 2);
        submachineAmmo = bu.getButton("SMG ammo: " + subAmmoCost, 1, 2);
        machineAmmo = bu.getButton("LMG ammo: " + machineAmmo, 2, 2);
        shotgunAmmo = bu.getButton("Shotgun ammo: " + shotgunAmmo, 3, 2);

        backToShop = bu.getButton("Back to Shop", 0,3);


        stage.addActor(shotGunButton);
        stage.addActor(pistolButton);
        stage.addActor(submachineButton);
        stage.addActor(machineButton);

        stage.addActor(increasePistolDamage);
        stage.addActor(increaseSubmachineDamage);
        stage.addActor(increaseMachineDamage);
        stage.addActor(increaseShotgunDamage);

        stage.addActor(pistolAmmo);
        stage.addActor(submachineAmmo);
        stage.addActor(machineAmmo);
        stage.addActor(shotgunAmmo);

        stage.addActor(backToShop);

        Gdx.input.setInputProcessor(stage);


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
                    player.pistolAmmo += 100;
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
                    player.subAmmo += 50;
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
                    player.machineAmmo += 30;
                }else{
                    player.sc.playError();
                }

            }
        });

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
                    player.shotgunAmmo += 15;
                }else{
                    player.sc.playError();
                }

            }
        });

        pistolAmmo.addListener(new InputListener(){
           @Override
           public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
               return true;
           }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button){

               if(player.getMoney() >= pistolAmmoCost){
                   player.sc.playDing();
                   player.pistolAmmo += 50;
               }else{
                   player.sc.playError();
               }

            }
        });

        submachineAmmo.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button){
                if(player.getMoney() >= subAmmoCost){
                    player.sc.playDing();
                    player.subAmmo += 50;
                }else{
                    player.sc.playError();
                }
            }
        });

        machineAmmo.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button){
                if(player.getMoney() >= machineAmmoCost){
                    player.sc.playDing();
                    player.machineAmmo += 30;
                }else{
                    player.sc.playError();
                }
            }
        });

        shotgunAmmo.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button){
                if(player.getMoney() >= shotgunAmmoCost){
                    player.sc.playDing();
                    player.shotgunAmmo += 15;
                }else{
                    player.sc.playError();
                }
            }
        });





        backToShop.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                return true;
            }
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                returnToShop();
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

    private void returnToShop() {
        game.setScreen(parent);
        parent.show();
    }
}
