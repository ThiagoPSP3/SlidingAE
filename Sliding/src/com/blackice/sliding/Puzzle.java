package com.blackice.sliding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	public int pieceSize,screenMargin,wallThickness,puzzleSize,puzzleMargin;
	public enum Dir {
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
    	puzzleSize = resourcesManager.puzzleSize;
    	pieceSize = (int)resourcesManager.picSize/puzzleSize;
    	puzzleMargin = (int)(CAMERA_WIDTH - pieceSize*puzzleSize) / (puzzleSize + 3);
    	wallThickness = puzzleMargin;
    	screenMargin = (int)(CAMERA_HEIGHT - CAMERA_WIDTH)/2;
    	createRectangle();
    	for(int i=0;i<puzzleSize;i++)
    		for(int j=0;j<puzzleSize;j++)
    			if(puzzleSize*i+j<puzzleSize*puzzleSize-1)//Doesn't enter here if it's the last piece because it's where we put the blank
	    			addPiece(i,j);
    			else
    				blank.set(j*pieceSize+wallThickness+(j+1)*puzzleMargin, i*pieceSize+wallThickness+screenMargin+(i+1)*puzzleMargin);
    	shuffle();
	}
	private void addPiece(int x , int y){
    	resourcesManager.puzzle_region.setCurrentTileIndex(puzzleSize*x+y);
		Piece piece = new Piece(0, 0, resourcesManager.puzzle_region, vbom, puzzleSize*x+y);
    	piece.setPosition(y*pieceSize+wallThickness+(y+1)*puzzleMargin,x*pieceSize+wallThickness + screenMargin+(x+1)*puzzleMargin);
    	gameScene.attachChild(piece);
    	gameScene.registerTouchArea(piece);
    	gameScene.setTouchAreaBindingOnActionDownEnabled(true);
		pieces.add(piece);
    }
	private void createRectangle() 
	{
    	final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - screenMargin-wallThickness, CAMERA_WIDTH, wallThickness, vbom);
		final Rectangle roof = new Rectangle(0, screenMargin, CAMERA_WIDTH, wallThickness, vbom);
		final Rectangle left = new Rectangle(0, screenMargin, wallThickness, CAMERA_HEIGHT-screenMargin-screenMargin, vbom);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - wallThickness, screenMargin, wallThickness, CAMERA_HEIGHT-screenMargin-screenMargin, vbom);
		gameScene.attachChild(ground);
		gameScene.attachChild(roof);
		gameScene.attachChild(left);
		gameScene.attachChild(right);	
	}
	private void shuffle()
	{
		Vector2 corner = new Vector2(blank);
		for(int i=0;i<1000||!blank.equals(corner);i++)
			moveBlank(getRandomDir());
	}
	private void moveBlank(Dir moveDir)
	{
		int adj=0;
		Vector2 inc = new Vector2(0,0);
		switch(moveDir){
		case Xdown:
			adj=-1;
			inc.x = -puzzleMargin-pieceSize;
			break;
		case Xup:
			adj=1;
			inc.x = puzzleMargin+pieceSize;
			break;
		case Ydown:
			adj=-puzzleSize;
			inc.y = -puzzleMargin-pieceSize;
			break;
		case Yup:
			adj=puzzleSize;
			inc.y = puzzleMargin+pieceSize;
			break;
		case Stop:
			break;
		}
    	for(Piece piece:pieces){
    		if(piece.getX()+inc.x == blank.x && piece.getY()+inc.y == blank.y){
    			iniPos.set(piece.getX(),piece.getY());
    			piece.move((int)blank.x, (int)blank.y, adj);
    			blank.set(iniPos);
    			break;
    		}
    	}
	}
	private Dir getRandomDir()
	{
		Random r = new Random();
		boolean xdown=true,xup=true,ydown=true,yup=true;
		if(blank.x==0)
			xdown = false;
		if(blank.y==0)
			ydown = false;
		if(blank.x==(puzzleSize-1)*pieceSize + puzzleSize+puzzleMargin)
			xup = false;
		if(blank.y==(puzzleSize-1)*pieceSize + puzzleSize+puzzleMargin)
			yup = false;
		int opts=0;
		if(xdown)opts++;
		if(xup)opts++;
		if(ydown)opts++;
		if(yup)opts++;
		int rand = r.nextInt(opts) + 1;
		Dir moveDir=Dir.Yup;
		switch(rand){
		case 3:
			if(xdown&&xup&&ydown)
				moveDir=Dir.Ydown;
			else
				moveDir=Dir.Yup;
			break;
		case 2:
			if(xdown&&xup)
				moveDir=Dir.Xup;
			else if(xdown&&ydown || xup&&ydown)
				moveDir=Dir.Ydown;
			else
				moveDir=Dir.Yup;
			break;
		case 1:
			if(xdown)
				moveDir=Dir.Xdown;
			else if(xup)
				moveDir=Dir.Xup;
			else if(ydown)
				moveDir=Dir.Ydown;
			else if(yup)
				moveDir=Dir.Yup;
			break;
		}
		return moveDir;
	}
	private void isPuzzleComplete()
    {
    	int i = 1;
    	for(Piece piece:pieces){
			i++;
			if(piece.gridPosAct != piece.gridPosIni)
				break;
			if(i == puzzleSize*puzzleSize)
				gameScene.scoreText.setText("You Win "+gameScene.score);
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
					piece.move((int)iniPos.x - pieceSize - puzzleMargin,(int)iniPos.y,-1);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Xup:
    			if(curPos.x-pieceSize/2 - iniPos.x >= (pieceSize + puzzleMargin)/2)
    				piece.move((int)iniPos.x + pieceSize + puzzleMargin,(int)iniPos.y,1);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Ydown:
    			if(iniPos.y - curPos.y+pieceSize/2 >= (pieceSize + puzzleMargin)/2)
    				piece.move((int)iniPos.x,(int) iniPos.y - pieceSize - puzzleMargin,-puzzleSize);
				else
					piece.setPosition(iniPos.x,iniPos.y);
    			break;
    		case Yup:
    			if(curPos.y-pieceSize/2 - iniPos.y >= (pieceSize + puzzleMargin)/2)
    				piece.move((int)iniPos.x,(int) iniPos.y + pieceSize + puzzleMargin,puzzleSize);
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
