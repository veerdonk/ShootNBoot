package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class TouchpadController {
    private Touchpad touchpad;

    public TouchpadController(Texture background, Texture knob, int x, int y, int width, int height) {
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        Skin touchpadSkin = new Skin();
        touchpadSkin.add("touchpadBackground", background);
        touchpadSkin.add("touchpadKnob", knob);
        Drawable touchpadBackground = touchpadSkin.getDrawable("touchpadBackground");
        Drawable touchpadKnob = touchpadSkin.getDrawable("touchpadKnob");
        touchpadStyle.background = touchpadBackground;
        touchpadStyle.knob = touchpadKnob;
        this.touchpad = new Touchpad(10, touchpadStyle);
        this.touchpad.setBounds(x, y, width, height);
    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

    public float xPercent(){
        return touchpad.getKnobPercentX();
    }
    public float yPercent(){
        return touchpad.getKnobPercentY();
    }
}
