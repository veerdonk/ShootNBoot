package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.veerdonk.shootnboot.Model.AmmoPack;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.MapNode;
import com.veerdonk.shootnboot.Model.PowerUp;
import com.veerdonk.shootnboot.Model.Zombie;

public class CollisionController {

    private float newEndX;
    private float newEndY;

    public void checkCharacter(Rectangle rect, Sprite sprite, float percentX, float percentY, Array<MapNode> collisionMapnodes, float endx, float endy){

        boolean collidedX = false;
        boolean collidedY = false;

        for(MapNode node : collisionMapnodes){

            for(Rectangle wallRect : node.wallsInTile){
                collidedX = checkX(rect, sprite, wallRect, node, endx, percentX, true);
                if(collidedX){
                    sprite.setX(newEndX);
                    rect.x = newEndX;
                }else{
                    sprite.setX(endx);
                    rect.x = endx;
                }
                collidedY = checkY(rect, sprite, wallRect, node, endy, percentY, true);
                if(collidedY){
                    sprite.setY(newEndY);
                    rect.y = newEndY;
                }else{
                    sprite.setY(endy);
                    rect.y = endy;
                }
            }
            for(Zombie zombie : node.zombiesInTile){
                boolean hitByZombieX = checkX(rect, sprite, zombie.getZombieRect(), node, endx, percentX + 0.00000001f);//subtract 0.00000001 to make sure player gets hurt when standing still
                boolean hitByZombieY = checkY(rect, sprite, zombie.getZombieRect(), node, endy, percentY);

                if(node.playerInTile.size != 0) {
                    if (hitByZombieX || hitByZombieY) {
                        node.playerInTile.get(0).hurt(zombie.getDamage());
                    }
                }
            }
            for(PowerUp powerUp : node.powerUpsInTile){
                if(
                checkX(rect, sprite, powerUp.getPowerUpRect(), node, endx, percentX) ||
                checkY(rect, sprite, powerUp.getPowerUpRect(), node, endy, percentY)
                ){
                    if(node.playerInTile.size != 0) {
                        powerUp.isCollected = true;
                    }
                }
            }
            for(Gun gun : node.gunsInTile){
                boolean foundGunX = checkX(rect, sprite, gun.getGunRect(), node, endx, percentX);
                boolean foundGunY = checkY(rect, sprite, gun.getGunRect(), node, endy, percentY);

                if(node.playerInTile.size != 0){
                    if(foundGunX || foundGunY){
                        gun.pickedUp = true;
                        node.playerInTile.get(0).setWeapon(gun);
                    }
                }
            }
            for(AmmoPack ammoPack : node.ammoPacksInTile){
                if(
                    checkX(rect, sprite, ammoPack.getAmmoRect(), node, endx, percentX) ||
                    checkY(rect, sprite, ammoPack.getAmmoRect(), node, endy, percentY)){
                    if(node.playerInTile.size != 0){
                        ammoPack.isCollected = true;
                    }
                }
            }
        }

        if(collidedX){

        }

        if(!collidedX && !collidedY){
            sprite.setPosition(endx, endy);
            rect.setPosition(endx, endy);
        }
    }

    //TODO fix corner pieces
    //TODO pixel perfect collisions
    public boolean checkX(Rectangle movingRect, Sprite sprite, Rectangle rectToCheck, MapNode node, float endx, float percentX, boolean move){
        float newx = endx;
        boolean collided = false;

        float leftCollisionWall = rectToCheck.x;
        float rightCollisionWall = rectToCheck.x + rectToCheck.width;


        //move left
        if(
            percentX < 0 && //moving left
            endx < rightCollisionWall && //position after moving is inside other rect
            movingRect.x > rightCollisionWall //start position was to the right of other rect
        ){
            newx = endx + (rightCollisionWall - endx) + 1;
            newEndX = newx;
            collided = true;
        }

        //move right
        if(
                percentX > 0 &&
                endx + movingRect.width > leftCollisionWall &&
                movingRect.x + movingRect.width < leftCollisionWall
        ){
            newx = endx - movingRect.width - (endx - leftCollisionWall) - 1;
            newEndX = newx;
            collided = true;
        }
        return collided;
    }

    public boolean checkY(Rectangle movingRect, Sprite sprite, Rectangle rectToCheck, MapNode node, float endy, float percentY, boolean move){
        float newy = endy;
        boolean collided = false;

        float topCollisionWall = rectToCheck.y + rectToCheck.height;
        float botCollisionWall = rectToCheck.y;// - rectToCheck.height;

        //move down
        if(
                percentY < 0 &&
                endy < topCollisionWall &&
                movingRect.y > topCollisionWall
        ){
            newy = endy + (topCollisionWall - endy) + 1;
            newEndY = newy;
            collided = true;
        }
        if(
                percentY > 0 &&
                endy + movingRect.height > botCollisionWall &&
                movingRect.y + movingRect.height < botCollisionWall
        ){
            newy = endy - movingRect.height - (endy - botCollisionWall) - 1;
            newEndY = newy;
            collided = true;
        }

        return collided;
    }


    public boolean checkX(Rectangle movingRect, Sprite sprite, Rectangle rectToCheck, MapNode node, float endx, float percentX){
        float newx = endx;
        boolean collided = false;

        float leftCollisionWall = rectToCheck.x;
        float rightCollisionWall = rectToCheck.x + rectToCheck.width;


        //move left
        if(
                percentX < 0 && //moving left
                endx < rightCollisionWall
        ){
            newx = endx + (rightCollisionWall - endx) + 1;
            newEndX = newx;
            collided = true;
        }

        //move right
        if(
                percentX > 0 &&
                endx + movingRect.width > leftCollisionWall
        ){
            newx = endx - movingRect.width - (endx - leftCollisionWall) - 1;
            newEndX = newx;
            collided = true;
        }
        return collided;
    }

    public boolean checkY(Rectangle movingRect, Sprite sprite, Rectangle rectToCheck, MapNode node, float endy, float percentY){
        float newy = endy;
        boolean collided = false;

        float topCollisionWall = rectToCheck.y + rectToCheck.height;
        float botCollisionWall = rectToCheck.y;// - rectToCheck.height;

        //move down
        if(
                percentY < 0 &&
                endy < topCollisionWall
        ){
            newy = endy + (topCollisionWall - endy) + 1;
            newEndY = newy;
            collided = true;
        }

        if(
                percentY > 0 &&
                        endy + movingRect.height > botCollisionWall
        ){
            newy = endy - movingRect.height - (endy - botCollisionWall) - 1;
            newEndY = newy;
            collided = true;
        }
        return collided;
    }
}
