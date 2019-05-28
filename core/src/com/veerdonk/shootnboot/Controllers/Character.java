package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.math.Rectangle;
import com.veerdonk.shootnboot.Model.Bullet;

public class Character {
    private int health = 100;

    public void getShot(Bullet b){
        this.health -= b.gun.getDamage();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getXOnCollision(float percentX, Rectangle rect, float newx){
        if(percentX < 0){
            newx = rect.x + rect.width + 1;
        }else if(percentX > 0){
            newx = rect.x - 1;
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
}
