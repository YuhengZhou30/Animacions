package com.yuheng.animation;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Animator implements ApplicationListener {

	Rectangle left, right;
	final int IDLE = 0, LEFT = 3, RIGHT = 4;
	private static final int FRAME_COLS = 35, FRAME_ROWS = 29;
	private static final float BACKGROUND_SCROLL_SPEED = 500.0f;
	float lastTouchX = -1;
	float lastTouchY = -1;
	Animation<TextureRegion> walkAnimation;
	Texture walkSheet;
	SpriteBatch spriteBatch;
	TextureRegion currentFrame;
	OrthographicCamera camera;
	float stateTime;
	Texture backgroundTexture;
	float backgroundOffsetX = 0;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		walkSheet = new Texture(Gdx.files.internal("Orangeblack.png"));
		backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));


		left = new Rectangle(0, -600, 400, 550);
		right = new Rectangle(0, -50, 400, 550);

		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);

		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}
		backgroundTexture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);

		walkAnimation = new Animation<TextureRegion>(0.08f, walkFrames);
		spriteBatch = new SpriteBatch();
		stateTime = 0f;

		Animator animator = new Animator();
		animator.setCamera(camera);
	}

	@Override

	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int joystickDirection = virtual_joystick_control();

		spriteBatch.begin();

		// Dibuja el fondo repetido e infinito
		spriteBatch.draw(backgroundTexture, backgroundOffsetX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.draw(backgroundTexture, backgroundOffsetX + Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		stateTime += Gdx.graphics.getDeltaTime();
		currentFrame = walkAnimation.getKeyFrame(stateTime, true); // Asignación única de currentFrame

		float characterX = (Gdx.graphics.getWidth() - currentFrame.getRegionWidth() * 4) / 2;
		float characterY = (Gdx.graphics.getHeight() - currentFrame.getRegionHeight() * 4) / 5;

		// Dibuja el personaje con la opción de voltear horizontalmente
		spriteBatch.draw(currentFrame, characterX, characterY, currentFrame.getRegionWidth() * 4, currentFrame.getRegionHeight() * 4);
		spriteBatch.end();
	}



	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		walkSheet.dispose();
	}

	protected int virtual_joystick_control() {
		float touchX = Gdx.input.getX();
		float touchY = Gdx.input.getY();

		if (Gdx.graphics.getWidth() > Gdx.graphics.getHeight()) {
			float temp = touchX;
			touchX = touchY;
			touchY = Gdx.graphics.getWidth() - temp;
		} else {
			touchY = Gdx.graphics.getHeight() - touchY;
		}

		Vector3 touchPos = new Vector3(touchX, touchY, 0);
		camera.unproject(touchPos);

		if (touchPos.x != lastTouchX || touchPos.y != lastTouchY) {
			// Las coordenadas han cambiado desde la última vez

			if (left.contains(touchPos.x, touchPos.y)) {
				System.out.println("Click Left");
				backgroundOffsetX += BACKGROUND_SCROLL_SPEED * Gdx.graphics.getDeltaTime();
				if (currentFrame != null) {
					currentFrame.flip(true, false);
				}
				lastTouchX = touchPos.x;
				lastTouchY = touchPos.y;
				return LEFT;
			} else if (right.contains(touchPos.x, touchPos.y)) {
				System.out.println("Click Right");
				backgroundOffsetX -= BACKGROUND_SCROLL_SPEED * Gdx.graphics.getDeltaTime();
				if (currentFrame != null) {
					currentFrame.flip(true, false);
				}
				lastTouchX = touchPos.x;
				lastTouchY = touchPos.y;
				return RIGHT;
			}
		}
		lastTouchX = touchPos.x;
		lastTouchY = touchPos.y;
		return IDLE;

	}

	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}
}
