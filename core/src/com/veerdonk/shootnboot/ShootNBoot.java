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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.veerdonk.shootnboot.Controllers.CameraController;
import com.veerdonk.shootnboot.Controllers.CollisionController;
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
	private CollisionController collisionController;

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
	private int mapNodeHeight = 64;
	private int mapNodeWidth = 64;
	private MapNode [] [] mapNodes;


	private SpriteBatch batch;
	private Stage stage;

	@Override
	public void create () {
		textureAtlas = new TextureAtlas(Gdx.files.internal("Characters.atlas"));
		bSprite = textureAtlas.createSprite("bulletYellow");
		tiledMap = new TmxMapLoader().load("shootNBootMap.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		collisionController = new CollisionController();

		mapProperties = tiledMap.getProperties();
		mapHeight = mapProperties.get("mapHeight", Integer.class);
		mapWidth = mapProperties.get("mapWidth", Integer.class);
		mapNodes = new MapNode[mapWidth][mapHeight];
		//Creates all the mapnodes (expensive)
		for(int i = 0; i < mapWidth/mapNodeWidth; i++){
			for(int j = 0; j < mapHeight/mapNodeHeight; j++){
				mapNodes[i][j] = new MapNode(i*mapNodeWidth,j*mapNodeHeight,mapNodeWidth,mapNodeHeight, i, j);
		}}
		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("background"); //TODO check whether this is right
		int total = 0;
		for(int x = 0; x < mapWidth/layer.getTileWidth(); x++){
		    for(int y = 0; y < mapHeight/layer.getTileHeight(); y++){
		        if(layer.getCell(x,y) != null && layer.getCell(x, y).getTile().getProperties().containsKey("blocked")){
		            mapNodes[x*32/mapNodeWidth][y*32/mapNodeHeight].wallsInTile.add(new Rectangle(x*32, y*32, 32, 32));
		            total ++;

                }
            }
        }
		Gdx.app.log("total walls created",Integer.toString(total));
//		for(RectangleMapObject rectangleMapObject : mapObjects.getByType(RectangleMapObject.class)){
//			int wallXNode = (int) rectangleMapObject.getRectangle().x/mapWidth;
//			int wallYNode = (int) rectangleMapObject.getRectangle().y/mapHeight;
//			mapNodes[wallXNode][wallYNode].wallsInTile.add(rectangleMapObject.getRectangle());
//		}
		Gdx.app.log("walls in 48,2: ", Integer.toString(mapNodes[49][2].getxNode()));
		batch = new SpriteBatch();
		cameraController = new CameraController(800, 480);
		Sprite playerSprite = textureAtlas.createSprite("survivor1_stand");
		player = new Player(
				playerSprite,
				5,
				200,
				200
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

		if(touchpadController.getTouchpad().isTouched()) {
			if(!rotationTouchpadController.getTouchpad().isTouched()){
				player.rotate(new Vector2(touchpadController.xPercent(), touchpadController.yPercent()));
			}
			player.move(
					touchpadController.xPercent(),
					touchpadController.yPercent(),
					getCollisionMapNodes(new Vector2(
									touchpadController.xPercent(),
									touchpadController.yPercent()),
							getCurrentMapNode(player.getX(), player.getY()))
			);
		}
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
					new Vector2(
					        rotVec.x,
					    rotVec.y),
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
			bSprite.setRotation(b.angle - 90f);
			bSprite.setPosition(b.position.x, b.position.y);
			bSprite.draw(batch);
			Array<MapNode> collisionMapnodes = getCollisionMapNodes(b.direction, getCurrentMapNode(b.position.x, b.position.y));
			for(MapNode node : collisionMapnodes){
				for(Rectangle wallRect : node.wallsInTile)
					if(collisionController.checkX(b.bulletRect, bSprite, wallRect, node, b.position.x, b.direction.x) ||
							collisionController.checkY(b.bulletRect, bSprite, wallRect, node, b.position.y, b.direction.y)){
						b.reset();
						activeBullets.removeIndex(i);
					}
			}
			if(outOfBounds(b.position.x, b.position.y)){
				b.reset();
				activeBullets.removeIndex(i);
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
			if(node.zombiesInTile != null) {
				for (Zombie z : node.zombiesInTile) {
					if (b.bulletRect.overlaps(z.getZombieRect())) {
						z.getShot(b);
						return true;
					}
				}
			}
			if(node.playerInTile != null) {
				for (Player p : node.playerInTile) {
					if (b.bulletRect.overlaps(p.getPlayerRect())) {
						p.getShot(b);
						return true;
					}
				}
			}
			if(node.wallsInTile != null) {
				for (Rectangle rect : node.wallsInTile) {
					if (b.bulletRect.overlaps(rect)) {
						return true; //walls dont mind getting shot
					}
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

	public boolean outOfBounds(float x, float y){
		return x < 0 || y < 0 || x > 3200 || y > 3200;
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
