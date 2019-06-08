package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.veerdonk.shootnboot.Controllers.CollisionController;
import com.veerdonk.shootnboot.Controllers.SoundController;
import com.veerdonk.shootnboot.Screens.GameScreen;


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
