package com.andengine.sliding;

import java.util.ArrayList;
import java.util.List;

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
	private Piece teste0;
	private Piece teste1;
	private Piece teste2;
	private Piece teste3;
	private Piece teste4;
	private Piece teste5;
	private Piece teste6;
	private Piece teste7;
	private Piece teste8;
	
	
    @Override
    public void createScene()
    {
    	createBackground();
        createHUD();
        createPhysics();
    	/*for(int i=0;i<3;i++){
    		for(int j=0;j<3;j++){
    			resourcesManager.puzzle_region.setCurrentTileIndex(3*i+j);
    			puzzle.add(new Piece(0, 0, resourcesManager.puzzle_region, vbom));
    	    	puzzle.get(3*i+j).setPosition(j*240,i*240 + 360);
    	    	attachChild(puzzle.get(3*i+j));
    		}
    	}*/
        puzzle = new Piece[9];	
    	resourcesManager.puzzle_region.setCurrentTileIndex(0);
        teste0 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	puzzle[0] = teste0;
    	teste0.setPosition(0, 360);
    	attachChild(teste0);
    	
    	resourcesManager.puzzle_region.setCurrentTileIndex(1);
        teste1 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	teste1.setPosition(240, 360);
    	attachChild(teste1);
    	
    	resourcesManager.puzzle_region.setCurrentTileIndex(2);
        teste2 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	teste2.setPosition(480, 360);
    	attachChild(teste2);
    	
    	resourcesManager.puzzle_region.setCurrentTileIndex(3);
        teste3 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	teste3.setPosition(0, 600);
    	attachChild(teste3);
    	
    	resourcesManager.puzzle_region.setCurrentTileIndex(4);
        teste4 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	teste4.setPosition(240, 600);
    	attachChild(teste4);
    	
    	resourcesManager.puzzle_region.setCurrentTileIndex(5);
        teste5 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	teste5.setPosition(480, 600);
    	attachChild(teste5);
    	
    	resourcesManager.puzzle_region.setCurrentTileIndex(6);
        teste6 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	teste6.setPosition(0, 840);
    	attachChild(teste6);
    	
    	resourcesManager.puzzle_region.setCurrentTileIndex(7);
        teste7 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	teste7.setPosition(240, 840);
    	attachChild(teste7);
    	
    	resourcesManager.puzzle_region.setCurrentTileIndex(8);
        teste8 = new Piece(0, 0, resourcesManager.puzzle_region, vbom);
    	teste8.setPosition(480, 840);
    	attachChild(teste8);
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