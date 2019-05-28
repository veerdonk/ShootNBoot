package com.veerdonk.shootnboot.Model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.veerdonk.shootnboot.Controllers.Player;
import com.veerdonk.shootnboot.Controllers.Zombie;

public class MapNode {
    private float x;
    private float y;
    private float width;
    private float height;
    private int xNode;
    private int yNode;
    public Array<Bullet> bulletsInTile;
    public Array<Zombie> zombiesInTile;
    public Array<Player> playerInTile;
    public Array<Rectangle> wallsInTile;

    public MapNode(float x, float y, float width, float height, int xNode, int yNode) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xNode = xNode;
        this.yNode = yNode;
        this.bulletsInTile = new Array<Bullet>();
        this.zombiesInTile = new Array<Zombie>();
    }

    public void setAll(float x, float y, float width, float height, int xNode, int yNode){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xNode = xNode;
        this.yNode = yNode;
    }

    public int[][] getNodeAtPosition(int relX, int relY){
        return new int [relX][relY];
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getxNode() {
        return xNode;
    }

    public void setxNode(int xNode) {
        this.xNode = xNode;
    }

    public int getyNode() {
        return yNode;
    }

    public void setyNode(int yNode) {
        this.yNode = yNode;
    }

}
