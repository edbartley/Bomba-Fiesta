package com.FlowerpotGames.Bombas;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen implements Screen, InputProcessor
{
	static final int BALLON_BLACK = 0;
	static final int BALLON_BLUE = 1;
	static final int BALLON_GREEN = 2;
	static final int BALLON_ORANGE = 3;
	static final int BALLON_PURPLE = 4;
	static final int BALLON_RED = 5;
	static final int BALLON_YELLOW = 6;
	
	static final int SUCKER_BLUE = 0;
	static final int SUCKER_GREEN = 1;
	static final int SUCKER_PURPLE = 2;
	static final int SUCKER_RED = 3;
	static final int SUCKER_YELLOW = 4;
	
	static final int CHILI_HAPPY = 0;
	static final int CHILI_LOAD = 1;
	static final int CHILI_FIRE = 2;
	
	OrthographicCamera camera;
	
	final BombaGame game;
	private SpriteBatch spriteBatch;
	private TextureRegion[] ballonRegions = new TextureRegion[7];
	private TextureRegion[] suckerRegions = new TextureRegion[5];
	private TextureRegion[] chiliRegions = new TextureRegion[3];
	private TextureRegion seedRegion;
	private TextureRegion powRegion;
	
	ArrayList<Sprite> bombaSprites = new ArrayList<Sprite>();
	ArrayList<Boolean> bombaVisible = new ArrayList<Boolean>();
	ArrayList<Integer> bombaLolipop = new ArrayList<Integer>();
	ArrayList<Float> bombaMoveSpeed = new ArrayList<Float>();
	
	ArrayList<Sprite> lolipopSprites = new ArrayList<Sprite>();
	ArrayList<Boolean> lolipopVisible = new ArrayList<Boolean>();
	ArrayList<Boolean> lolipopIsFalling = new ArrayList<Boolean>();
	
	Music music;
	Sound fireSound;
	Sound popSound;
	Sound candySound;
	
	Sprite chiliSprite;
	Sprite pow;
	Sprite seed;
	
	private Texture bombaAtlas;
	
	boolean isBallonRising = true;
	boolean isSeedSpawned = false;
	
	FPSLogger fpsLog;

	public GameScreen(BombaGame game)
	{
		this.game = game;
	}
	
	
	float chiliTiming = 0;
	float bombaLaunchTimer = 0;
	
	enum ChiliState
	{
		HAPPY,
		LOADING,
		FIRING
	}
	
	ChiliState chiliState = ChiliState.HAPPY;
	private int popingBomba = -1;
	private int score;
	private BitmapFont font;
	private Texture background;
	private Sprite backSprite;
	
	@Override
	public void render(float delta)
	{
//		fpsLog.log();
		float x = chiliSprite.getX();
		
		if(x < 0)
			x = 0;
		else if(x > BombaGame.SCREEN_WIDTH - chiliSprite.getWidth())
			x = BombaGame.SCREEN_WIDTH - chiliSprite.getWidth();
		
		chiliSprite.setPosition(x, chiliSprite.getY());
		
		bombaLaunchTimer += delta;
		if(bombaLaunchTimer > 0.5f)
		{
			bombaLaunchTimer = 0;
			
			final float launchX = (float) (Math.random() * (BombaGame.SCREEN_WIDTH - 82));
			final int bombaColor =  (int) (Math.random() * 6);
			final int lolipopColor = (int) (Math.random() * 5);
			
			int bomba = 0;
			
			// find a free bomba
			if(bombaVisible.size() > 0)
			{
				for(int count = 0; count < bombaVisible.size(); ++count)
				{
					if(bombaVisible.get(count).booleanValue() == false)
					{
						bomba = count;
						break;
					}
					
					bombaVisible.add(new Boolean(false));
					bombaSprites.add(new Sprite(ballonRegions[bombaColor]));
					bombaLolipop.add(new Integer(lolipopColor));
					bombaMoveSpeed.add(new Float(Math.random() * 200 + 300));
					bomba = bombaSprites.size() - 1;
				}
			}
			else
			{
				bombaVisible.add(new Boolean(false));
				bombaSprites.add(new Sprite(ballonRegions[bombaColor]));
				bombaLolipop.add(new Integer(lolipopColor));
				bombaMoveSpeed.add(new Float(Math.random() * 200 + 300));
				bomba = 0;
			}
			
			bombaVisible.set(bomba, true);
			bombaSprites.get(bomba).setRegion(ballonRegions[bombaColor]);
			bombaSprites.get(bomba).setPosition(launchX, -bombaSprites.get(bomba).getHeight());
			
			// Does this bomba have a lolipop?
			if(Math.random() > 0.49)
			{
				// find a free lolipop sprite
				final int lolipopQty = lolipopSprites.size();
				int lolipopNum;
				
				if(lolipopQty > 0)
				{
					for(int count = 0; count < lolipopQty; ++count)
					{
						if(lolipopVisible.get(count).booleanValue() == false && lolipopIsFalling.get(count).booleanValue() == false)
						{
							lolipopNum = count;
							break;
						}
					}
					
					lolipopSprites.add(new Sprite(this.suckerRegions[lolipopColor]));
					lolipopVisible.add(new Boolean(true));
					lolipopIsFalling.add(new Boolean(false));
					lolipopNum = lolipopVisible.size() - 1;
				}
				else
				{
					lolipopSprites.add(new Sprite(this.suckerRegions[lolipopColor]));
					lolipopVisible.add(new Boolean(true));
					lolipopIsFalling.add(new Boolean(false));
					lolipopNum = 0;
				}
				
				lolipopSprites.get(lolipopNum).setRegion(suckerRegions[lolipopColor]);
				lolipopVisible.set(lolipopNum, true);
				bombaLolipop.set(bomba, lolipopNum);
			}
			else
			{
				bombaLolipop.set(bomba, -1);
			}
			
			isBallonRising = true;
		}
		
		if(isBallonRising == true)
		{
			chiliTiming += delta;
			
			switch (chiliState)
			{
			case FIRING:
				if(chiliTiming > 0.5f)
				{
					chiliSprite.setRegion(chiliRegions[GameScreen.CHILI_LOAD]);
					chiliState = ChiliState.LOADING;
					chiliTiming = 0;
					isSeedSpawned = false;
				}
				break;
			case LOADING:
				if(chiliTiming > 0.5f)
				{
					chiliSprite.setRegion(chiliRegions[GameScreen.CHILI_FIRE]);
					chiliState = ChiliState.FIRING;
					pow.setPosition(chiliSprite.getX() + (chiliSprite.getWidth() - pow.getWidth()) / 2 + pow.getWidth()*0.25f, chiliSprite.getHeight() - pow.getHeight());
					chiliTiming = 0;
					fire();
				}
				break;
			case HAPPY:
				if(chiliTiming > 1)
				{
					chiliSprite.setRegion(chiliRegions[GameScreen.CHILI_LOAD]);
					chiliState = ChiliState.LOADING;
					chiliTiming = 0;
				}
				break;
			}		
		}
		else
		{
			chiliSprite.setRegion(chiliRegions[GameScreen.CHILI_HAPPY]);
		}
		
        if(isSeedSpawned == true)
        {
        	float seedY = seed.getY();
        	seedY += seedY * 3 * delta; 
        	seed.setPosition(seed.getX(), seedY);
        	
        	// did the seed hit a bomba?
        	float seedCenterX = seed.getX() + seed.getWidth() / 2;
        	float seedCenterY = seed.getY() + seed.getHeight() / 2;
        	
        	int qty = bombaVisible.size();
        	for(int count = 0; count < qty; ++count)
        	{
        		if(bombaVisible.get(count).booleanValue() == true)
        		{
        			Sprite bomba = bombaSprites.get(count);
        			float bombaCenterX = bomba.getX() + bomba.getWidth() / 2;
        			float bombaCenterY = bomba.getY() + bomba.getHeight() / 2;
        			
        			if(Math.sqrt(Math.pow(bombaCenterX - seedCenterX, 2) + Math.pow(bombaCenterY - seedCenterY, 2)) < 30)
        			{
        				isSeedSpawned = false;
        				popBomba(count);
        				break;
        			}
        		}
        	}
        }
        
        if(popingBomba > -1)
        {
        	bombaVisible.set(popingBomba, false);
        	popingBomba = -1;
        	// play a sound
        }
		
        // update and draw stuff
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//		camera.update();
//		spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
		camera.apply(Gdx.gl10);
        backSprite.draw(spriteBatch);
        int bombaQty = bombaSprites.size();
        for(int bombaIndex = 0; bombaIndex < bombaQty; bombaIndex++)
        {
        	if(bombaVisible.get(bombaIndex).booleanValue() == true)
        	{
        		final Sprite bomba = bombaSprites.get(bombaIndex);
        		float bombaY = bomba.getY();
        		
        		bombaY += bombaMoveSpeed.get(bombaIndex) * delta;
        		
         		if(bombaY < BombaGame.SCREEN_HEIGHT + 200)
        		{
        			bomba.setPosition(bomba.getX(), bombaY);
        		}
        		else
        		{
        			bombaVisible.set(bombaIndex, false);
        		}
        		
        		int lolypopNum = this.bombaLolipop.get(bombaIndex);
        		if(lolypopNum > -1)
        		{
        			Sprite lolypopSprite = this.lolipopSprites.get(lolypopNum);
        			lolypopSprite.setPosition(bomba.getX() + 10, bombaY - 100);
        		}
        		
        		bomba.draw(spriteBatch);
        	}
        }
        
        int lolypopQty = lolipopSprites.size();
        for(int lolypopIndex = 0; lolypopIndex < lolypopQty; lolypopIndex++)
        {
        	if(lolipopVisible.get(lolypopIndex).booleanValue() == true)
        	{
        		Sprite lolypopSprite = this.lolipopSprites.get(lolypopIndex);
        		
        		// Is it falling?
        		if(lolipopIsFalling.get(lolypopIndex).booleanValue() == true)
        		{
        			// set it's falling position.
        			float lolyY = lolypopSprite.getY();
        			lolyY -= 500 * delta;
        			
        			lolypopSprite.setPosition(lolypopSprite.getX(), lolyY);
        			
        			if(lolyY < 0)
        			{
        				lolipopVisible.set(lolypopIndex, false);
        			}
        			
        			// Did the lolypop contact the chili?
        			if(collision(lolypopSprite, chiliSprite))
        			{
        				lolipopVisible.set(lolypopIndex, false);
        				
        				score += 10;
        				candySound.stop();
        				candySound.play();
        			}
        		}
        			
        		
        		lolypopSprite.draw(spriteBatch);
        	}
        }
        
        chiliSprite.draw(spriteBatch);        
        
        if(isSeedSpawned == true)
        {
        	seed.draw(spriteBatch);
        }
        
        if(chiliState == ChiliState.FIRING)
        	pow.draw(spriteBatch);
        
        font.draw(spriteBatch, Integer.toString(score), 5, BombaGame.SCREEN_HEIGHT);
              
        spriteBatch.end();
	}

	boolean valueInRange(final float value, final float min, final float max)
	{
		return (value >= min) && (value <= max);
	}
	
	private boolean collision(Sprite A, Sprite B) {
		final float aX = A.getX();
		final float aY = A.getY();
		final float bX = B.getX();
		final float bY = B.getY();
		
		boolean xOverlap = valueInRange(aX, bX + 1, bX + B.getWidth() - 1) ||
	                    valueInRange(bX, aX + 1, aX + A.getWidth() - 1);

		boolean yOverlap = valueInRange(aY, bY + 1, bY + B.getHeight() - 1) ||
	                    valueInRange(bY, aY + 1, aY + A.getHeight() - 1);

	    return xOverlap && yOverlap;
	}

	private void popBomba(int count) {
		popingBomba = count;
		score += 5;
		
		if(bombaLolipop.get(count) > -1)
		{
			lolipopIsFalling.set(bombaLolipop.get(count), true);
			bombaLolipop.set(count, -1);
		}
		
		popSound.play();
	}

	private void fire() {
		isSeedSpawned = true;
		seed.setPosition(pow.getX() + (pow.getWidth() - seed.getWidth()) / 2, pow.getY());
		fireSound.play();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		camera = new OrthographicCamera(BombaGame.SCREEN_WIDTH, BombaGame.SCREEN_HEIGHT);
//		camera.zoom = 2;
//		camera.position.set(-500, -500, 0);
		camera.position.set(BombaGame.SCREEN_WIDTH/2, BombaGame.SCREEN_HEIGHT/2, 0);
		camera.update();
		
		bombaAtlas = new Texture(Gdx.files.internal("bomba.png"));
		background = new Texture(Gdx.files.internal("background.png"));
		spriteBatch = new SpriteBatch();
		
		TextureRegion backRegion = new TextureRegion(background, 0, 0, 614, 1024);
		backSprite = new Sprite(backRegion);
		
		ballonRegions[GameScreen.BALLON_BLACK] = new TextureRegion(bombaAtlas, 267, 251, 82, 111);
		ballonRegions[GameScreen.BALLON_BLUE] = new TextureRegion(bombaAtlas, 0, 303, 82, 111);
		ballonRegions[GameScreen.BALLON_GREEN] = new TextureRegion(bombaAtlas, 117, 270, 82, 111);
		ballonRegions[GameScreen.BALLON_ORANGE] = new TextureRegion(bombaAtlas, 418, 111, 82, 111);
		ballonRegions[GameScreen.BALLON_PURPLE] = new TextureRegion(bombaAtlas, 117, 381, 82, 111);
		ballonRegions[GameScreen.BALLON_RED] = new TextureRegion(bombaAtlas, 418, 0, 82, 111);
		ballonRegions[GameScreen.BALLON_YELLOW] = new TextureRegion(bombaAtlas, 267, 362, 82, 111);
		
		suckerRegions[GameScreen.SUCKER_BLUE] = new TextureRegion(bombaAtlas, 0, 414, 66, 64);
		suckerRegions[GameScreen.SUCKER_GREEN] = new TextureRegion(bombaAtlas, 199, 445, 66, 64);
		suckerRegions[GameScreen.SUCKER_PURPLE] = new TextureRegion(bombaAtlas, 199, 381, 66, 64);
		suckerRegions[GameScreen.SUCKER_RED] = new TextureRegion(bombaAtlas, 199, 270, 66, 64);
		suckerRegions[GameScreen.SUCKER_YELLOW] = new TextureRegion(bombaAtlas, 349, 251, 66, 64);
		
		chiliRegions[GameScreen.CHILI_FIRE] = new TextureRegion(bombaAtlas, 118, 0, 149, 270);
		chiliRegions[GameScreen.CHILI_HAPPY] = new TextureRegion(bombaAtlas, 0, 0, 117, 303);
		chiliRegions[GameScreen.CHILI_LOAD] = new TextureRegion(bombaAtlas, 268, 0, 150, 251);
		
		seedRegion = new TextureRegion(bombaAtlas, 92, 303, 17, 31);
		seed = new Sprite(seedRegion);
		
		powRegion = new TextureRegion(bombaAtlas, 0, 478, 44, 28);

		chiliSprite = new Sprite(chiliRegions[GameScreen.CHILI_HAPPY]);
		chiliSprite.setPosition((BombaGame.SCREEN_WIDTH - chiliSprite.getWidth()) / 2 , 0);
		
		pow = new Sprite(this.powRegion);
		pow.setPosition(chiliSprite.getX() + (chiliSprite.getWidth() - pow.getWidth()) / 2, chiliSprite.getHeight()/2);
		
		Gdx.input.setInputProcessor(this);
		fpsLog = new FPSLogger();
		
		font = new BitmapFont(Gdx.files.internal("font10.fnt"), Gdx.files.internal("font10.png"), false);
		font.setScale(5);
		
		fireSound = Gdx.audio.newSound(Gdx.files.internal("fire.wav"));
		popSound = Gdx.audio.newSound(Gdx.files.internal("pop.wav"));
		candySound = Gdx.audio.newSound(Gdx.files.internal("candy.wav"));
		music = Gdx.audio.newMusic(Gdx.files.internal("music.ogg"));
		
		music.setLooping(true);
		music.play();
		music.setVolume(0.5f);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		fireSound.dispose();
		popSound.dispose();
		candySound.dispose();
		music.dispose();
		font.dispose();
		

	}

	@Override
	public boolean keyDown(int keycode) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		chiliSprite.setPosition(x * 2 - chiliSprite.getWidth() /2, 0);
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		chiliSprite.setPosition(x *2 - chiliSprite.getWidth() /2, 0);
		pow.setPosition(chiliSprite.getX() + (chiliSprite.getWidth() - pow.getWidth()) / 2 + pow.getWidth()*0.25f, pow.getY());
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		chiliSprite.setPosition(x *2 - chiliSprite.getWidth() /2, 0);
		pow.setPosition(chiliSprite.getX() + (chiliSprite.getWidth() - pow.getWidth()) / 2 + pow.getWidth()*0.25f, pow.getY());

		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
