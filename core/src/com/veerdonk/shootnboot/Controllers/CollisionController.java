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


    public void checkCharacter(Rectangle rect, Sprite sprite, float percentX, float percentY, Array<MapNode> collisionMapnodes, float endx, float endy){

        boolean collidedX = false;
        boolean collidedY = false;

        for(MapNode node : collisionMapnodes){

            for(Rectangle wallRect : node.wallsInTile){
                collidedX = checkX(rect, sprite, wallRect, node, endx, percentX, true);
                collidedY = checkY(rect, sprite, wallRect, node, endy, percentY, true);
            }
            for(Zombie zombie : node.zombiesInTile){
                boolean hitByZombieX = checkX(rect, sprite, zombie.getZombieRect(), node, endx, percentX + 0.00000001f, false);//subtract 0.00000001 to make sure player gets hurt when standing still
                boolean hitByZombieY = checkY(rect, sprite, zombie.getZombieRect(), node, endy, percentY, false);

                if(node.playerInTile.size != 0) {
                    if (hitByZombieX || hitByZombieY) {
                        node.playerInTile.get(0).hurt(zombie.getDamage());
                    }
                }
            }
            for(PowerUp powerUp : node.powerUpsInTile){
                if(
                checkX(rect, sprite, powerUp.getPowerUpRect(), node, endx, percentX, false) ||
                checkY(rect, sprite, powerUp.getPowerUpRect(), node, endy, percentY, false)
                ){
                    if(node.playerInTile.size != 0) {
                        powerUp.isCollected = true;
                    }
                }
            }
            for(Gun gun : node.gunsInTile){
                boolean foundGunX = checkX(rect, sprite, gun.getGunRect(), node, endx, percentX, false);
                boolean foundGunY = checkY(rect, sprite, gun.getGunRect(), node, endy, percentY, false);

                if(node.playerInTile.size != 0){
                    if(foundGunX || foundGunY){
                        gun.pickedUp = true;
                        node.playerInTile.get(0).setWeapon(gun);
                    }
                }
            }
            for(AmmoPack ammoPack : node.ammoPacksInTile){
                if(
                    checkX(rect, sprite, ammoPack.getAmmoRect(), node, endx, percentX, false) ||
                    checkY(rect, sprite, ammoPack.getAmmoRect(), node, endy, percentY, false)){
                    if(node.playerInTile.size != 0){
                        ammoPack.isCollected = true;
                    }
                }
            }
        }
        if(!collidedX && !collidedY){
            sprite.setPosition(endx, endy);
            rect.setPosition(endx, endy);
        }
        if(!collidedX && collidedY){
            sprite.setX(endx);
            rect.x = endx;
        }
        if(collidedX && ! collidedY){
            Gdx.app.log("setting y pos", Float.toString(endy));
            sprite.setY(endy);
            rect.y = endy;
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
//        if(
//            percentX < 0 && //moving left
//            endx < rightCollisionWall && //position after moving is inside other rect
//            movingRect.x > rightCollisionWall //&&//start position was to the right of other rect
//        ){
//            Gdx.app.log("moving left, wall found", rectToCheck.toString());
//            newx = endx + (rightCollisionWall - endx) + 1;
//            collided = true;
//        }

        //move right
//        Gdx.app.log("moving rect x", Float.toString(movingRect.x - movingRect.width));
//        Gdx.app.log("leftcollisionwall", Float.toString(leftCollisionWall));
//        Gdx.app.log("endx", Float.toString(endx));
        if(
                percentX > 0 &&
                endx + movingRect.width > leftCollisionWall &&
                movingRect.x + movingRect.width < leftCollisionWall//&&
                //!(movingRect.y > rectToCheck.y + rectToCheck.height) &&
                //!(movingRect.y < rectToCheck.y)
        ){
            Gdx.app.log("moving right, wall found", rectToCheck.toString());
            newx = endx - movingRect.width - (endx - leftCollisionWall) - 1;
            collided = true;
        }



//        if(rectToCheck.x + rectToCheck.width > endx && percentX < 0 && node.getxNode() <= 49 && node.getyNode() >= 1 && node.getyNode() < 49){
//            //Gdx.app.log("wall hit: ", rectToCheck.toString());
//            newx = rectToCheck.x + rectToCheck.width + 1;
//            collided = true;
//        }
//        if(rectToCheck.x < endx + rectToCheck.width && percentX > 0 &&  node.getxNode() > 0 && node.getyNode() >= 1 && node.getyNode() < 49){
////            Gdx.app.log("percentX", Float.toString(percentX));
////            Gdx.app.log("endx", Float.toString(endx + 32));
//            newx = rectToCheck.x - rectToCheck.width - 1;
//            collided = true;
//        }


        if(collided) {
            if(move) {
                movingRect.x = newx;
                sprite.setX(newx);
            }
        }
        return collided;
    }

    public boolean checkY(Rectangle movingRect, Sprite sprite, Rectangle rectToCheck, MapNode node, float endy, float percentY, boolean move){
        float newy = endy;
        boolean collided = false;

        float leftCollisionWall = rectToCheck.x;
        float rightCollisionWall = rectToCheck.x + rectToCheck.width;

        float topCollisionWall = rectToCheck.y; //+ rectToCheck.height;
        float botCollisionWall = rectToCheck.y;// - rectToCheck.height;

        //move down
//        if(
//                percentY < 0 &&
//                endy < topCollisionWall &&
//                movingRect.y > topCollisionWall //&&
//                //!(movingRect.x + movingRect.width < rightCollisionWall) &&
//                //!(movingRect.x > leftCollisionWall)
//        ){
//            Gdx.app.log("moving down, wall found", rectToCheck.toString());
//            newy = endy + (topCollisionWall - endy) + 1;
//            collided = true;
//        }
//        Gdx.app.log("movingrect top y", Float.toString(movingRect.y + movingRect.height));
        //move up
        if(
                percentY > 0 &&
                endy + movingRect.height > botCollisionWall &&
                movingRect.y + movingRect.height < botCollisionWall //&&
                //!(movingRect.x + movingRect.width < rightCollisionWall) &&
                //!(movingRect.x > leftCollisionWall)
        ){
            Gdx.app.log("moving up, wall found", rectToCheck.toString());
            newy = endy - movingRect.height - (endy - botCollisionWall) - 1;
            collided = true;
        }


//
//        if(rectToCheck.y + rectToCheck.height > endy && percentY < 0 && node.getyNode() <= 49 && node.getxNode() >= 1 && node.getxNode() < 49){
//            newy = rectToCheck.y + rectToCheck.height + 1;
//            collided = true;
//        }
//        if(rectToCheck.y < endy + rectToCheck.height && percentY > 0 && node.getyNode() > 0 && node.getxNode() >= 1 && node.getxNode() < 49){
//            newy = rectToCheck.y - rectToCheck.height - 1;
//            collided = true;
//        }


        if(collided) {
            if(move) {
                movingRect.y = newy;
                sprite.setY(newy);
            }
        }
        return collided;
    }
}
