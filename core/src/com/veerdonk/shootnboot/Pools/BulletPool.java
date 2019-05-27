package com.veerdonk.shootnboot.Pools;

import com.badlogic.gdx.utils.Pool;
import com.veerdonk.shootnboot.Model.Bullet;

public class BulletPool extends Pool<Bullet> {

    //custom with starting (init) and max number
   public BulletPool(int init, int max){
       super(init, max);
   }

   //deafult with 16 objects
   public BulletPool(){
       super();
   }

    @Override
    protected Bullet newObject() {
        return new Bullet();
    }
}
