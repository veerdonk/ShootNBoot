package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.veerdonk.shootnboot.Model.Bullet;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Pools.BulletPool;

public class Player {
    private float width = 32f;
    private float height = 32f;
    private Texture playerImage;
    private Sprite playerSprite;
    private float playerSpeed;
    private Rectangle playerRect;
    private int playerHealth;
    private Gun weapon;
    private BulletPool bp;

    public Player(Sprite playerImage, float playerSpeed, float initialX, float initialY, int playerHealth, BulletPool bp) {
        this.playerSprite = playerImage;
        playerSprite.setPosition(initialX, initialY);
        this.playerSpeed = playerSpeed;
        this.playerRect = new Rectangle(initialX, initialY, width, height);
        this.playerHealth = playerHealth;
        this.bp = bp;
    }

    public void setPosition(float x, float y){
        this.getPlayerRect().setPosition(x, y);
        this.getPlayerSprite().setPosition(x,y);
    }

    public void move(float percentX, float percentY){ //TODO add check for bounding tiles/collisions
        float newx = getX() + percentX * playerSpeed;
        float newy = getY() + percentY * playerSpeed;

        playerSprite.setPosition(newx, newy);
        playerRect.setY(newy);
        playerRect.setX(newx);

    }

    public Gun getWeapon() {
        return weapon;
    }

    public void setWeapon(Gun weapon) {
        this.weapon = weapon;
    }

    public void pickupGun(Gun gun){
        this.weapon = gun;
        this.playerSprite = gun.getPlayerGunSprite();
    }

    public float getX(){
        return playerRect.getX();
    }

    public float getY(){
        return playerRect.getY();
    }

    public Texture getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(Texture playerImage) {
        this.playerImage = playerImage;
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
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

    public void setPlayerRect(Rectangle playerRect) {
        this.playerRect = playerRect;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    public void dispose(){
        playerImage.dispose();
    }

    public void rotate(Vector2 vec) {
        playerSprite.setRotation(vec.angle());
    }
    public void shoot(Vector2 vec){
        //TODO implement
    }
}
