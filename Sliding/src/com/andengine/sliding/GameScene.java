package com.andengine.sliding;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import com.andengine.sliding.SceneManager.SceneType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class GameScene extends BaseScene implements IOnAreaTouchListener
{
	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	FixtureDef objectFixtureDef;
	public int blankX,blankY,iniPosX,iniPosY,curPosX,curPosY;
	
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 1280;
	
	public List<Piece> puzzle;
	
	public static final int step = 233,topMargin = 360,bottonMargin = 200,wallThickness = 2,puzzleSize=3,puzzleMargin=4;
	
	public enum Dir{
		Xdown,Xup,Ydown,Yup,Stop
	}
	Dir dir;
	
    @Override
    public void createScene()
    {
    	createBackground();
        createHUD();
        createRectangle();
        createPuzzle();
        setOnAreaTouchListener(this);     	    	
    }    
    private void createPuzzle() 
    {
    	puzzle = new ArrayList<Piece>();
    	if(!puzzle.isEmpty())
    		puzzle.clear();
    	for(int i=0;i<puzzleSize;i++){
    		for(int j=0;j<puzzleSize;j++){
    			if(puzzleSize*i+j<puzzleSize*puzzleSize-1){
	    			resourcesManager.puzzle_region.setCurrentTileIndex(puzzleSize*i+j);
	    			Piece piece = new Piece(0, 0, resourcesManager.puzzle_region, vbom, puzzleSize*i+j);
	    	    	piece.setPosition(j*step+wallThickness+(j+1)*puzzleMargin,i*step+wallThickness + topMargin+(i+1)*puzzleMargin);
	    	    	attachChild(piece);
	    	    	registerTouchArea(piece);
	    			setTouchAreaBindingOnActionDownEnabled(true);
	    			puzzle.add(piece);
    			}
    			else{
    				blankX = j*step+wallThickness+(j+1)*puzzleMargin;
    				blankY = i*step+wallThickness+topMargin+(i+1)*puzzleMargin;
    			}
    		}
    	}
	}
	private void createRectangle() 
	{ 
    	final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - bottonMargin-wallThickness, CAMERA_WIDTH, wallThickness, vbom);
		final Rectangle roof = new Rectangle(0, topMargin, CAMERA_WIDTH, wallThickness, vbom);
		final Rectangle left = new Rectangle(0, topMargin, wallThickness, CAMERA_HEIGHT-topMargin-bottonMargin, vbom);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - wallThickness, topMargin, wallThickness, CAMERA_HEIGHT-topMargin-bottonMargin, vbom);
		attachChild(ground);
		attachChild(roof);
		attachChild(left);
		attachChild(right);	
	}
	private void createBackground()
    {
        setBackground(new Background(0,0,0));        
    }    
    private void createHUD()
    {
        gameHUD = new HUD();         
        // CREATE SCORE TEXT
        scoreText = new Text(0, 0, resourcesManager.font, "Moves: 0123456789,", new TextOptions(HorizontalAlign.LEFT), vbom);
        //scoreText.setAnchorCenter(0, 0);    
        scoreText.setText("Moves: 0");
        gameHUD.attachChild(scoreText);
        camera.setHUD(gameHUD);
    }    
    private void addToScore(int i)
    {
        score += i;
        scoreText.setText("Moves: " + score);
    }
    @Override
	public boolean onAreaTouched( final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea,final float pTouchAreaLocalX, final float pTouchAreaLocalY)
    {  	
    	if(pSceneTouchEvent.isActionDown()) {
        	final Piece piece = (Piece) pTouchArea;
        	iniPosX = (int)piece.getX();
        	iniPosY = (int)piece.getY();
    		if(iniPosX==blankX){
    			 if(iniPosY==blankY + piece.size + puzzleMargin)
    				 dir = Dir.Ydown;    			 
    			 else if(iniPosY==blankY - piece.size - puzzleMargin)
    				 dir = Dir.Yup;
    		}
    		else if(iniPosY==blankY){
	   			 if(iniPosX==blankX + piece.size + puzzleMargin)
	   				dir = Dir.Xdown;	 
	   			 else if(iniPosX==blankX - piece.size - puzzleMargin)
	   				dir = Dir.Xup;
    		}
    		else
    			dir = Dir.Stop;
			return true;
		}
    	if(pSceneTouchEvent.isActionMove()) {
        	final Piece piece = (Piece) pTouchArea;
    		curPosX = (int)pSceneTouchEvent.getX();
    		curPosY = (int)pSceneTouchEvent.getY();
        	switch(dir){
    		case Xdown:    			
    			if(curPosX-piece.size/2<iniPosX && curPosX-piece.size/2>iniPosX-piece.size-puzzleMargin)
    				piece.setPosition(curPosX-piece.size/2, iniPosY);
    			break;
    		case Xup:
    			if(curPosX-piece.size/2>iniPosX && curPosX-piece.size/2<iniPosX+piece.size+puzzleMargin)
    				piece.setPosition(curPosX-piece.size/2, iniPosY);
				break;
    		case Ydown:
    			if(curPosY-piece.size/2<iniPosY &&curPosY-piece.size/2>iniPosY-piece.size-puzzleMargin)
    				piece.setPosition(iniPosX,curPosY-piece.size/2);
    			break;
    		case Yup:
    			if(curPosY-piece.size/2>iniPosY &&curPosY-piece.size/2<iniPosY+piece.size+puzzleMargin)
    				piece.setPosition(iniPosX,curPosY-piece.size/2);
    			break;    
    		case Stop:
    			break;
    		}   		
    		return true;
    	} 
    	if(pSceneTouchEvent.isActionUp()) {
        	final Piece piece = (Piece) pTouchArea;
    		curPosX = (int)pSceneTouchEvent.getX();
    		curPosY = (int)pSceneTouchEvent.getY();
			switch(dir){
	    		case Xdown:
	    			if(iniPosX - curPosX+piece.size/2 >= (piece.size + puzzleMargin)/2){
		    			piece.setPosition(iniPosX - piece.size - puzzleMargin, iniPosY);
						blankX = iniPosX;
						blankY = iniPosY;
						addToScore(1);
						piece.gridPosAct-=3;
	    			}
					else
						piece.setPosition(iniPosX,iniPosY);
	    			break;
	    		case Xup:
	    			if(curPosX-piece.size/2 - iniPosX >= (piece.size + puzzleMargin)/2){
		    			piece.setPosition(iniPosX + piece.size + puzzleMargin, iniPosY);
						blankX = iniPosX;
						blankY = iniPosY;
						addToScore(1);
						piece.gridPosAct+=3;
					}
					else
						piece.setPosition(iniPosX,iniPosY);
	    			break;
	    		case Ydown:
	    			if(iniPosY - curPosY+piece.size/2 >= (piece.size + puzzleMargin)/2){
		    			piece.setPosition(iniPosX, iniPosY - piece.size - puzzleMargin);
						blankX = iniPosX;
						blankY = iniPosY;
						addToScore(1);
						piece.gridPosAct--;
					}
					else
						piece.setPosition(iniPosX,iniPosY);
	    			break;
	    		case Yup:
	    			if(curPosY-piece.size/2 - iniPosY >= (piece.size + puzzleMargin)/2){
		    			piece.setPosition(iniPosX, iniPosY + piece.size + puzzleMargin);
						blankX = iniPosX;
						blankY = iniPosY;
						addToScore(1);
						piece.gridPosAct++;
					}
					else
						piece.setPosition(iniPosX,iniPosY);
					break;
	    		case Stop:
	    			break;
			}
    		dir = Dir.Stop;
    		int i =1;
    		for(Piece pos:puzzle){
    			i++;
    			if(pos.gridPosAct != pos.gridPosIni)
    				break;
    			if(i == puzzleSize*puzzleSize)
    				puzzleComplete();
    		}
			return true;
    	}
		return false;
	}
    private void puzzleComplete() {
        scoreText.setText("You Win");
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