package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.Random;

public class Bullet implements Pool.Poolable {

    public Vector2 position = new Vector2(-10,-10);
    public Rectangle bulletRect;
    public float speed = 5f;
    public float angle;
    public Gun gun;
    public Vector2 direction;

    @Override
    public void reset() {
        this.position.set(0,0);
        this.direction.set(0,0);
        this.speed = 5f;
        this.angle = 0;
        this.gun = null;
    }

    public void update(){
          position.add(direction);
          bulletRect.setPosition(position.x, position.y);
    }

    public void fire(Vector2 pos, Vector2 direction, float angle, Gun gun){
        this.position = pos;
        this.direction = direction.nor();
        this.angle = angle;
        this.gun = gun;
        this.bulletRect = new Rectangle(pos.x, pos.y, 5,5); //TODO correct w/h to reflect actual bullet
        direction.add(new Vector2(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle)));
    }

    public void fireRandom(Vector2 pos, Vector2 direction, float angle, Gun gun, int range){
        this.position = pos;
        this.direction = direction.nor();
        this.angle = angle;
        this.gun = gun;
        this.bulletRect = new Rectangle(pos.x, pos.y, 5,5);
        Random random = new Random();
        int randomDir = random.nextInt(range*2) - range;
        direction.add(calculateDirection(speed, angle, randomDir));
        //direction.add(new Vector2(speed * MathUtils.cosDeg(angle + randomDir), speed * MathUtils.sinDeg(angle + randomDir)));
    }

    public Vector2 calculateDirection(float speed, float angle, float randomDir){
        return new Vector2(speed * MathUtils.cosDeg(angle + randomDir), speed * MathUtils.sinDeg(angle + randomDir));
    }
    public Vector2 calculateDirection(float speed, float angle){
        return new Vector2(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle));
    }

}
