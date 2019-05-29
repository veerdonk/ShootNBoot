package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.MapNode;


public class Player extends Character{
    private float width = 32f;
    private float height = 32f;
    private Sprite playerSprite;
    private float playerSpeed;
    private Rectangle playerRect;
    private Gun weapon;
    private CollisionController cc;

    public Player(Sprite playSprite, float playerSpeed, float initialX, float initialY) {
        this.playerSprite = playSprite;
        playerSprite.setPosition(initialX, initialY);
        this.playerSpeed = playerSpeed;
        this.playerRect = new Rectangle(initialX, initialY, width, height);
        this.cc = new CollisionController();
    }

    public void setPosition(float x, float y){
        this.getPlayerRect().setPosition(x, y);
        this.getPlayerSprite().setPosition(x,y);
    }

    public void move(float percentX, float percentY, Array<MapNode> collisionMapnodes){
        float endx = getX() + percentX * playerSpeed;
        float endy = getY() + percentY * playerSpeed;

        cc.checkCharacter(playerRect, playerSprite, percentX, percentY, collisionMapnodes, endx, endy);

//        boolean collidedX = false;
//        boolean collidedY = false;
//
//        for(MapNode node : collisionMapnodes){
//            Gdx.app.log("nodex", Integer.toString(node.getxNode()));
//            Gdx.app.log("nodey", Integer.toString(node.getyNode()));
//            boolean collision = false;
//            for(Rectangle wallRect : node.wallsInTile){
//                collidedX = checkX(wallRect, node, endx, percentX);
//                collidedY = checkY(wallRect, node, endy, percentY);
//            }


//            for(Zombie zombie : node.zombiesInTile){
//                newx = getX() + percentX * playerSpeed;
//                newy = getY() + percentY * playerSpeed;
//                if(zombie.getZombieRect().overlaps(playerRect)){
//                    newx = getXOnCollision(percentX, zombie.getZombieRect(), newx);
//                    newy = getYOnCollision(percentY, zombie.getZombieRect(), newy);
//                    //TODO hurt the player
//                }
//            }


//        }
//        if(!collidedX && !collidedY){
//            playerSprite.setPosition(endx, endy);
//            playerRect.setPosition(endx, endy);
//        }else if(!collidedX && collidedY){
//            playerSprite.setX(endx);
//            playerRect.x = endx;
//        }else if(collidedX && ! collidedY){
//            playerSprite.setY(endy);
//            playerRect.y = endy;
//        }


    }

    public Gun getWeapon() {
        return weapon;
    }

    public boolean checkX(Rectangle wallRect, MapNode node, float endx, float percentX){
        float newx = endx;
        boolean collided = false;
        if(wallRect.x + 32 > endx && percentX < 0 && node.getxNode() < 49){
            Gdx.app.log("wall hit: ", wallRect.toString());
            newx = wallRect.x + 33;
            collided = true;
        }
        if(wallRect.x < endx + 32 && percentX > 0 &&  node.getxNode() >= 1){
            Gdx.app.log("percentX", Float.toString(percentX));
            Gdx.app.log("endx", Float.toString(endx + 32));
            newx = wallRect.x - 35;
            collided = true;
        }
        if(collided) {
            playerRect.x = newx;
            playerSprite.setX(newx);
        }
        return collided;
    }

    public boolean checkY(Rectangle wallRect, MapNode node, float endy, float percentY){
        float newy = endy;
        boolean collided = false;
        if(wallRect.y + 32 > endy && percentY < 0 && node.getyNode() < 49){
            newy = wallRect.y + 33;
            collided = true;
        }
        if(wallRect.y < endy + 32 && percentY > 0 && node.getyNode() >= 1){
            newy = wallRect.y - 33;
            collided = true;
        }
        if(collided) {
            playerRect.y = newy;
            playerSprite.setY(newy);
        }
        return collided;
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
