package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Bullet implements Pool.Poolable {
    //TODO Clean up
    public Vector2 position = new Vector2(-10,-10);
    public float xpercent;
    public float ypercent;
    public Rectangle bulletRect;
    public float speed = 7f;
    public float angle;
    public Gun gun;

    public Vector2 direction;

    @Override
    public void reset() {
        this.position.set(0,0);
        //this.ypercent = 0;
        //this.xpercent = 0;
        this.direction.set(0,0);
        this.angle = 0;
        this.gun = null;
    }

    public void update(){
//        float newx = position.x + xpercent * speed;
//        float newy = position.y + ypercent * speed;
//        curMapNode.getNodeAtPosition((int) (newx/curMapNode.getWidth()), (int) (newy/curMapNode.getHeight()));
//        position.set(newx, newy);
//        bulletRect.setPosition(newx, newy);
          position.add(direction);
          bulletRect.setPosition(position.x, position.y);
    }

    public void fire(Vector2 pos, float xpercent, float ypercent, float angle, Gun gun){
        this.position = pos;
        this.xpercent = xpercent;
        this.ypercent = ypercent;
        this.angle = angle;
        this.gun = gun;
        this.bulletRect = new Rectangle(pos.x, pos.y, 10,10); //TODO correct w/h to reflect actual bullet
    }

    public void fire(Vector2 pos, Vector2 direction, float angle, Gun gun){
        this.position = pos;
        this.direction = direction.nor();//TODO test normalized vector and scale
        this.angle = angle;
        this.gun = gun;
        this.bulletRect = new Rectangle(pos.x, pos.y, 10,10); //TODO correct w/h to reflect actual bullet
    }
}
