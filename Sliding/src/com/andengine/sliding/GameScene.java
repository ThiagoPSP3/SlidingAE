package com.andengine.sliding;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.andengine.sliding.SceneManager.SceneType;
import com.badlogic.gdx.math.Vector2;

public class GameScene extends BaseScene
{
	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	private PhysicsWorld physicsWorld;
	
    @Override
    public void createScene()
    {
    	createBackground();
        createHUD();
        createPhysics();
    }
    
    private void createBackground()
    {
        setBackground(new Background(Color.BLUE));
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
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false); 
        registerUpdateHandler(physicsWorld);
    }
    
    private void addToScore(int i)
    {
        score += i;
        scoreText.setText("Score: " + score);
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