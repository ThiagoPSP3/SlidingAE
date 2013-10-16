package com.blackice.sliding;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.blackice.sliding.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnAreaTouchListener
{
	private HUD gameHUD;
	public Text scoreText;
	public int score = 0;
	
	Puzzle puzzle;
	
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 1280;
	
    @Override
    public void createScene()
    {
    	createBackground();
        createHUD();
        puzzle = new Puzzle(this);
        setOnAreaTouchListener(this);     	    	
    }
	private void createBackground()
    {
		Color color=Color.BLACK;
        setBackground(new Background(color));        
    }    
    private void createHUD()
    {
        gameHUD = new HUD();
        scoreText = new Text(0, 0, resourcesManager.font, "Moves: 0123456789,", new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setText("Moves: 0");
        gameHUD.attachChild(scoreText);
        camera.setHUD(gameHUD);
    }    
    public void addToScore(int i)
    {
        score += i;
        scoreText.setText("Moves: " + score);
    }
    @Override
	public boolean onAreaTouched( final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea,final float pTouchAreaLocalX, final float pTouchAreaLocalY)
    {  	
    	if(pSceneTouchEvent.isActionDown()) {
    		return puzzle.onTouchDownHandler(pTouchArea);
		}
    	else if(pSceneTouchEvent.isActionMove()) {        	
    		return puzzle.onTouchMoveHandler(pSceneTouchEvent, pTouchArea);
    	} 
    	else if(pSceneTouchEvent.isActionUp()) {        	
			return puzzle.onTouchUpHandler(pSceneTouchEvent,pTouchArea);
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
        camera.setCenter(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
        // TODO code responsible for disposing scene
        // removing all game scene objects.
    }
}