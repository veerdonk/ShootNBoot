package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

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

}
