package com.veerdonk.shootnboot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.veerdonk.shootnboot.Screens.MainMenuScreen;

public class ShootNBoot extends Game {

	public SpriteBatch batch;
	public BitmapFont font;

	@Override
	public void create() {
		batch = new SpriteBatch();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Oswald-Regular.ttf"));
		FreeTypeFontParameter freeTypeFontParameter= new FreeTypeFontParameter();
		freeTypeFontParameter.size = 100;
		this.font = generator.generateFont(freeTypeFontParameter);
		generator.dispose();
		this.setScreen(new MainMenuScreen(this));
	}

	public void render(){
		super.render();
	}

	public void dispose(){
		batch.dispose();
		font.dispose();
	}
}
