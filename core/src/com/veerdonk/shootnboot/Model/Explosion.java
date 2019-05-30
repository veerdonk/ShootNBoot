package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class Explosion {
    public static final float FRAME_LENGTH = 0.5f;
    public static final int OFFSET = 16;
    public static final int SIZE = 32;

    private static Animation animation = null;
    float x,y;
    float statetime;

    public boolean remove = false;

    public Explosion(float x, float y, Array<TextureRegion> explosionTextures) {
        this.x = x;
        this.y = y;
        statetime = 0;

        if(animation == null){
            animation = new Animation(FRAME_LENGTH, explosionTextures);
        }
    }
    public void update(float deltaTime){
        statetime += deltaTime;
        if(animation.isAnimationFinished(statetime)){
            remove = true;
        }
    }

    public void render(SpriteBatch batch){
        batch.draw((TextureRegion) animation.getKeyFrame(statetime), x, y);
    }
}
