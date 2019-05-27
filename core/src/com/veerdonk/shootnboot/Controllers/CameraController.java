package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraController {
    private OrthographicCamera camera;
    private int width;
    private int height;

    public CameraController(int width, int height) {
        this.width = width;
        this.height = height;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
