package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.veerdonk.shootnboot.ShootNBoot;

public class Zombie extends Character{
    private float width = 32f;
    private float height = 32f;
    private Texture zombieImage;
    private Sprite zombieSprite;
    private float zombieSpeed;
    private Rectangle zombieRect;
    private int zombieHealth;

    public Zombie(Texture zombieImage, float zombieSpeed, float initialX, float initialY, int zombieHealth) {
        this.zombieImage = zombieImage;
        this.zombieSprite = new Sprite(zombieImage);
        zombieSprite.setPosition(initialX, initialY);
        this.zombieSpeed = zombieSpeed;
        this.zombieRect = new Rectangle(initialX, initialY, width, height);
        this.zombieHealth = zombieHealth;
    }

    public float getX(){
        return zombieRect.getX();
    }

    public float getY(){
        return zombieRect.getY();
    }


    public void move(Vector2 playerPos){
        float destX = playerPos.x - getX();
        float destY = playerPos.y - getY();
        float dist = (float) Math.sqrt(destX * destX + destY * destY);
        destX = destX / dist;
        destY = destY / dist;
        zombieRect.setX(getX() + destX * zombieSpeed);
        zombieRect.setY(getY() + destY * zombieSpeed);
        zombieSprite.setPosition(getX(), getY());
        zombieSprite.setRotation(
                (float) ShootNBoot.getAngle(
                        playerPos.x,
                        playerPos.y,
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
}
