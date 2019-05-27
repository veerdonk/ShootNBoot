package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.veerdonk.shootnboot.Controllers.Player;

public class Gun {
    private Sprite gunSprite;
    private Sprite playerGunSprite;
    private GunType gunType;
    private float fireRate;
    private int damage;
    private float x;
    private float y;
    private Rectangle gunRect;

    public Gun(Sprite gunSprite, Sprite playerGunSprite, GunType gunType, float x, float y) {
        this.playerGunSprite = playerGunSprite;
        this.gunSprite = gunSprite;
        this.gunType = gunType;
        this.gunSprite.setPosition(x, y);
        this.gunRect = new Rectangle(x, y, 5, 5);
        switch (gunType){
            case PISTOL:
                this.fireRate = 5;
                this.damage = 5;
                break;
            case SHOTGUN:
                this.fireRate = 2;
                this.damage = 2;
                break;
            case MACHINEGUN:
                this.fireRate = 10;
                this.damage = 8;
                break;
            case SUBMACHINE:
                this.fireRate = 15;
                this.damage = 3;
                break;
        }
    }

    public Sprite getPlayerGunSprite() {
        return playerGunSprite;
    }

    public void setPlayerGunSprite(Sprite playerGunSprite) {
        this.playerGunSprite = playerGunSprite;
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


