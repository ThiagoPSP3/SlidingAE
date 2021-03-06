package com.blackice.sliding;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Piece extends Sprite{
	public int gridPosIni;
	public int gridPosAct;
	
	public Piece(float pX, float pY, ITextureRegion pTextureRegion,	VertexBufferObjectManager pVertexBufferObjectManager, int gridPosition) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		gridPosIni = gridPosition;
		gridPosAct = gridPosition;
	}
	@Override
    protected void preDraw(GLState pGLState, Camera pCamera) 
    {
       super.preDraw(pGLState, pCamera);
       pGLState.enableDither();
    }
	public void move(int x,int y,int inc)
    {
    	setPosition(x,y);
		gridPosAct+=inc;
    }
}
