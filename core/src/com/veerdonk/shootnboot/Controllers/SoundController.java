package com.veerdonk.shootnboot.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class SoundController {

    private Sound pistol = Gdx.audio.newSound(Gdx.files.internal("sound/pistol.mp3"));
    private Sound machine = Gdx.audio.newSound(Gdx.files.internal("sound/cg1.mp3"));
    private Sound shotgun = Gdx.audio.newSound(Gdx.files.internal("sound/shotgun.mp3"));

    private Sound zombieAttack1 = Gdx.audio.newSound(Gdx.files.internal("sound/zombie_att1.mp3"));
    private Sound zombieAttack2 = Gdx.audio.newSound(Gdx.files.internal("sound/zombie_att2.mp3"));
    private Sound zombieAttack3 = Gdx.audio.newSound(Gdx.files.internal("sound/zombie_att3.mp3"));
    private Sound zombieHurt1 = Gdx.audio.newSound(Gdx.files.internal("sound/zombie_pain1.mp3"));
    private Sound zombieHurt2 = Gdx.audio.newSound(Gdx.files.internal("sound/zombie_pain2.mp3"));
    private Sound zombieHurt3 = Gdx.audio.newSound(Gdx.files.internal("sound/zombie_pain3.mp3"));

    private Sound playerHurt = Gdx.audio.newSound(Gdx.files.internal("sound/gruntsound.mp3"));
    private Sound playerLevel = Gdx.audio.newSound(Gdx.files.internal("sound/chipquest.mp3"));
    private Sound ding = Gdx.audio.newSound(Gdx.files.internal("sound/ding.mp3"));
    private Sound error = Gdx.audio.newSound(Gdx.files.internal("sound/error.mp3"));
    private long lastPlayerHurt = TimeUtils.millis();


    public void playSound(String sound){
        switch(sound){
            case "pistol":
                shootPistol();
                break;
            case "machine":
                shootMachine();
                break;
            case "shotgun":
                shootShotgun();
                break;
        }
    }

    public void shootPistol(){
        long id = pistol.play();
        pistol.setLooping(id, false);
    }

    public void shootMachine(){
        machine.play();
    }

    public void shootShotgun(){
        shotgun.play();
    }

    public void hurtZombie(){
        Random rand = new Random();
        int i = rand.nextInt(3);
        Gdx.app.log("i", Integer.toString(i));
        switch(i){
            case 0:
                zombieHurt1.play();
                break;
            case 1:
                zombieHurt2.play();
                break;
            case 2:
                zombieHurt3.play();
                break;
        }
    }

    public void hurtPlayer(){
        if(TimeUtils.millis() - lastPlayerHurt > 300) {
            playerHurt.play();
            lastPlayerHurt = TimeUtils.millis();
        }

    }

    public void level(){
        playerLevel.play();
    }

    public void zombieAttack(){
        Random rand = new Random();
        int i = rand.nextInt(2);
        switch(i){
            case 0:
                zombieAttack1.play();
                break;
            case 1:
                zombieAttack2.play();
                break;
            case 2:
                zombieAttack3.play();
                break;
        }
    }

    public void playDing(){
        ding.play();
    }

    public void playError(){
        error.play();
    }

    public void dispose(){
        pistol.dispose();
        machine.dispose();
        shotgun.dispose();

        zombieAttack1.dispose();
        zombieAttack2.dispose();
        zombieAttack3.dispose();
        zombieHurt1.dispose();
        zombieHurt2.dispose();
        zombieHurt3.dispose();

        playerHurt.dispose();
        playerLevel.dispose();
        ding.dispose();
        error.dispose();
    }

}
