package com.andengine.sliding;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.andengine.sliding.SceneManager.SceneType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameScene extends BaseScene implements IOnAreaTouchListener
{
	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	private PhysicsWorld physicsWorld;
	FixtureDef objectFixtureDef;
	
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 1280;
	
	
    @Override
    public void createScene()
    {
    	createBackground();
        createHUD();
        createPhysics();
        final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 202, CAMERA_WIDTH, 2, vbom);
		final Rectangle roof = new Rectangle(0, 360, CAMERA_WIDTH, 2, vbom);
		final Rectangle left = new Rectangle(0, 360, 2, CAMERA_HEIGHT-560, vbom);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 360, 2, CAMERA_HEIGHT-560, vbom);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		attachChild(ground);
		attachChild(roof);
		attachChild(left);
		attachChild(right);

		registerUpdateHandler(physicsWorld);
    	for(int i=0;i<3;i++){
    		for(int j=0;j<3;j++){
    			if(3*i+j<8){
	    			resourcesManager.puzzle_region.setCurrentTileIndex(3*i+j);
	    			Piece puzzle = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
	    	    	puzzle.setPosition(j*233+2,i*233+2 + 360);
	    	    	Body body = PhysicsFactory.createBoxBody(physicsWorld, puzzle, BodyType.DynamicBody, objectFixtureDef);    	    	
	    	    	attachChild(puzzle);
	    	    	physicsWorld.registerPhysicsConnector(new PhysicsConnector(puzzle, body, true, true));
	    			puzzle.setUserData(body);
	    	    	registerTouchArea(puzzle);
	    			setTouchAreaBindingOnActionDownEnabled(true);
    			}
    		}
    	}
    }
    
    private void createBackground()
    {
        setBackground(new Background(0,0,0));
        
    }
    
    private void createHUD()
    {
        gameHUD = new HUD(); 
        
        // CREATE SCORE TEXT
        scoreText = new Text(0, 0, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        //scoreText.setAnchorCenter(0, 0);    
        scoreText.setText("Score: 0");
        gameHUD.attachChild(scoreText);
        camera.setHUD(gameHUD);
    }
    
    private void createPhysics()
    {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false); 
        registerUpdateHandler(physicsWorld);
        objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
        setOnAreaTouchListener(this);
    }
    
    private void addToScore(int i)
    {
        score += i;
        scoreText.setText("Score: " + score);
    }
    @Override
	public boolean onAreaTouched( final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea,final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionMove()) {
			addToScore(5);
			final Piece piece = (Piece) pTouchArea;
			final Body body = (Body) piece.getUserData();
			body.setTransform(pSceneTouchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, body.getAngle());
			return true;
		}

		return false;
	}

    @Override
    public void onBackKeyPressed()
    {
    	SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene()
    {
    	camera.setHUD(null);
        camera.setCenter(360, 640);

        // TODO code responsible for disposing scene
        // removing all game scene objects.
    }

}