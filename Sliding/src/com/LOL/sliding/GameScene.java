package com.LOL.sliding;

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

import com.LOL.sliding.SceneManager.SceneType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class GameScene extends BaseScene implements IOnAreaTouchListener
{
	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	FixtureDef objectFixtureDef;
	public Vector2 blank,iniPos,curPos;
	
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 1280;
	
	public List<Piece> pieces;
	
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
    	pieces = new ArrayList<Piece>();
    	blank = new Vector2();iniPos = new Vector2();curPos = new Vector2();
    	for(int i=0;i<puzzleSize;i++)
    		for(int j=0;j<puzzleSize;j++)
    			if(puzzleSize*i+j<puzzleSize*puzzleSize-1)//Doesn's enter here if it's the last piece because it's where we put the blank
	    			addPiece(i,j);
    			else
    				blank.set(j*step+wallThickness+(j+1)*puzzleMargin, i*step+wallThickness+topMargin+(i+1)*puzzleMargin);
	}
    private void addPiece(int x , int y){
    	resourcesManager.puzzle_region.setCurrentTileIndex(puzzleSize*x+y);
		Piece piece = new Piece(0, 0, resourcesManager.puzzle_region, vbom, puzzleSize*x+y);
    	piece.setPosition(y*step+wallThickness+(y+1)*puzzleMargin,x*step+wallThickness + topMargin+(x+1)*puzzleMargin);
    	attachChild(piece);
    	registerTouchArea(piece);
		setTouchAreaBindingOnActionDownEnabled(true);
		pieces.add(piece);
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
    public void addToScore(int i)
    {
        score += i;
        scoreText.setText("Moves: " + score);
    }
    @Override
	public boolean onAreaTouched( final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea,final float pTouchAreaLocalX, final float pTouchAreaLocalY)
    {  	
    	if(pSceneTouchEvent.isActionDown()) {
    		return onTouchDownHandler(pTouchArea);
		}
    	else if(pSceneTouchEvent.isActionMove()) {        	
    		return onTouchMoveHandler(pSceneTouchEvent, pTouchArea);
    	} 
    	else if(pSceneTouchEvent.isActionUp()) {        	
			return onTouchUpHandler(pSceneTouchEvent,pTouchArea);
    	}
		return false;
	}
    private boolean onTouchDownHandler(final ITouchArea pTouchArea)
    {    	
    	final Piece piece = (Piece) pTouchArea;
    	iniPos.set((int)piece.getX(),(int)piece.getY());
		if(iniPos.x==blank.x){
			 if(iniPos.y==blank.y + piece.size + puzzleMargin)
				 dir = Dir.Ydown;    			 
			 else if(iniPos.y==blank.y - piece.size - puzzleMargin)
				 dir = Dir.Yup;
		}
		else if(iniPos.y==blank.y){
   			 if(iniPos.x==blank.x + piece.size + puzzleMargin)
   				dir = Dir.Xdown;	 
   			 else if(iniPos.x==blank.x - piece.size - puzzleMargin)
   				dir = Dir.Xup;
		}
		else
			dir = Dir.Stop;
    	return true;
    }
    private boolean onTouchMoveHandler(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea)
    {
    	final Piece piece = (Piece) pTouchArea;
		curPos.set((int)pSceneTouchEvent.getX(),(int)pSceneTouchEvent.getY());
    	switch(dir){
		case Xdown:    			
			if(curPos.x-piece.size/2<iniPos.x && curPos.x-piece.size/2>iniPos.x-piece.size-puzzleMargin)
				piece.setPosition(curPos.x-piece.size/2, iniPos.y);
			break;
		case Xup:
			if(curPos.x-piece.size/2>iniPos.x && curPos.x-piece.size/2<iniPos.x+piece.size+puzzleMargin)
				piece.setPosition(curPos.x-piece.size/2, iniPos.y);
			break;
		case Ydown:
			if(curPos.y-piece.size/2<iniPos.y && curPos.y-piece.size/2>iniPos.y-piece.size-puzzleMargin)
				piece.setPosition(iniPos.x,curPos.y-piece.size/2);
			break;
		case Yup:
			if(curPos.y-piece.size/2>iniPos.y && curPos.y-piece.size/2<iniPos.y+piece.size+puzzleMargin)
				piece.setPosition(iniPos.x,curPos.y-piece.size/2);
			break;    
		case Stop:
			break;
		}
    	return true;
    }
    private boolean onTouchUpHandler(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea)
    {
    	final Piece piece = (Piece) pTouchArea;
		curPos.set((int)pSceneTouchEvent.getX(),(int)pSceneTouchEvent.getY());
		switch(dir){
    		case Xdown:
    			if(iniPos.x - curPos.x+piece.size/2 >= (piece.size + puzzleMargin)/2)
					piece.move((int)iniPos.x - piece.size - puzzleMargin,(int)iniPos.y,-3);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Xup:
    			if(curPos.x-piece.size/2 - iniPos.x >= (piece.size + puzzleMargin)/2)
    				piece.move((int)iniPos.x + piece.size + puzzleMargin,(int)iniPos.y,3);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Ydown:
    			if(iniPos.y - curPos.y+piece.size/2 >= (piece.size + puzzleMargin)/2)
    				piece.move((int)iniPos.x,(int) iniPos.y - piece.size - puzzleMargin,-1);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Yup:
    			if(curPos.y-piece.size/2 - iniPos.y >= (piece.size + puzzleMargin)/2)
    				piece.move((int)iniPos.x,(int) iniPos.y + piece.size + puzzleMargin,1);
				else
					piece.setPosition(iniPos.x,iniPos.y);
				break;
    		case Stop:
    			break;
		}
		if(piece.getX() == blank.x && piece.getY() == blank.y){
			blank.set(iniPos);
			addToScore(1);
		}
		dir = Dir.Stop;
		isPuzzleComplete();
    	return true;
    }
    private void isPuzzleComplete()
    {
    	int i = 1;
    	for(Piece pos:pieces){
			i++;
			if(pos.gridPosAct != pos.gridPosIni)
				break;
			if(i == puzzleSize*puzzleSize)
				scoreText.setText("You Win");
		}        
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