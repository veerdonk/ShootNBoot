package com.veerdonk.shootnboot.Pools;

import com.badlogic.gdx.utils.Pool;
import com.veerdonk.shootnboot.Model.Zombie;

public class ZombiePool extends Pool<Zombie> {
    //custom with starting (init) and max number
    public ZombiePool(int init, int max){
        super(init, max);
    }

    //deafult with 16 objects
    public ZombiePool(){
        super();
    }

    @Override
    protected Zombie newObject() {
        return new Zombie();
    }
}
