package com.veerdonk.shootnboot.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.veerdonk.shootnboot.ShootNBoot;

public class ButtonUtil {

    private TextButton.TextButtonStyle style;

    public ButtonUtil(ShootNBoot game) {
        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/button_blue.atlas"));
        Skin skin = new Skin();
        skin.addRegions(buttonAtlas);
        style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable("blue_button_up");
        style.down = skin.getDrawable("blue_button_down");
        style.font = game.font;
    }

    public TextButton getButton(String text, int col, int row){
        TextButton button = new TextButton(text, style);
        float xpos = 0;
        float ypos = 0;
        switch(col){
            case 0:
                xpos = 10;
                break;
            case 1:
                xpos = 210;
                break;
            case 2:
                xpos = 420;
                break;
            case 3:
                xpos = 630;
                break;
        }

        switch(row){
            case 0:
                ypos = 375;
                break;
            case 1:
                ypos = 275;
                break;
            case 2:
                ypos = 175;
                break;
            case 3:
                ypos = 85;
                break;
        }
        button.setPosition(xpos, ypos);
        return button;
    }
}
