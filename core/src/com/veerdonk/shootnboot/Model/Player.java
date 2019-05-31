package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.veerdonk.shootnboot.Controllers.CollisionController;


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

    public Player(Sprite playSprite, float playerSpeed, float initialX, float initialY, MapNode currentNode) {
        this.playerSprite = playSprite;
        playerSprite.setPosition(initialX, initialY);
        this.playerSpeed = playerSpeed;
        this.playerRect = new Rectangle(initialX, initialY, width, height);
        this.cc = new CollisionController();
        this.currentNode = currentNode;
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
    }

    public void getXp(int gottenXp){
        xp += gottenXp;
        if(xp > (level/levelConstant)*(level/levelConstant)){
            levelup();
        }
    }

    public void levelup(){
        level += 1;
        setMaxHealth(getMaxHealth() + 10);
        heal(10);
        getWeapon().setDamage(getWeapon().getDamage() + 1);
        Gdx.app.log("Ding! level", Integer.toString(level));
    }

    public void heal(int amount){
        setHealth(getHealth() + amount);
        if(getHealth() > getMaxHealth()) {
            setHealth(getMaxHealth());
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


    public void rotate(Vector2 vec) {
        playerSprite.setRotation(vec.angle());
    }

}
