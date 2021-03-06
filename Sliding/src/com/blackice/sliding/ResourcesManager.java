package com.blackice.sliding;

import java.io.File;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.FileBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.net.Uri;
import android.os.Environment;

public class ResourcesManager {
	//---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    
    private static final ResourcesManager INSTANCE = new ResourcesManager();
	private static final int CAMERA_WIDTH = 720;
    
    public Engine engine;
    public GameActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;
    
    public ITextureRegion splash_region;
    private BitmapTextureAtlas splashTextureAtlas;
    
    public TiledTextureRegion puzzle_region;
    private BitmapTextureAtlas puzzleTextureAtlas;
    public ITextureRegion puzzle0101;
    
    public ITextureRegion menu_background_region;
    public ITextureRegion play_region;
    public ITextureRegion options_region;        
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    
    public Font font;
    
    public int puzzleSize = 3,picSize;
    
    //---------------------------------------------
    // TEXTURES & TEXTURE REGIONS
    //---------------------------------------------
    
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
        loadMenuFonts();
    }
    public void loadGameResources()
    {
        loadGameFonts();
        loadGameAudio();
        loadGameGraphics();
    }
    private void loadMenuGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
    	menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 1280, TextureOptions.BILINEAR);
    	menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.jpg");
    	play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play_button.png");
    	options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options_button.png");
    	try 
    	{
    	    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
    	    this.menuTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
    }
    public void unloadMenuTextures()
    {
        menuTextureAtlas.unload();
    }
    public void unloadGameTextures()
    {
        // TODO (Since we did not create any textures for game scene yet)
    }
    public void loadMenuTextures()
    {
        menuTextureAtlas.load();
    }
    private void loadMenuAudio()
    {
        
    }
    private void loadGameGraphics()
    {
    	/*
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/gallery/");
    	puzzleTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 699, 699, TextureOptions.BILINEAR);
    	puzzle_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(puzzleTextureAtlas, activity, "image0101.jpg", 0, 0, puzzleSize, puzzleSize);
    	puzzleTextureAtlas.load();*/
    	picSize=CAMERA_WIDTH-5*6;
    	activity.selecPic(picSize);
    }
    public void loadGame2()
    {
    	picSize=CAMERA_WIDTH-5*6;
    	FileBitmapTextureAtlasSource fileTextureSource = FileBitmapTextureAtlasSource.create(new File(gettempUri(2).getPath()));
    	puzzleTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), picSize, picSize, TextureOptions.BILINEAR);
    	puzzle_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromSource(puzzleTextureAtlas,fileTextureSource, 0, 0, puzzleSize, puzzleSize);
    	puzzleTextureAtlas.load();    	
    }
    private void loadGameFonts()
    {
         
    }
    
    private void loadGameAudio()
    {
        
    }
    
    public void loadSplashScreen()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 640, 360, TextureOptions.BILINEAR);
    	splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.jpg", 0, 0);
    	splashTextureAtlas.load();
    }
    
    public void unloadSplashScreen()
    {
    	splashTextureAtlas.unload();
    	splash_region = null;
    }
    
    private void loadMenuFonts()
    {
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, android.graphics.Color.WHITE, 2, android.graphics.Color.BLACK);
        font.load();
    }
    int filecount=0;
    File dir,tempFile;
    Uri tempUri;
    public void setTempUri(){
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sliding";
		dir = new File(file_path);

		if(!dir.exists())
			dir.mkdirs();

		do{
			filecount+=1;
			tempFile = new File(dir, "slidingtmpN" +filecount+ ".jpg");
		}while(tempFile.exists());

		filecount = 0;
		tempUri = Uri.fromFile(tempFile);
	}
    public Uri gettempUri(int i)
	{
		if(i==1)
			setTempUri();
		return tempUri;
	}
    /**
     * @param engine
     * @param activity
     * @param camera
     * @param vbom
     * <br><br>
     * We use this method at beginning of game loading, to prepare Resources Manager properly,
     * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
     */
    public static void prepareManager(Engine engine, GameActivity activity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
    
    public static ResourcesManager getInstance()
    {
        return INSTANCE;
    }
}
