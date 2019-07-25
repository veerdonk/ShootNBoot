package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class AmmoPack {

    public boolean isCollected;
    private Sprite ammoSprite;
    private Rectangle ammoRect;
    private GunType ammoType;

    public AmmoPack(Sprite ammoSprite, Rectangle ammoRect, GunType ammoType) {
        this.ammoSprite = ammoSprite;
        this.ammoRect = ammoRect;
        this.ammoType = ammoType;
    }

    public Sprite getAmmoSprite() {
        return ammoSprite;
    }

    public Rectangle getAmmoRect() {
        return ammoRect;
    }

    public GunType getAmmoType() {
        return ammoType;
    }
}
