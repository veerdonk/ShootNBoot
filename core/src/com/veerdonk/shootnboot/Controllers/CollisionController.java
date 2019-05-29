package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.veerdonk.shootnboot.Model.MapNode;

public class CollisionController {
    public void checkCharacter(Rectangle rect, Sprite sprite, float percentX, float percentY, Array<MapNode> collisionMapnodes, float endx, float endy){
        boolean collidedX = false;
        boolean collidedY = false;

        for(MapNode node : collisionMapnodes){
            Gdx.app.log("nodex", Integer.toString(node.getxNode()));
            Gdx.app.log("nodey", Integer.toString(node.getyNode()));
            boolean collision = false;
            for(Rectangle wallRect : node.wallsInTile){
                collidedX = checkX(rect, sprite, wallRect, node, endx, percentX);
                collidedY = checkY(rect, sprite, wallRect, node, endy, percentY);
            }
            for(Zombie zombie : node.zombiesInTile){
                collidedX = checkX(rect, sprite, zombie.getZombieRect(), node, endx, percentX);
                collidedY = checkY(rect, sprite, zombie.getZombieRect(), node, endy, percentY);
                    //TODO hurt the player
                }
        }
        if(!collidedX && !collidedY){
            sprite.setPosition(endx, endy);
            rect.setPosition(endx, endy);
        }else if(!collidedX && collidedY){
            sprite.setX(endx);
            rect.x = endx;
        }else if(collidedX && ! collidedY){
            sprite.setY(endy);
            rect.y = endy;
        }
    }

    //TODO fix corner pieces
    //TODO refactor to check collisions that arent outer walls
    public boolean checkX(Rectangle movingRect, Sprite sprite, Rectangle rectToCheck, MapNode node, float endx, float percentX){
        float newx = endx;
        boolean collided = false;
        if(rectToCheck.x + 32 > endx && percentX < 0 && node.getxNode() <= 49 && node.getyNode() >= 1 && node.getyNode() < 49){
            Gdx.app.log("wall hit: ", rectToCheck.toString());
            newx = rectToCheck.x + 33;
            collided = true;
        }
        if(rectToCheck.x < endx + 32 && percentX > 0 &&  node.getxNode() > 0 && node.getyNode() >= 1 && node.getyNode() < 49){
            Gdx.app.log("percentX", Float.toString(percentX));
            Gdx.app.log("endx", Float.toString(endx + 32));
            newx = rectToCheck.x - 35;
            collided = true;
        }
        if(collided) {
            movingRect.x = newx;
            sprite.setX(newx);
        }
        return collided;
    }

    public boolean checkY(Rectangle movingRect, Sprite sprite, Rectangle rectToCheck, MapNode node, float endy, float percentY){
        float newy = endy;
        boolean collided = false;
        if(rectToCheck.y + 32 > endy && percentY < 0 && node.getyNode() <= 49 && node.getxNode() >= 1 && node.getxNode() < 49){
            newy = rectToCheck.y + 33;
            collided = true;
        }
        if(rectToCheck.y < endy + 32 && percentY > 0 && node.getyNode() > 0 && node.getxNode() >= 1 && node.getxNode() < 49){
            newy = rectToCheck.y - 33;
            collided = true;
        }
        if(collided) {
            movingRect.y = newy;
            sprite.setY(newy);
        }
        return collided;
    }
}
