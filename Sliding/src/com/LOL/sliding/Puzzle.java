package com.LOL.sliding;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;

public class Puzzle {

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 1280;
	
	public Vector2 blank,iniPos,curPos;

	public List<Piece> pieces;
	
	public static final int step = 233,topMargin = 360,bottonMargin = 200,wallThickness = 2,puzzleSize=3,puzzleMargin=4;
	int pieceSize;
	
	public enum Dir{
		Xdown,Xup,Ydown,Yup,Stop
	}
	Dir dir;	

    protected ResourcesManager resourcesManager;
    protected VertexBufferObjectManager vbom;
    protected SceneManager sceneManager;
    private GameScene gameScene;
	
	public Puzzle(GameScene scene) 
    {
		resourcesManager = ResourcesManager.getInstance();
		vbom = resourcesManager.vbom;
		sceneManager = SceneManager.getInstance();
		gameScene = scene;
    	pieces = new ArrayList<Piece>();
    	blank = new Vector2();iniPos = new Vector2();curPos = new Vector2();
    	pieceSize = step;
    	createRectangle();
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
    	gameScene.attachChild(piece);
    	gameScene.registerTouchArea(piece);
    	gameScene.setTouchAreaBindingOnActionDownEnabled(true);
		pieces.add(piece);
    }
	private void createRectangle() 
	{
    	final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - bottonMargin-wallThickness, CAMERA_WIDTH, wallThickness, vbom);
		final Rectangle roof = new Rectangle(0, topMargin, CAMERA_WIDTH, wallThickness, vbom);
		final Rectangle left = new Rectangle(0, topMargin, wallThickness, CAMERA_HEIGHT-topMargin-bottonMargin, vbom);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - wallThickness, topMargin, wallThickness, CAMERA_HEIGHT-topMargin-bottonMargin, vbom);
		gameScene.attachChild(ground);
		gameScene.attachChild(roof);
		gameScene.attachChild(left);
		gameScene.attachChild(right);	
	}
	private void isPuzzleComplete()
    {
    	int i = 1;
    	for(Piece pos:pieces){
			i++;
			if(pos.gridPosAct != pos.gridPosIni)
				break;
			if(i == puzzleSize*puzzleSize)
				gameScene.scoreText.setText("You Win");
		}        
	}
	public boolean onTouchDownHandler(final ITouchArea pTouchArea)
    {    	
    	final Piece piece = (Piece) pTouchArea;
    	iniPos.set((int)piece.getX(),(int)piece.getY());
		if(iniPos.x==blank.x){
			 if(iniPos.y==blank.y + pieceSize + puzzleMargin)
				 dir = Dir.Ydown;    			 
			 else if(iniPos.y==blank.y - pieceSize - puzzleMargin)
				 dir = Dir.Yup;
		}
		else if(iniPos.y==blank.y){
   			 if(iniPos.x==blank.x + pieceSize + puzzleMargin)
   				dir = Dir.Xdown;	 
   			 else if(iniPos.x==blank.x - pieceSize - puzzleMargin)
   				dir = Dir.Xup;
		}
		else
			dir = Dir.Stop;
    	return true;
    }
	public boolean onTouchMoveHandler(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea)
    {
    	final Piece piece = (Piece) pTouchArea;
		curPos.set((int)pSceneTouchEvent.getX(),(int)pSceneTouchEvent.getY());
    	switch(dir){
		case Xdown:    			
			if(curPos.x-pieceSize/2<iniPos.x && curPos.x-pieceSize/2>iniPos.x-pieceSize-puzzleMargin)
				piece.setPosition(curPos.x-pieceSize/2, iniPos.y);
			break;
		case Xup:
			if(curPos.x-pieceSize/2>iniPos.x && curPos.x-pieceSize/2<iniPos.x+pieceSize+puzzleMargin)
				piece.setPosition(curPos.x-pieceSize/2, iniPos.y);
			break;
		case Ydown:
			if(curPos.y-pieceSize/2<iniPos.y && curPos.y-pieceSize/2>iniPos.y-pieceSize-puzzleMargin)
				piece.setPosition(iniPos.x,curPos.y-pieceSize/2);
			break;
		case Yup:
			if(curPos.y-pieceSize/2>iniPos.y && curPos.y-pieceSize/2<iniPos.y+pieceSize+puzzleMargin)
				piece.setPosition(iniPos.x,curPos.y-pieceSize/2);
			break;    
		case Stop:
			break;
		}
    	return true;
    }
    public boolean onTouchUpHandler(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea)
    {
    	final Piece piece = (Piece) pTouchArea;
		curPos.set((int)pSceneTouchEvent.getX(),(int)pSceneTouchEvent.getY());
		switch(dir){
    		case Xdown:
    			if(iniPos.x - curPos.x+pieceSize/2 >= (pieceSize + puzzleMargin)/2)
					piece.move((int)iniPos.x - pieceSize - puzzleMargin,(int)iniPos.y,-3);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Xup:
    			if(curPos.x-pieceSize/2 - iniPos.x >= (pieceSize + puzzleMargin)/2)
    				piece.move((int)iniPos.x + pieceSize + puzzleMargin,(int)iniPos.y,3);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Ydown:
    			if(iniPos.y - curPos.y+pieceSize/2 >= (pieceSize + puzzleMargin)/2)
    				piece.move((int)iniPos.x,(int) iniPos.y - pieceSize - puzzleMargin,-1);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Yup:
    			if(curPos.y-pieceSize/2 - iniPos.y >= (pieceSize + puzzleMargin)/2)
    				piece.move((int)iniPos.x,(int) iniPos.y + pieceSize + puzzleMargin,1);
				else
					piece.setPosition(iniPos.x,iniPos.y);
				break;
    		case Stop:
    			break;
		}
		if(piece.getX() == blank.x && piece.getY() == blank.y){
			blank.set(iniPos);
			gameScene.addToScore(1);
		}
		dir = Dir.Stop;
		isPuzzleComplete();
    	return true;
    }
}
