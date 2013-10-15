package com.LOL.sliding;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.KeyEvent;

public class GameActivity extends BaseGameActivity
{

	private Camera camera;
	public ResourcesManager resourcesManager;
	 
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) 
	{
	    return new LimitedFPSEngine(pEngineOptions, 60);
	}	
	@Override
	public EngineOptions onCreateEngineOptions() 
	{
		camera = new Camera(0, 0, 720, 1280);
	    EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(720,1280), this.camera);
	    engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
	    return engineOptions;
	}
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)
	{
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
	    resourcesManager = ResourcesManager.getInstance();
	    pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) 
	{
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}
	@Override
	public void onPopulateScene(Scene pScene,OnPopulateSceneCallback pOnPopulateSceneCallback) 
	{
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
	    {
	            public void onTimePassed(final TimerHandler pTimerHandler) 
	            {
	                mEngine.unregisterUpdateHandler(pTimerHandler);
	                SceneManager.getInstance().createMenuScene();
	            } 
	    }));
	    pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	        System.exit(0);	
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}
	Uri mImageCaptureUri;
	public void selecPic(){
    	Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 0);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode != RESULT_OK) return;
 
		if (requestCode == 0){ 
		        mImageCaptureUri = data.getData();
		        Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setType("image/*");
			    intent.setData(mImageCaptureUri);
			    intent.putExtra("crop", "true");
			    intent.putExtra("outputX", 699);
			    intent.putExtra("outputY", 699);
		        intent.putExtra("aspectX", 1);
		        intent.putExtra("aspectY", 1);
			    intent.putExtra("return-data", false);
			    intent.putExtra("scale", true);
		        intent.putExtra("noFaceDetection",true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, resourcesManager.gettempUri(1));
			    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			    startActivityForResult(intent, 1);
		}
		else if (requestCode == 1){
			resourcesManager.loadGame2();
		}
	}
}
