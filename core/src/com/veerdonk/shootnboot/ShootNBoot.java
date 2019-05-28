package com.veerdonk.shootnboot;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.veerdonk.shootnboot.Controllers.CameraController;
import com.veerdonk.shootnboot.Controllers.Player;
import com.veerdonk.shootnboot.Controllers.TouchpadController;
import com.veerdonk.shootnboot.Controllers.Zombie;
import com.veerdonk.shootnboot.Model.Bullet;
import com.veerdonk.shootnboot.Model.Gun;
import com.veerdonk.shootnboot.Model.GunType;
import com.veerdonk.shootnboot.Model.MapNode;
import com.veerdonk.shootnboot.Pools.BulletPool;

public class ShootNBoot extends ApplicationAdapter {
	private Player player;
	private CameraController cameraController;
	private TouchpadController touchpadController;
	private TouchpadController rotationTouchpadController;

	private final Array<Bullet> activeBullets = new Array<Bullet>();
	private final BulletPool bp = new BulletPool();
	private Sprite bSprite;

	private TextureAtlas textureAtlas;
	private Gun firstGun;

	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	MapProperties mapProperties;
	int mapHeight;
	int mapWidth;
	private int mapNodeHeight = 128;
	private int mapNodeWidth = 128;
	private MapNode [] [] mapNodes;

	private SpriteBatch batch;
	private Stage stage;

	@Override
	public void create () {
		textureAtlas = new TextureAtlas(Gdx.files.internal("Characters.atlas"));
		bSprite = textureAtlas.createSprite("bulletYellow");
		tiledMap = new TmxMapLoader().load("shootNBootMap.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		mapProperties = tiledMap.getProperties();
		mapHeight = mapProperties.get("mapHeight", Integer.class);
		mapWidth = mapProperties.get("mapWidth", Integer.class);
		mapNodes = new MapNode[mapWidth][mapHeight];
		//Creates all the mapnodes (expensive)
		for(int i = 0; i < mapWidth/mapNodeWidth; i++){
			for(int j = 0; j < mapHeight/mapNodeHeight; j++){
				mapNodes[i][j] = new MapNode(i*mapNodeWidth,j*mapNodeHeight,mapNodeWidth,mapNodeHeight, i, j);
		}}

		batch = new SpriteBatch();
		cameraController = new CameraController(800, 480);
		Sprite playerSprite = textureAtlas.createSprite("survivor1_stand");
		player = new Player(
				playerSprite,
				5,
				400,
				50,
				100,
				bp
		);
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

		stage = new Stage(new FitViewport(800, 480, new CameraController(800, 480).getCamera()), batch);
		stage.addActor(touchpadController.getTouchpad());
		stage.addActor(rotationTouchpadController.getTouchpad());
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		//Open GL stuff to set clear color and clear the screen
		Gdx.gl.glClearColor(0, 0 ,0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cameraController.getCamera().position.set(player.getX(), player.getY(), 0);
		cameraController.getCamera().update();
		tiledMapRenderer.setView(cameraController.getCamera());
		tiledMapRenderer.render();

		player.move(
				touchpadController.xPercent(),
				touchpadController.yPercent()
		);
		if(rotationTouchpadController.getTouchpad().isTouched()){
			Vector2 rotVec = new Vector2(
					rotationTouchpadController.xPercent(),
					rotationTouchpadController.yPercent());
			player.rotate(rotVec);
            Gdx.app.log("KnobX", Float.toString(rotVec.x));
            Gdx.app.log("KnobY", Float.toString(rotVec.y));
			//fires a bullet when the right touchpad is touched
			Bullet b = bp.obtain();
			b.fire( new Vector2(
					player.getX(),
					player.getY()),
					rotVec.x,
					rotVec.y,
					rotVec.angle(),
                    player.getWeapon()
					);
			activeBullets.add(b);
		}

		batch.setProjectionMatrix(cameraController.getCamera().combined);
		batch.begin();

		firstGun.getGunSprite().draw(batch);
		player.getPlayerSprite().draw(batch);

		for(int i = 0; i < activeBullets.size; i++){
			Bullet b = activeBullets.get(i);
			b.update(); // update bullet
			if(checkProjectileCollisions(b, getCollisionMapNodes(b.direction, getCurrentMapNode(b.position.x, b.position.y)))){
				//bullet has collided with something -> return it to pool
				b.reset();
				activeBullets.removeIndex(i);
			}else{
				bSprite.setRotation(b.angle - 90f);
				bSprite.setPosition(b.position.x, b.position.y);
				bSprite.draw(batch);
			}
		}

		batch.end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public static double getAngle(float x1, float y1, float x2, float y2){
		final double deltaY = (y2 - y1);
		final double deltaX = (x2 - x1);
		final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
		return (result < 0) ? (360d + result) : result;
	}

	public boolean checkProjectileCollisions(Bullet b, Array<MapNode> nodesToCheck){
		for(MapNode node : nodesToCheck){
			for(Zombie z : node.zombiesInTile){
				if(b.bulletRect.overlaps(z.getZombieRect())){
					z.getShot(b);
					return true;
				}
			}
			for(Player p : node.playerInTile){
				if(b.bulletRect.overlaps(p.getPlayerRect())){
					p.getShot(b);
					return true;
				}
			}
		}
		return false;
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
		if(direction.x < 0){
			collisonMapNodes.add(mapNodes[xNode-1][yNode]);//get left node
		}else if(direction.x > 0){
			collisonMapNodes.add(mapNodes[xNode+1][yNode]);//get right node
		}
		if(direction.y < 0){
			collisonMapNodes.add(mapNodes[xNode][yNode-1]);//get top node
		}else if(direction.y > 0){
			collisonMapNodes.add(mapNodes[xNode][yNode+1]);//get bottom node
		}
		if(direction.y < 0 && direction.x < 0){
			collisonMapNodes.add(mapNodes[xNode-1][yNode-1]);//get lower left node
		}else if(direction.y > 0 && direction.x > 0){
			collisonMapNodes.add(mapNodes[xNode+1][yNode+1]);//get upper right node
		}
		if(direction.y < 0 && direction.x > 0){
			collisonMapNodes.add(mapNodes[xNode+1][yNode-1]);//get lower right node
		}else if (direction.y > 0 && direction.x < 0){
			collisonMapNodes.add(mapNodes[xNode-1][yNode+1]);//get upper left node
		}
		return collisonMapNodes;
	}

	@Override
	public void dispose () {
		batch.dispose();
		player.dispose();
	}
}
