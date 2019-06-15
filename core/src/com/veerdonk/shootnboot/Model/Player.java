package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.veerdonk.shootnboot.Controllers.CollisionController;
import com.veerdonk.shootnboot.Controllers.SoundController;
import com.veerdonk.shootnboot.Screens.GameScreen;

import java.sql.Time;


public class Player extends Character {
    private float width = 32f;
    private float height = 32f;
    private Sprite playerSprite;
    private float playerSpeed;
    private Rectangle playerRect;
    private Gun weapon;
    private CollisionController cc;
    public MapNode currentNode;
    private float levelConstant = 0.2f;
    private int xp = 0;
    private int level = 1;
    private int money = 0;
    private int attPoints = 0;
    public SoundController sc;
    private int damage;
    private int powerUpDuration;
    private boolean regenActive = false;
    private boolean quadActive = false;
    private boolean rapidActive =false;
    private boolean speedActive =false;
    private long regenActivated;
    private long quadActivated;
    private long rapidActivated;
    private long speedActivated;
    private long lastRegen = TimeUtils.millis();
    private float oldSpeed;
    private int oldDamage;
    private float oldFireRate;


    public Player(Sprite playSprite, float playerSpeed, float initialX, float initialY, MapNode currentNode, SoundController sc) {
        this.playerSprite = playSprite;
        playerSprite.setPosition(initialX, initialY);
        this.playerSpeed = playerSpeed;
        this.playerRect = new Rectangle(initialX, initialY, width, height);
        this.cc = new CollisionController();
        this.currentNode = currentNode;
        this.sc = sc;
    }

    public void setPosition(float x, float y){
        this.getPlayerRect().setPosition(x, y);
        this.getPlayerSprite().setPosition(x,y);
    }

    public void move(float percentX, float percentY, Array<MapNode> collisionMapnodes){
        float endx = getX() + percentX * playerSpeed;
        float endy = getY() + percentY * playerSpeed;

        cc.checkCharacter(playerRect, playerSprite, percentX, percentY, collisionMapnodes, endx, endy);
        if(regenActive && TimeUtils.millis() - lastRegen > 1000){
            heal(20);
            lastRegen = TimeUtils.millis();
            if(TimeUtils.millis() - regenActivated > powerUpDuration){
                regenActive = false;
            }
        }
        if(quadActive) {
            if (TimeUtils.millis() - quadActivated > powerUpDuration) {
                this.weapon.setDamage(oldDamage);
            }
        }
        if(speedActive){
            if(TimeUtils.millis() - speedActivated > powerUpDuration){
                playerSpeed = oldSpeed;
            }
        }
        if(rapidActive){
            if(TimeUtils.millis() - rapidActivated > powerUpDuration){
                this.weapon.setFireRate(oldFireRate);
            }
        }
    }

    public Gun getWeapon() {
        return weapon;
    }

    public void setWeapon(Gun weapon) {
        this.weapon = weapon;
        this.playerSprite = weapon.getPlayerGunSprite();
        this.weapon.setDamage(this.weapon.getDamage() + damage);
    }

    public boolean getXp(int gottenXp){
        boolean leveledup = false;
        xp += gottenXp;
        if(xp > (level/levelConstant)*(level/levelConstant)){
            levelup();
            leveledup = true;
        }
        return leveledup;
    }

    public void levelup(){
        level += 1;
        heal(10);
        attPoints += 1;
        Gdx.app.log("Ding! level", Integer.toString(level));
    }

    public boolean increaseAttHealth(){
        if(attPoints > 0){
            setMaxHealth(getMaxHealth() + getMaxHealth()/100*10);
            heal(getMaxHealth()/100*10);
            attPoints --;
            return true;
        }else{
            return false;
        }
    }

    public boolean increaseAttSpeed(){
        if(attPoints > 0){
            playerSpeed += 0.5f;
            attPoints --;
            return true;
        }else{
            return false;
        }
    }

    public boolean increaseAttDamage(){
        if(attPoints > 0){
            damage += 3;
            attPoints --;
            this.weapon.setDamage(this.weapon.getDamage() + 3);
            return true;
        }else{
            return false;
        }
    }

    public void heal(int amount){
        this.setHealth(this.getHealth() + amount);
        if(getHealth() > getMaxHealth()) {
            this.setHealth(getMaxHealth());
        }
    }

    public void getPowerUp(PowerUPType type){
        if(type == PowerUPType.QUAD){
            quadActive = true;
            oldDamage = this.weapon.getDamage();
            this.weapon.setDamage(this.weapon.getDamage()*4);
            quadActivated = TimeUtils.millis();
        }else if(type == PowerUPType.RAPIDFIRE){
            rapidActive = true;
            oldFireRate = this.weapon.getFireRate();
            this.weapon.setFireRate(this.weapon.getFireRate()/2);
            rapidActivated = TimeUtils.millis();
        }else if(type == PowerUPType.SPEED){
            speedActive = true;
            oldSpeed = playerSpeed;
            this.setPlayerSpeed((float) (playerSpeed*1.25));
            speedActivated = TimeUtils.millis();
        }else if(type == PowerUPType.REGEN){
            regenActive = true;
            regenActivated = TimeUtils.millis();
        }
    }

    public float getX(){
        return playerRect.getX();
    }

    public float getY(){
        return playerRect.getY();
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public float getPlayerSpeed() {
        return playerSpeed;
    }

    public void setPlayerSpeed(float playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    public Rectangle getPlayerRect() {
        return playerRect;
    }

    public int getLevel() {
        return level;
    }

    public void rotate(Vector2 vec) {
        playerSprite.setRotation(vec.angle());
    }

    public int getAttPoints() {
        return attPoints;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
