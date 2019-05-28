package com.veerdonk.shootnboot.Controllers;

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

}
