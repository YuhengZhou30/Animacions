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

	Rectangle up, down, left, right, fire;
	final int IDLE=0, UP=1, DOWN=2, LEFT=3, RIGHT=4;
	// Constant rows and columns of the sprite sheet
	private static final int FRAME_COLS = 35, FRAME_ROWS = 29;

	// Objects used
	Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
	Texture walkSheet;
	SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	// A variable for tracking elapsed time for the animation
	float stateTime;

	// Background texture
	Texture backgroundTexture;
	@Override
	public void create() {
		// Crea la cámara
		camera = new OrthographicCamera();

		// Carga la hoja de sprites como una textura
		walkSheet = new Texture(Gdx.files.internal("Orangeblack.png"));
		backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
		int SCR_WIDTH = Gdx.graphics.getWidth();
		int SCR_HEIGHT = Gdx.graphics.getHeight();

		// Define los rectángulos para controlar el movimiento del personaje
		up = new Rectangle(0, SCR_HEIGHT * 3 / 4, SCR_WIDTH, SCR_HEIGHT / 4);
		down = new Rectangle(0, 0, SCR_WIDTH, SCR_HEIGHT / 4);
		left = new Rectangle(0, 0, SCR_WIDTH / 4, SCR_HEIGHT);
		right = new Rectangle(SCR_WIDTH * 3 / 4, 0, SCR_WIDTH / 4, SCR_HEIGHT);


		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);

		// Coloca las regiones en un arreglo unidimensional en el orden correcto
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}
		backgroundTexture.setWrap(Texture.TextureWrap.MirroredRepeat,
				Texture.TextureWrap.MirroredRepeat);
		// Inicializa la animación con el intervalo de fotogramas y el arreglo de fotogramas
		walkAnimation = new Animation<TextureRegion>(0.08f, walkFrames);

		// Instancia un SpriteBatch para dibujar y reinicia el tiempo de animación acumulado a 0
		spriteBatch = new SpriteBatch();
		stateTime = 0f;

		// Instancia un nuevo objeto Animator
		Animator animator = new Animator();
		// Establece la cámara en el objeto Animator
		animator.setCamera(camera);
	}


	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		// Clear screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int joystickDirection = virtual_joystick_control();
		// Draw background
		spriteBatch.begin();
		spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.end();

		// Accumulate elapsed animation time
		stateTime += Gdx.graphics.getDeltaTime();

		// Get current frame of animation for the current stateTime
		TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

		// Calculate the position to draw the character to center it on the screen
		float characterX = (Gdx.graphics.getWidth() - currentFrame.getRegionWidth() * 4) / 2;
		float characterY = (Gdx.graphics.getHeight() - currentFrame.getRegionHeight() * 4) / 5;

		spriteBatch.begin();
		// Draw current frame at the centered position
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
	public void dispose() {
		spriteBatch.dispose();
		walkSheet.dispose();
	}

	protected int virtual_joystick_control() {
		// Obtener las coordenadas del toque actual
		float touchX = Gdx.input.getX();
		float touchY = Gdx.input.getY();

		// Imprimir las coordenadas del toque actual
		System.out.println("Click"+ " Touch X: " + touchX + ", Touch Y: " + touchY);

		// Si la pantalla está en orientación horizontal, ajustar las coordenadas
		if (Gdx.graphics.getWidth() > Gdx.graphics.getHeight()) {
			float temp = touchX;
			touchX = touchY;
			touchY = Gdx.graphics.getWidth() - temp; // Ajustar y invertir
		} else {
			touchY = Gdx.graphics.getHeight() - touchY; // Invertir y
		}

		// Imprimir las coordenadas del toque después del ajuste
		//System.out.println("Click "+ "Adjusted Touch X: " + touchX + ", Adjusted Touch Y: " + touchY);
		// Traducir las coordenadas de la pantalla a las coordenadas del mundo del juego
		Vector3 touchPos = new Vector3(touchX, touchY, 0);
		camera.unproject(touchPos);

		// Verificar si el toque está dentro de alguna de las áreas de control

		if (up.contains(touchPos.x, touchPos.y)) {
			System.out.println("Click"+ " Up");
			return UP;
		} else if (down.contains(touchPos.x, touchPos.y)) {
			System.out.println("Click"+" Down");
			return DOWN;
		} else if (left.contains(touchPos.x, touchPos.y)) {
			System.out.println("Click"+" Left");
			return LEFT;
		} else if (right.contains(touchPos.x, touchPos.y)) {
			System.out.println("Click "+ "Right");
			return RIGHT;
		} else {
			//System.out.println("Click"+ " No area clicked");
		}

		return IDLE;
	}




	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}
}
