package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Bullet implements Pool.Poolable {

    public Vector2 position = new Vector2(-10,-10);
    public float xpercent;
    public float ypercent;

    public float speed = 7f;
    public float angle;
    public Gun gun;

    @Override
    public void reset() {
        this.position.set(0,0);
        this.ypercent = 0;
        this.xpercent = 0;
        this.angle = 0;
        this.gun = null;
    }

    public void update(MapNode curMapNode){
        //TODO check collision using nodes
        float newx = position.x + xpercent * speed;
        float newy = position.y + ypercent * speed;
        curMapNode.getNodeAtPosition((int) (newx/curMapNode.getWidth()), (int) (newy/curMapNode.getHeight()));
        position.set(newx, newy);
    }

    public void fire(Vector2 pos, float xpercent, float ypercent, float angle, Gun gun){
        this.position = pos;
        this.xpercent = xpercent;
        this.ypercent = ypercent;
        this.angle = angle;
        this.gun = gun;
    }
}
