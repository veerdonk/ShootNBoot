package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Gun {
    private Sprite gunSprite;
    private Sprite playerGunSprite;
    private GunType gunType;
    private float fireRate;
    private int damage;
    private float x;
    private float y;
    private Rectangle gunRect;
    public boolean pickedUp = false;

    public Gun(Sprite gunSprite, Sprite playerGunSprite, GunType gunType, float x, float y) {
        this.playerGunSprite = playerGunSprite;
        this.gunSprite = gunSprite;
        this.gunType = gunType;
        this.x = x;
        this.y = y;
        this.gunSprite.setPosition(x, y);
        this.gunRect = new Rectangle(x, y, 5, 5);
        switch (gunType){
            case PISTOL:
                this.fireRate = 400;
                this.damage = 20;
                break;
            case SHOTGUN:
                this.fireRate = 1000;
                this.damage = 10;
                break;
            case MACHINEGUN:
                this.fireRate = 300;
                this.damage = 18;
                break;
            case SUBMACHINE:
                this.fireRate = 250;
                this.damage = 14;
                break;
        }
    }

    public Sprite getPlayerGunSprite() {
        return playerGunSprite;
    }

    public void setPlayerGunSprite(Sprite playerGunSprite) {
        this.playerGunSprite = playerGunSprite;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Rectangle getGunRect() {
        return gunRect;
    }

    public void setGunRect(Rectangle gunRect) {
        this.gunRect = gunRect;
    }

    public Sprite getGunSprite() {
        return gunSprite;
    }

    public void setGunSprite(Sprite gunSprite) {
        this.gunSprite = gunSprite;
    }

    public GunType getGunType() {
        return gunType;
    }

    public void setGunType(GunType gunType) {
        this.gunType = gunType;
    }

    public float getFireRate() {
        return fireRate;
    }

    public void setFireRate(float fireRate) {
        this.fireRate = fireRate;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}


