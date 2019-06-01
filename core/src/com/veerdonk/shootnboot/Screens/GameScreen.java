package com.veerdonk.shootnboot.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.veerdonk.shootnboot.Controllers.CameraController;
import com.veerdonk.shootnboot.Controllers.CollisionController;
import com.veerdonk.shootnboot.Model.Explosion;
import com.veerdonk.shootnboot.Model.Player;
import com.veerdonk.shootnboot.Controllers.TouchpadController;
import com.veerdonk.shootnboot.Model.Zombie;
import com.veerdonk.shootnboot.Model.Bullet;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.GunType;
import com.veerdonk.shootnboot.Model.MapNode;
import com.veerdonk.shootnboot.Pools.BulletPool;
import com.veerdonk.shootnboot.Pools.ZombiePool;
import com.veerdonk.shootnboot.ShootNBoot;

import java.util.Random;

public class GameScreen implements Screen {
    final ShootNBoot game;
    private Button machineButton;
    private Button gunButton;
    private Button submachineButton;
    private float delta = 0;
    private Player player;
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
    private Gun firstGun;
    private Gun firstShotGun;
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

    private SpriteBatch batch;
    private Stage stage;

    private boolean beenToShop = true;

    public GameScreen(final ShootNBoot game) {
        this.game = game;
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
        zombieSpawnRate = 5000;//spawnrate in millis
        zombieSpeed = 1.5f;
        lastZombie = now;
        zombieDamage = 5;
        zombieSprite = textureAtlas.createSprite("zoimbie1_hold");

        batch = new SpriteBatch();
        cameraController = new CameraController(800, 480);
        Sprite playerSprite = textureAtlas.createSprite("survivor1_stand");
        player = new Player(
                playerSprite,
                5,
                200,
                200,
                getCurrentMapNode(200,200)
        );
        player.currentNode.playerInTile.add(player);
        touchpadController = new TouchpadController(
                new Texture(Gdx.files.internal("touchBack.png")),
                new Texture(Gdx.files.internal("touchKnob.png")),
                15,
                15,
                150,
                150
        );
        rotationTouchpadController = new TouchpadController(
                new Texture(Gdx.files.internal("touchBack.png")),
                new Texture(Gdx.files.internal("touchKnob.png")),
                (int) cameraController.getCamera().viewportWidth - 150 - 15,
                15,
                150,
                150
        );
        firstGun = new Gun(textureAtlas.createSprite("weapon_gun"), textureAtlas.createSprite("survivor1_gun"), GunType.PISTOL, 200f, 100f);
        firstShotGun = new Gun(textureAtlas.createSprite("weapon_shotgun"), textureAtlas.createSprite("survivor1_shotgun"), GunType.SHOTGUN, 400f, 100f);
        getCurrentMapNode(firstGun.getX(), firstGun.getY()).gunsInTile.add(firstGun);
        getCurrentMapNode(firstShotGun.getX(), firstShotGun.getY()).gunsInTile.add(firstShotGun);
        activeGuns.add(firstGun);
        activeGuns.add(firstShotGun);
        stage = new Stage(new FitViewport(800, 480, new CameraController(800, 480).getCamera()), batch);
        stage.addActor(touchpadController.getTouchpad());
        stage.addActor(rotationTouchpadController.getTouchpad());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        this.render(Gdx.graphics.getDeltaTime());
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

        if (touchpadController.getTouchpad().isTouched()) {
            if (!rotationTouchpadController.getTouchpad().isTouched()) {
                player.rotate(new Vector2(touchpadController.xPercent(), touchpadController.yPercent()));
            }
        }
        player.move(
                touchpadController.xPercent(),
                touchpadController.yPercent(),
                getCollisionMapNodes(new Vector2(
                                touchpadController.xPercent(),
                                touchpadController.yPercent()),
                        player.currentNode)
        );

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
                        activeBullets.add(b);
                        lastShot = now;
                    }
                }
            }
        }
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
//			for(Zombie z : curMapNode.zombiesInTile){
//				Gdx.app.log("in tile before", z.toString());
//			}
            curMapNode.removeZombieFromArray(zombie);
            zombie.move(player.getX(), player.getY());

            if (zombie.getHealth() < 100) {
                drawHealth(zombie.getX(),
                        zombie.getY() + 45,
                        43,
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
            } else {
                zombie.getZombieSprite().draw(batch);
                curMapNode = getCurrentMapNode(zombie.getX(), zombie.getY());
                curMapNode.zombiesInTile.add(zombie);
            }
        }
        ////////////////////////////////////////


        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if(player.getXp(xpGained)){
            if(player.getLevel() == 2){
                game.setScreen(new ShopScreen(game, this));
            }
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
            Random random = new Random();

            float zombieX = 50 + random.nextInt( 3150 - 50);
            float zombieY = 50 + random.nextInt(3150 - 50);
            Zombie zombie = zp.obtain();
            zombie.sendZombie(zombieSprite, zombieSpeed, zombieX, zombieY, zombieDamage);
            activeZombies.add(zombie);
        }

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

    public void increaseDifficulty(int kills, long timePassed){
//		if(kills % )
        if(zombieSpawnRate > 100){
            zombieSpawnRate -= 200;
        }

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
    }
}
