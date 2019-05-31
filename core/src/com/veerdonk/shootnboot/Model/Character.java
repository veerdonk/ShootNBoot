package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class Character {
    private int health = 100;
    private long hitFreq = 100;
    private long lastHit = TimeUtils.millis();
    private int maxHealth = 100;

    public void getShot(Bullet b){
        this.health -= b.gun.getDamage();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void hurt(int damage){
        if(TimeUtils.millis() - lastHit > hitFreq) {
            this.health = this.health - damage;
            lastHit = TimeUtils.millis();
        }
    }

    public float getXOnCollision(float percentX, Rectangle rect, float newx){
        if(percentX < 0){
            newx = rect.x + rect.width + 10;
        }else if(percentX > 0){
            newx = rect.x - 10;
        }
        return newx;
    }

    public float getYOnCollision(float percentY, Rectangle rect, float newy){
        if(percentY < 0){
            newy = rect.y + 1;
        }else if(percentY > 0){
            newy = rect.y - rect.height - 1;
        }
        return newy;
    }

    public void setHitFreq(long hitFreq) {
        this.hitFreq = hitFreq;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
}
