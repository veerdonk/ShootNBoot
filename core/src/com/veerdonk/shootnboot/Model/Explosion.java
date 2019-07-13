package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


public class Explosion {
    public static final float FRAME_LENGTH = 0.1f;
    private float size;

    private static Animation animation = null;
    private float x,y;
    private float statetime;
    private Color color;
    private long startTime;

    public boolean remove = false;

    public Explosion(float x, float y, Array<TextureRegion> explosionTextures, float size, Color color, long startTime) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
        statetime = 0;
        this.startTime = startTime;

        if(animation == null){
            animation = new Animation(FRAME_LENGTH, explosionTextures, Animation.PlayMode.NORMAL);
        }
    }

    public Explosion(float x, float y, Array<TextureRegion> explosionTextures, float size, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
        statetime = 0;
        this.startTime = TimeUtils.millis();
        if(animation == null){
            animation = new Animation(FRAME_LENGTH, explosionTextures, Animation.PlayMode.NORMAL);
        }
    }

    public void update(float deltaTime){
        if(startTime - TimeUtils.millis() <= 0) {
            statetime += deltaTime;
        }
        if(animation.isAnimationFinished(statetime)){
            remove = true;
        }
    }

    public void render(SpriteBatch batch){
        if(startTime- TimeUtils.millis() <= 0) {
            batch.setColor(color);
            batch.draw((TextureRegion) animation.getKeyFrame(statetime, false), x, y, 0, 0, size, size, 1f, 1f, 0);
            batch.setColor(Color.WHITE);
        }
    }
}
