package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PowerUp {

    private Sprite powerUpSprite;
    private Rectangle powerUpRect;
    private PowerUPType type;

    public PowerUp(Sprite powerUpSprite, Rectangle powerUpRect, PowerUPType type) {
        this.powerUpSprite = powerUpSprite;
        this.powerUpRect = powerUpRect;
        this.type = type;
    }

    public Sprite getPowerUpSprite() {
        return powerUpSprite;
    }

    public Rectangle getPowerUpRect() {
        return powerUpRect;
    }

    public PowerUPType getType() {
        return type;
    }
}
