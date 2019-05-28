package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.veerdonk.shootnboot.Model.Bullet;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.MapNode;
import com.veerdonk.shootnboot.Pools.BulletPool;

public class Player extends Character{
    private float width = 32f;
    private float height = 32f;
    private Sprite playerSprite;
    private float playerSpeed;
    private Rectangle playerRect;
    private Gun weapon;

    public Player(Sprite playSprite, float playerSpeed, float initialX, float initialY) {
        this.playerSprite = playSprite;
        playerSprite.setPosition(initialX, initialY);
        this.playerSpeed = playerSpeed;
        this.playerRect = new Rectangle(initialX, initialY, width, height);
    }

    public void setPosition(float x, float y){
        this.getPlayerRect().setPosition(x, y);
        this.getPlayerSprite().setPosition(x,y);
    }

    public void move(float percentX, float percentY, Array<MapNode> collisionMapnodes){
        float newx = getX() + percentX * playerSpeed;
        float newy = getY() + percentY * playerSpeed;
        for(MapNode node : collisionMapnodes){
            for(Rectangle wallRect : node.wallsInTile){
                if(wallRect.overlaps(playerRect)){
                    newx = getXOnCollision(percentX, wallRect, newx);
                    newy = getYOnCollision(percentY, wallRect, newy);
                }
            }
            for(Zombie zombie : node.zombiesInTile){
                if(zombie.getZombieRect().overlaps(playerRect)){
                    newx = getXOnCollision(percentX, zombie.getZombieRect(), newx);
                    newy = getYOnCollision(percentY, zombie.getZombieRect(), newy);
                    //TODO hurt the player
                }
            }
        }
        playerSprite.setPosition(newx, newy);
        playerRect.setPosition(newx, newy);
    }

    public Gun getWeapon() {
        return weapon;
    }

    public void setWeapon(Gun weapon) {
        this.weapon = weapon;
    }

    public void pickupGun(Gun gun){
        this.weapon = gun;
        this.playerSprite = gun.getPlayerGunSprite();
    }

    public float getX(){
        return playerRect.getX();
    }

    public float getY(){
        return playerRect.getY();
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public void setPlayerSprite(Sprite playerSprite) {
        this.playerSprite = playerSprite;
    }

    public float getPlayerSpeed() {
        return playerSpeed;
    }

    public void setPlayerSpeed(float playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    public Rectangle getPlayerRect() {
        return playerRect;
    }

    public void setPlayerRect(Rectangle playerRect) {
        this.playerRect = playerRect;
    }

    public void rotate(Vector2 vec) {
        playerSprite.setRotation(vec.angle());
    }

}
