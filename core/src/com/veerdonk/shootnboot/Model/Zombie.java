package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.veerdonk.shootnboot.ShootNBoot;

import java.util.Objects;

public class Zombie extends Character implements Pool.Poolable {
    private float width = 15f;
    private float height = 15f;
    private Sprite zombieSprite;
    private float zombieSpeed;
    private Rectangle zombieRect;
    private int damage;
    private int xpValue = 30;

    public void sendZombie(Sprite zombieSprite, float zombieSpeed, float initialX, float initialY, int damage) {
        this.zombieSprite = zombieSprite;
        zombieSprite.setPosition(initialX, initialY);
        this.zombieSpeed = zombieSpeed;
        this.zombieRect = new Rectangle(initialX, initialY, width, height);
        this.damage = damage;
        this.setHitFreq(5);
    }

    public float getX(){
        return zombieRect.getX();
    }

    public float getY(){
        return zombieRect.getY();
    }


    public void move(float endX, float endY){
        float destX = endX - getX();
        float destY = endY - getY();
        float dist = (float) Math.sqrt(destX * destX + destY * destY);
        destX = destX / dist;
        destY = destY / dist;
        zombieRect.setX(getX() + destX * zombieSpeed);
        zombieRect.setY(getY() + destY * zombieSpeed);
        zombieSprite.setPosition(getX(), getY());
        zombieSprite.setRotation(
                (float) ShootNBoot.getAngle(
                        endX,
                        endY,
                        getX(),
                        getY()
                ) + 180f);
    }

    public Rectangle getZombieRect() {
        return zombieRect;
    }

    public void setZombieRect(Rectangle zombieRect) {
        this.zombieRect = zombieRect;
    }

    public Sprite getZombieSprite() {
        return zombieSprite;
    }

    public void setZombieSprite(Sprite zombieSprite) {
        this.zombieSprite = zombieSprite;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getXpValue() {
        return xpValue;
    }

    public void setXpValue(int xpValue) {
        this.xpValue = xpValue;
    }

    @Override
    public void reset() {
        this.zombieRect = null;
        this.zombieSprite = null;
        this.setHealth(100);
    }

    public void recoil(Vector2 hurtDir) {
        zombieRect.x += hurtDir.x;
        zombieRect.y += hurtDir.y;
        zombieSprite.setPosition(zombieSprite.getX() + hurtDir.x, zombieSprite.getY() + hurtDir.y);
    }

    @Override
    public String toString() {
        return "Zombie{" +
                "zombieRect=" + zombieRect +
                '}';
    }
}
