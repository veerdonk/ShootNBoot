package com.veerdonk.shootnboot.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.veerdonk.shootnboot.Controllers.CameraController;
import com.veerdonk.shootnboot.Controllers.CollisionController;
import com.veerdonk.shootnboot.Controllers.SoundController;
import com.veerdonk.shootnboot.Controllers.TouchpadController;
import com.veerdonk.shootnboot.Model.Bullet;
import com.veerdonk.shootnboot.Model.Explosion;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.GunType;
import com.veerdonk.shootnboot.Model.MapNode;
import com.veerdonk.shootnboot.Model.Player;
import com.veerdonk.shootnboot.Model.PowerUPType;
import com.veerdonk.shootnboot.Model.PowerUp;
import com.veerdonk.shootnboot.Model.Zombie;
import com.veerdonk.shootnboot.Pools.BulletPool;
import com.veerdonk.shootnboot.Pools.ZombiePool;
import com.veerdonk.shootnboot.ShootNBoot;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class GameScreen implements Screen {
    final ShootNBoot game;
    private SoundController sc;
    private Gun machine;
    private Gun submachine;
    private float delta = 0;
    public static Player player;
    private CameraController cameraController;
    private TouchpadController touchpadController;
    private TouchpadController rotationTouchpadController;
    private CollisionController collisionController;

    private Array<Zombie> activeZombies;
    private long zombieSpawnRate;
    private long lastZombie;
    private Sprite zombieSprite;
    private float zombieSpeed;
    private int zombieDamage;
    private final ZombiePool zp = new ZombiePool();
    private Array<Explosion> activeExplosions = new Array<Explosion>();
    private Array<TextureRegion> explosionTextures;
    private Texture blank;

    private final Array<Bullet> activeBullets = new Array<Bullet>();
    private final BulletPool bp = new BulletPool();
    private Sprite bSprite;
    private long now = TimeUtils.millis();
    private long lastShot = now;

    private TextureAtlas textureAtlas;
    private Map<String, Gun> guns = new HashMap<String, Gun>();
    private Gun pistol;
    private Gun shotgun;
    private Array<Gun> activeGuns = new Array<Gun>();

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    MapProperties mapProperties;
    int mapHeight;
    int mapWidth;
    private int mapNodeHeight = 64;
    private int mapNodeWidth = 64;
    private MapNode [] [] mapNodes;
    private int xpGained;
    private float difficultyMultiplier = 1.5f;
    private long lastDifficultyIncrease = TimeUtils.millis();

    private SpriteBatch batch;
    private Stage stage;
    private long returnedFromShop;
    private int freezeHealth;

    private boolean movementVisible = false;
    private boolean rotationVisible = false;
    private long lastPowerUp = TimeUtils.millis();
    private Array<PowerUp> powerUps;
    private Map<String, Sprite> powerUpSprites;

    private Button bombButton;
    private Random random = new Random();
    private String swarmMessage = "A large group of zombies has spawned...";
    private long textDisplayedSince;
    private boolean swarmSpawned;

    public GameScreen(final ShootNBoot game) {
        this.game = game;
        sc = new SoundController();
        textureAtlas = new TextureAtlas(Gdx.files.internal("Characters.atlas"));
        blank = new Texture(Gdx.files.internal("blank_1px.png"));
        bSprite = textureAtlas.createSprite("bulletSand1_outline");
        tiledMap = new TmxMapLoader().load("shootNBootMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        collisionController = new CollisionController();
        explosionTextures = new Array<TextureRegion>();
        explosionTextures.addAll(textureAtlas.findRegions("explosion"));
        mapProperties = tiledMap.getProperties();
        mapHeight = mapProperties.get("mapHeight", Integer.class);
        mapWidth = mapProperties.get("mapWidth", Integer.class);
        mapNodes = new MapNode[mapWidth][mapHeight];
        //Creates all the mapnodes (expensive)
        for(int i = 0; i < mapWidth/mapNodeWidth; i++){
            for(int j = 0; j < mapHeight/mapNodeHeight; j++){
                mapNodes[i][j] = new MapNode(i*mapNodeWidth,j*mapNodeHeight,mapNodeWidth,mapNodeHeight, i, j);
            }}
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("background");

        for(int x = 0; x < mapWidth/layer.getTileWidth(); x++){
            for(int y = 0; y < mapHeight/layer.getTileHeight(); y++){
                if(layer.getCell(x,y) != null && layer.getCell(x, y).getTile().getProperties().containsKey("blocked")){
                    mapNodes[x*32/mapNodeWidth][y*32/mapNodeHeight].wallsInTile.add(new Rectangle(x*32, y*32, 32, 32));
                }
            }
        }

        activeZombies = new Array<Zombie>();
        zombieSpawnRate = 4500;//spawnrate in millis
        zombieSpeed = 1.5f;
        lastZombie = now;
        zombieDamage = 5;
        zombieSprite = textureAtlas.createSprite("zoimbie1_hold");

        powerUpSprites = new HashMap<>();
        powerUpSprites.put("QUAD", textureAtlas.createSprite("powerUpQuad"));
        powerUpSprites.put("RAPIDFIRE", textureAtlas.createSprite("powerUpRapid"));
        powerUpSprites.put("SPEED", textureAtlas.createSprite("powerUpSpeed"));
        powerUpSprites.put("REGEN", textureAtlas.createSprite("powerUpRegen"));
        powerUps = new Array<>();

        batch = new SpriteBatch();
        cameraController = new CameraController(800, 480);
        Sprite playerSprite = textureAtlas.createSprite("survivor1_stand");
        player = new Player(
                playerSprite,
                5,
                200,
                200,
                getCurrentMapNode(200,200),
                sc
        );
        player.currentNode.playerInTile.add(player);
        touchpadController = new TouchpadController(
                new Texture(Gdx.files.internal("touchBack.png")),
                new Texture(Gdx.files.internal("touchKnob.png")),
                15,
                35,
                150,
                150
        );
        rotationTouchpadController = new TouchpadController(
                new Texture(Gdx.files.internal("touchBack.png")),
                new Texture(Gdx.files.internal("touchKnob.png")),
                (int) cameraController.getCamera().viewportWidth - 150 - 15,
                35,
                150,
                150
        );
        pistol = new Gun(textureAtlas.createSprite("weapon_gun"), textureAtlas.createSprite("survivor1_gun"), GunType.PISTOL, 200f, 100f, "pistol");
        shotgun = new Gun(textureAtlas.createSprite("weapon_shotgun"), textureAtlas.createSprite("survivor1_shotgun"), GunType.SHOTGUN, 0f, 0f, "shotgun");
        submachine = new Gun(textureAtlas.createSprite("weapon_machine"), textureAtlas.createSprite("survivor1_submachine"), GunType.SUBMACHINE, 0f, 0f, "machine");
        machine = new Gun(textureAtlas.createSprite("weapon_machine_gun"), textureAtlas.createSprite("survivor1_machine_gun"), GunType.MACHINEGUN, 0f, 0f, "pistol");
        getCurrentMapNode(pistol.getX(), pistol.getY()).gunsInTile.add(pistol);
        getCurrentMapNode(shotgun.getX(), shotgun.getY()).gunsInTile.add(shotgun);
        guns.put("pistol", pistol);
        guns.put("shotgun", shotgun);
        guns.put("submachine", submachine);
        guns.put("machine", machine);
        activeGuns.add(pistol);
        //activeGuns.add(shotgun);
        stage = new Stage(new FitViewport(800, 480, new CameraController(800, 480).getCamera()), batch);
        stage.addActor(touchpadController.getTouchpad());
        stage.addActor(rotationTouchpadController.getTouchpad());
//        touchpadController.getTouchpad().setVisible(false);

        Gdx.input.setInputProcessor(stage);
        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/button_blue.atlas"));
        Skin skin = new Skin();
        skin.addRegions(buttonAtlas);
        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = skin.getDrawable("blue_button_up");
        style.down = skin.getDrawable("blue_button_down");
        bombButton = new Button();
        game.setScreen(new ShopScreen(game, this, player, guns));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        returnedFromShop = TimeUtils.millis();
        freezeHealth = player.getHealth();
    }

    public void moveTouchPad(Touchpad touchpad, Vector2 vec){

        Gdx.app.log("TouchVec", vec.toString());
        touchpad.setPosition(vec.x - 75, vec.y -75);
        InputEvent fakeTouchEvent = new InputEvent();
        fakeTouchEvent.setType(InputEvent.Type.touchDown);
        fakeTouchEvent.setStageX(vec.x);
        fakeTouchEvent.setStageY(vec.y);
        touchpad.fire(fakeTouchEvent);
    }


    @Override
    public void render(float delta) {

        //Open GL stuff to set clear color and clear the screen
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cameraController.getCamera().position.set(player.getX(), player.getY(), 0);
        cameraController.getCamera().update();
        tiledMapRenderer.setView(cameraController.getCamera());
        tiledMapRenderer.render();
        now = TimeUtils.millis();
        xpGained = 0;
        ///////////////Player movement////////////////////
        //	Handles touchpads/rotation/movement/etc		//

        if(TimeUtils.millis() - returnedFromShop < 2000){
            player.setHealth(freezeHealth);
        }

        if (touchpadController.getTouchpad().isTouched()) {
            if (!rotationTouchpadController.getTouchpad().isTouched()) {
                player.rotate(new Vector2(touchpadController.xPercent(), touchpadController.yPercent()));
            }
        }
//        else if (Gdx.input.isTouched()){
//            Vector2 stageVec = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
//            if(stageVec.x < 400) {
//                moveTouchPad(touchpadController.getTouchpad(), stageVec);
//            }
//        }
        int healthLost = player.getHealth();
        player.move(
                touchpadController.xPercent(),
                touchpadController.yPercent(),
                getCollisionMapNodes(new Vector2(
                                touchpadController.xPercent(),
                                touchpadController.yPercent()),
                                player.currentNode)
        );
        if(player.getHealth() < healthLost){
            sc.hurtPlayer();
        }
        if (rotationTouchpadController.getTouchpad().isTouched()) {
            Vector2 rotVec = new Vector2(
                    rotationTouchpadController.xPercent(),
                    rotationTouchpadController.yPercent());
            player.rotate(rotVec);
            //fires a bullet when the right touchpad is touched

            if (player.getWeapon() != null) {
                if (now - lastShot > player.getWeapon().getFireRate()) {//TODO fix fire location to be the actual gun
                    if (player.getWeapon().getGunType() == GunType.SHOTGUN) {
                        //fire multiple bullets with spread
                        for (int i = 0; i < 6; i++) {
                            Bullet b = bp.obtain();
                            b.fireRandom(new Vector2(
                                            player.getX(),
                                            player.getY()),
                                    new Vector2(
                                            rotVec.x,
                                            rotVec.y),
                                    rotVec.angle(),
                                    player.getWeapon(),
                                    25
                            );
                            activeBullets.add(b);
                            lastShot = now;
                        }
                        sc.playSound("shotgun");
                    } else {
                        Bullet b = bp.obtain();
                        b.fire(new Vector2(
                                        player.getX(),
                                        player.getY()),
                                new Vector2(
                                        rotVec.x,
                                        rotVec.y),
                                rotVec.angle(),
                                player.getWeapon()
                        );
                        sc.playSound(b.gun.getSoundKey());
                        activeBullets.add(b);
                        lastShot = now;
                    }
                }
            }
        }
//        else if (Gdx.input.isTouched()){
//            Vector2 stageVec = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
//            if(stageVec.x > 400) {
//                moveTouchPad(rotationTouchpadController.getTouchpad(), stageVec);
//            }
//        }
        //Gdx.app.log("player mapnode", player.currentNode.toString());
        player.currentNode.playerInTile.removeIndex(0);//remove from old node
        player.currentNode = getCurrentMapNode(player.getX(), player.getY());//update node
        player.currentNode.playerInTile.add(player);//add player to updated node


        ////////////////////////////////////////////////

        batch.setProjectionMatrix(cameraController.getCamera().combined);
        batch.begin();
        for (int i = 0; i < activeExplosions.size; i++) {
            Explosion explosion = activeExplosions.get(i);
            explosion.update(Gdx.graphics.getDeltaTime());
            explosion.render(batch);
            if (explosion.remove) {
                activeExplosions.removeIndex(i);
            }
        }
        for (int i = 0; i < activeGuns.size; i++) {
            if (!activeGuns.get(i).pickedUp) {
                activeGuns.get(i).getGunSprite().draw(batch);
            } else {
                activeGuns.get(i).setPosition(0, 0);
                activeGuns.removeIndex(i);
            }
        }

        drawHealth(
                player.getX(),
                player.getY() - 12,
                50,
                5,
                batch,
                player.getHealth(),
                player.getMaxHealth());

        player.getPlayerSprite().draw(batch);


        ///////////////Bullets stuff//////////////////

        for (int i = 0; i < activeBullets.size; i++) {
            boolean isRemoved = false;
            Bullet b = activeBullets.get(i);
            b.update(); // update bullet
            bSprite.setRotation(b.angle - 90f);
            bSprite.setPosition(b.position.x, b.position.y);
            bSprite.draw(batch);
            Array<MapNode> collisionMapnodes = getCollisionMapNodes(b.direction, getCurrentMapNode(b.position.x, b.position.y));
            for (MapNode node : collisionMapnodes) {
                for (Rectangle wallRect : node.wallsInTile)
                    if (collisionController.checkX(b.bulletRect, bSprite, wallRect, node, b.position.x, b.direction.x, false) ||
                            collisionController.checkY(b.bulletRect, bSprite, wallRect, node, b.position.y, b.direction.y, false)) {
                        bp.free(b);
                        activeBullets.removeIndex(i);
                        isRemoved = true;
                    }
                for (Zombie zombie : node.zombiesInTile) {
                    //Gdx.app.log("zombie in tile", zombie.getZombieRect().toString());
                    if (collisionController.checkX(b.bulletRect, bSprite, zombie.getZombieRect(), node, b.position.x, b.direction.x, false) ||
                            collisionController.checkY(b.bulletRect, bSprite, zombie.getZombieRect(), node, b.position.y, b.direction.y, false)) {
                        zombie.hurt(b.gun.getDamage());
                        sc.hurtZombie();
                        node.removeZombieFromArray(zombie);
                        zombie.recoil(b.calculateDirection(b.gun.getKnockBack(), b.direction.angle()));
                        getCurrentMapNode(zombie.getX(), zombie.getY()).zombiesInTile.add(zombie);//update zombie array of node incase it was bumped to another node
                        Explosion explosion = new Explosion(b.position.x, b.position.y, explosionTextures, 16, Color.WHITE);
                        activeExplosions.add(explosion);
                        bp.free(b);
                        activeBullets.removeIndex(i);
                        isRemoved = true;
                    }
                }
            }
            if (outOfBounds(b.position.x, b.position.y) && !isRemoved) {
                bp.free(b);
                activeBullets.removeIndex(i);
            }

        }

        ////////////////////////////////////////////////

        ///////////////Zombie stuff////////////////////
        spawnZombie();
        for (int i = 0; i < activeZombies.size; i++) {
            Zombie zombie = activeZombies.get(i);
            MapNode curMapNode = getCurrentMapNode(zombie.getX(), zombie.getY());
            curMapNode.removeZombieFromArray(zombie);
            zombie.move(player.getX(), player.getY());

            float healthY = zombie.getY() + 45;
            int healthWidth = 43;
            if(zombie.isBoss){
                healthY = zombie.getY() + 65;
                healthWidth = 63;
            }

            if (zombie.getHealth() < zombie.getMaxHealth() && zombie.getHealth() > 0) {
                drawHealth(zombie.getX(),
                        healthY,
                        healthWidth,
                        5,
                        batch,
                        zombie.getHealth(),
                        zombie.getMaxHealth());
            }
            if (zombie.getHealth() <= 0) {
                activeZombies.removeIndex(i);
                activeExplosions.add(new Explosion(zombie.getX(), zombie.getY(), explosionTextures, 40, Color.GREEN));
                zp.free(zombie);
                xpGained += zombie.getXpValue();
                player.setMoney(player.getMoney() + zombie.getGp());
            } else {
                zombie.getZombieSprite().draw(batch);
                curMapNode = getCurrentMapNode(zombie.getX(), zombie.getY());
                curMapNode.zombiesInTile.add(zombie);
            }
        }
        spawnPowerUp();
        for(int i = 0; i < powerUps.size; i++){
            PowerUp powerUp = powerUps.get(i);
            if(powerUp.isCollected){
                sc.playPowerUpPickup();
                player.getPowerUp(powerUp.getType());
                powerUps.removeIndex(i);
                powerUp.getPowerUpSprite().setPosition(0,0);
                getCurrentMapNode(powerUp.getPowerUpRect().getX(), powerUp.getPowerUpRect().getX()).removePowerUpFromArray(powerUp);
            }else{
                powerUp.getPowerUpSprite().draw(batch);
            }
        }



        ////////////////////////////////////////

        game.font.getData().setScale(0.2f, 0.2f);
        float camY = cameraController.getCamera().position.y;
        float camX = cameraController.getCamera().position.x;
        game.font.draw(batch, "Level: " + Integer.toString(player.getLevel()), camX-385, camY+180);
        game.font.draw(batch, "Att points: " + Integer.toString(player.getAttPoints()),camX-385, camY+200);
        game.font.draw(batch, "gold: " + Integer.toString(player.getMoney()),camX-385, camY+220);
        game.font.draw(batch, "Next shop at: " + Integer.toString(player.getLevel() + 2-player.getLevel()%2), camX-385, camY+240);

        if(now - textDisplayedSince < 5000){
            game.font.setColor(Color.RED);
            game.font.draw(batch, swarmMessage, camX-100, camY+210);
            game.font.setColor(Color.WHITE);
        }
        if(player.isRegenActive()){
            game.font.setColor(Color.GREEN);
            game.font.draw(batch, "Regen active", camX - 385, camY+150);
            game.font.setColor(Color.WHITE);
        }
        if(player.isQuadActive()){
            game.font.setColor(Color.PURPLE);
            game.font.draw(batch, "Quad active", camX - 385, camY+130);
            game.font.setColor(Color.WHITE);
        }
        if(player.isRapidActive()){
            game.font.setColor(Color.BLUE);
            game.font.draw(batch, "Rapid fire active", camX - 385, camY+110);
            game.font.setColor(Color.WHITE);
        }
        if(player.isSpeedActive()){
            game.font.setColor(Color.YELLOW);
            game.font.draw(batch, "Speed active", camX-385, camY+90);
            game.font.setColor(Color.WHITE);
        }

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if(player.getXp(xpGained)){
            if(player.getLevel()%2 == 0){
                game.setScreen(new ShopScreen(game, this, player, guns));
            }
            sc.level();
            increaseDifficulty();
        }
        if (player.getHealth() <= 0) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

    }

    public static double getAngle(float x1, float y1, float x2, float y2){
        final double deltaY = (y2 - y1);
        final double deltaX = (x2 - x1);
        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }

    public MapNode getCurrentMapNode(float x, float y){
        int xNode = (int) x/mapNodeWidth;
        int yNode = (int) y/mapNodeHeight;
        return mapNodes[xNode][yNode];
    }

    public Array<MapNode> getCollisionMapNodes(Vector2 direction, MapNode curMapNode){
        Array<MapNode> collisonMapNodes = new Array<MapNode>();
        int xNode = curMapNode.getxNode();
        int yNode = curMapNode.getyNode();

        //direction is down + left and were not on the bottom or leftmost node
        if (direction.y < 0 && direction.x < 0 && xNode > 0 && yNode > 0) {
            collisonMapNodes.add(mapNodes[xNode - 1][yNode - 1]);//get lower left node
        }
        //direction is left and were not on the leftmost node
        if (direction.x < 0 && direction.y == 0 && xNode > 0) {
            collisonMapNodes.add(mapNodes[xNode - 1][yNode]);//get left node
        }
        //direction is right and were not on the rightmost node
        if (direction.x > 0 && direction.y == 0 && xNode < mapWidth/mapNodeWidth-1) {
            collisonMapNodes.add(mapNodes[xNode + 1][yNode]);//get right node
        }
        //direction is up and right and were not on the topright node
        if (direction.y > 0 && direction.x > 0 && xNode < mapWidth/mapNodeWidth-1 && yNode < mapHeight/mapNodeHeight-1) {
            collisonMapNodes.add(mapNodes[xNode + 1][yNode + 1]);//get upper right node
        }
        //direction is down and right and were not on the lowerright node
        if (direction.y < 0 && direction.x > 0 && yNode > 0 && xNode < mapWidth/mapNodeWidth-1) {
            collisonMapNodes.add(mapNodes[xNode + 1][yNode - 1]);//get lower right node
        }
        //direction is down and were not on the bottom node
        if (direction.y < 0 && direction.x == 0 && yNode > 0) {
            collisonMapNodes.add(mapNodes[xNode][yNode - 1]);//get top node
        }
        //direction is up and left and were not on the upperleft node
        if (direction.y > 0 &&  direction.x < 0  && xNode > 0 && yNode < mapHeight/mapNodeHeight-1) {
            collisonMapNodes.add(mapNodes[xNode - 1][yNode + 1]);//get upper left node
        }
        //direction is up and were not on the top node
        if (direction.y > 0 && direction.x == 0 && yNode < mapHeight/mapNodeHeight-1) {
            collisonMapNodes.add(mapNodes[xNode][yNode + 1]);//get top node
        }
        collisonMapNodes.add(curMapNode);
        return collisonMapNodes;
    }

    public void spawnZombie(){
        if(now - lastZombie > zombieSpawnRate){
            lastZombie = now;
            float zombieX = 50 + random.nextInt( 3150 - 50);
            float zombieY = 50 + random.nextInt(3150 - 50);
            Zombie zombie = zp.obtain();

            if(random.nextInt(33 - (int)(2*difficultyMultiplier)) == 1){
                zombie.sendBossZombie(zombieSprite, zombieSpeed, zombieX, zombieY);
            }else {
                zombie.sendZombie(zombieSprite, zombieSpeed, zombieX, zombieY, zombieDamage);
            }
            activeZombies.add(zombie);
            sc.zombieAttack();

            if(random.nextInt(43 - (int)(2*difficultyMultiplier)) == 1){
                spawnSwarm((int) (difficultyMultiplier*2.5 * 3));
            }

        }

    }

    public void spawnSwarm(int numZombies){
        float swarmCenterX = 50 + random.nextInt(3150 - 50*numZombies);
        float swarmCenterY = 50 + random.nextInt(3150 - 50*numZombies);

        for(int i = 0; i < numZombies; i++){
            Zombie zombie = zp.obtain();
            float zombieOffsetX = random.nextInt(32*numZombies);
            float zombieOffsetY = random.nextInt(32*numZombies);
            zombie.sendZombie(
                    zombieSprite,
                    zombieSpeed,
                    swarmCenterX + zombieOffsetX,
                    swarmCenterY + zombieOffsetY,
                    zombieDamage);
            activeZombies.add(zombie);
        }
        textDisplayedSince = now;
        //TODO display 'huge wave' message on screen
    }


    public void drawHealth(float x, float y, float width, float height, SpriteBatch batch, int health, int maxHealth){
        float healthPerc = (float) health / (float) maxHealth;
        if(healthPerc > 0.6f){
            batch.setColor(Color.GREEN);
        }else if(healthPerc > 0.2f){
            batch.setColor(Color.ORANGE);
        }else{
            batch.setColor(Color.RED);
        }
        batch.draw(blank, x, y, width * healthPerc, height);
        batch.setColor(Color.WHITE);
    }

    public boolean outOfBounds(float x, float y){
        return x <= 0 || y <= 0 || x > 3170 || y > 3170;
    }

    public void increaseDifficulty(){
        if(TimeUtils.millis() - lastDifficultyIncrease > 30000){
            difficultyMultiplier += 0.2f;
            lastDifficultyIncrease = TimeUtils.millis();
        }

        if(zombieSpawnRate > 100){
            zombieSpawnRate -= 300*difficultyMultiplier;
            if(zombieSpawnRate < 250){
                zombieSpawnRate = 250;
            }
        }
        zombieSpeed += 0.1 * difficultyMultiplier;
        //zombieDamage += (int) (zombieDamage/2)*(difficultyMultiplier/2);
    }

    public void spawnPowerUp(){
        if(now - lastPowerUp > 10000){
            Random random = new Random();
            float randX = 50 + random.nextInt( 3150 - 50);
            float randY = 50 + random.nextInt( 3150 - 50);
            PowerUPType type = randomEnum(PowerUPType.class);
            Sprite powerUpSprite = powerUpSprites.get(type.toString());
            PowerUp powerUp = new PowerUp(powerUpSprite, new Rectangle(randX, randY, 30,30), type);
            powerUpSprite.setSize(30,30);
            powerUpSprite.setPosition(randX, randY);
            getCurrentMapNode(randX, randY).powerUpsInTile.add(powerUp);
            powerUps.add(powerUp);
            lastPowerUp = now;
        }
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        Random random = new Random();
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        sc.dispose();
    }
}
