package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.veerdonk.shootnboot.Controllers.CollisionController;
import com.veerdonk.shootnboot.Controllers.SoundController;
import com.veerdonk.shootnboot.Screens.GameScreen;

import java.sql.Time;


public class Player extends Character {
    public int bombs;
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
    private int powerUpDuration = 10000;
    private boolean regenActive = false;
    private boolean quadActive = false;
    private boolean rapidActive = false;
    private boolean speedActive = false;
    private long regenActivated;
    private long quadActivated;
    private long rapidActivated;
    private long speedActivated;
    private long lastRegen = TimeUtils.millis();
    private float oldSpeed;
    private int oldDamage;
    private float oldFireRate;
    public int pistolAmmo;
    public int subAmmo;
    public int machineAmmo;
    public int shotgunAmmo;
    public Queue<Gun> weapons;


    public Player(Sprite playSprite, float playerSpeed, float initialX, float initialY, MapNode currentNode, SoundController sc) {
        this.playerSprite = playSprite;
        playerSprite.setPosition(initialX, initialY);
        this.playerSpeed = playerSpeed;
        this.playerRect = new Rectangle(initialX, initialY, width, height);
        this.cc = new CollisionController();
        this.currentNode = currentNode;
        this.sc = sc;
        this.oldSpeed = playerSpeed;
        this.weapons = new Queue<>();
        pistolAmmo = 100;
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
                Gdx.app.log("powerup", "regen stopped");
            }
        }
        if(quadActive) {
            if (TimeUtils.millis() - quadActivated > powerUpDuration) {
                this.weapon.setDamage(oldDamage);
                quadActive = false;
                Gdx.app.log("powerup", "quad stopped");
            }
        }
        if(speedActive){
            if(TimeUtils.millis() - speedActivated > powerUpDuration){
                playerSpeed = oldSpeed;
                speedActive = false;
                Gdx.app.log("powerup", "speed stopped");
            }
        }
        if(rapidActive){
            if(TimeUtils.millis() - rapidActivated > powerUpDuration){
                this.weapon.setFireRate(oldFireRate);
                rapidActive = false;
                Gdx.app.log("powerup", "rapid stopped");
            }
        }
    }

    public Gun getWeapon() {
        return weapon;
    }

    public void setWeapon(Gun newWeapon) {
        if(weapon != null){
            boolean alreadyHave = false;
            for(Gun g : weapons){
                if(weapon.getGunType() == g.getGunType()){
                alreadyHave = true;
                }
            }
            if(!alreadyHave) {
                this.weapons.addLast(weapon);
            }
        }
        this.weapon = newWeapon;
        this.playerSprite = weapon.getPlayerGunSprite();
        this.weapon.setDamage(this.weapon.getDamage() + damage);
        this.oldDamage = weapon.getDamage();
        this.oldFireRate = weapon.getFireRate();
    }

    public void switchWeapon(){
        Gun nextWeapon = weapons.removeFirst();
        this.weapons.addLast(weapon);
        this.weapon = nextWeapon;
        this.playerSprite = nextWeapon.getPlayerGunSprite();
        this.oldDamage = nextWeapon.getDamage();
        this.oldFireRate = nextWeapon.getFireRate();
        Gdx.app.log("switched to: ", nextWeapon.toString());
        Gdx.app.log("weapong queue: ", weapons.toString());

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
            oldSpeed = playerSpeed;
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
            oldDamage = this.weapon.getDamage();
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
            if(weapon != null) {
                quadActive = true;
//                oldDamage = this.weapon.getDamage();
                this.weapon.setDamage(this.weapon.getDamage() * 4);
                quadActivated = TimeUtils.millis();
                Gdx.app.log("powerup", "quad");
            }
        }else if(type == PowerUPType.RAPIDFIRE){
            if(weapon != null) {
                rapidActive = true;
//            oldFireRate = this.weapon.getFireRate();
                this.weapon.setFireRate(this.weapon.getFireRate() / 2);
                rapidActivated = TimeUtils.millis();
                Gdx.app.log("powerup", "rapid fire");
            }
        }else if(type == PowerUPType.SPEED){
            speedActive = true;
//            oldSpeed = playerSpeed;
            this.setPlayerSpeed((float) (playerSpeed*1.5));
            speedActivated = TimeUtils.millis();
            Gdx.app.log("powerup", "speed");

        }else if(type == PowerUPType.REGEN){
            regenActive = true;
            regenActivated = TimeUtils.millis();
            Gdx.app.log("powerup", "regen");
        }
    }

    public int getBombs() {
        return bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }

    public float getX(){
        return playerRect.getX();
    }

    public float getY(){
        return playerRect.getY();
    }

    public Vector2 getVector2(){
        return new Vector2(playerRect.getX(), playerRect.getY());
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

    public boolean isRegenActive() {
        return regenActive;
    }

    public boolean isQuadActive() {
        return quadActive;
    }

    public boolean isRapidActive() {
        return rapidActive;
    }

    public boolean isSpeedActive() {
        return speedActive;
    }

    public void increaseAmmo(GunType ammoType) {
        switch(ammoType){
            case PISTOL:
                pistolAmmo += 50;
                break;
            case SUBMACHINE:
                subAmmo += 50;
                break;
            case MACHINEGUN:
                machineAmmo += 30;
                break;
            case SHOTGUN:
                shotgunAmmo += 15;
                break;
        }
    }

    public void useAmmo(GunType gt){
        switch(gt){
            case PISTOL:
                pistolAmmo -= 1;
                break;
            case SUBMACHINE:
                subAmmo -= 1;
                break;
            case MACHINEGUN:
                machineAmmo -= 1;
                break;
            case SHOTGUN:
                shotgunAmmo -= 1;
                break;
        }
    }
    public int getAmmo(GunType gt){
        switch(gt){
            case PISTOL:
                return pistolAmmo;
            case SUBMACHINE:
                return subAmmo;
            case MACHINEGUN:
                return machineAmmo;
            case SHOTGUN:
                return shotgunAmmo;
            default:
                return 0;
        }
    }
}
