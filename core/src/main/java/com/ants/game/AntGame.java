package com.ants.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
//import com.badlogic.gdx.scenes.scene2d.actions.Actions;
//import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
//import com.badlogic.gdx.scenes.scene2d.ui.Window;
//import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.math.*;
import java.util.*;
import org.w3c.dom.Text;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AntGame extends ApplicationAdapter {
	protected Stage stage;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	protected AssetManager manager;
	private ShapeRenderer shapeRenderer;
	private Hill hill;
	private ArrayList<Marker> markers;
	private BitmapFont font;
	private int points = 0;

	final float WIDTH = 640;
	final float HEIGHT = 480;
	final float HILL_RADIUS = 20;
	final int MAX_ANTS = 50;

	public class Hill implements GameObject {
		@Override
		public float getCenterX() {
			return WIDTH/2;
		}

		@Override
		public float getCenterY() {
			return HEIGHT/2;
		}
	}

	public class Marker implements GameObject {
		private float x,y;

		public Marker(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public float getCenterX() {
			return x;
		}

		@Override
		public float getCenterY() {
			return y;
		}
	}

//	private Skin skin;

	public Hill getHill() {
		return hill;
	}

	public ArrayList<GameObject> getGameObjects() {
		final ArrayList<GameObject> result = new ArrayList<>();
		result.add(getHill());
		for (GameObject marker : markers) {
			result.add(marker);
		}
		for (Actor actor : stage.getActors()) {
			if (actor instanceof GameObject) {
				result.add((GameObject) actor);
			}
		}
		return result;
	}

	public ArrayList<GameObject> getGameObjectsInRadius(float x, float y, float radius) {
		final ArrayList<GameObject> result = new ArrayList<>();
		final Circle viewCircle = new Circle(x, y, radius);
        for (GameObject object : getGameObjects()) {
            if (viewCircle.contains(object.getCenterX(), object.getCenterY())) {
                result.add(object);
            }
        }
        return result;
	}

	public GameObject createMarker(float x, float y) {
		final Marker marker = new Marker(x, y);
		markers.add(marker);
		return marker;
	}

	public void unload(GameObject object) {
		if (object instanceof Sugar) {
			points += 5;
		}
	}

	@Override
	public void create() {
		manager = new AssetManager();
		manager.load("guide.png", Texture.class);
		manager.load("sugar.png", Texture.class);

		camera = new OrthographicCamera();
		camera.setToOrtho(true, WIDTH, HEIGHT);

		batch = new SpriteBatch();
		stage = new Stage(new FitViewport(WIDTH, HEIGHT, camera), batch);

		font = new BitmapFont();
		font.getData().setScale(1.5f, -1.5f);
		font.setColor(Color.RED);

		shapeRenderer = new ShapeRenderer();

		hill = new Hill();
		markers = new ArrayList<>();

//		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
//
//		Window window = new Window("Example screen", skin, "border");
//		window.defaults().pad(4f);
//		window.add("This is a simple Scene2D view.").row();
//		final TextButton button = new TextButton("Click me!", skin);
//		button.pad(8f);
//		button.addListener(new ChangeListener() {
//			@Override
//			public void changed(final ChangeEvent event, final Actor actor) {
//				button.setText("Clicked.");
//			}
//		});
//		window.add(button);
//		window.pack();
//		window.setPosition(stage.getWidth() / 2f - window.getWidth() / 2f,
//				stage.getHeight() / 2f - window.getHeight() / 2f);
//		window.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)));
//		stage.addActor(window);
//
		Gdx.input.setInputProcessor(stage);
		manager.finishLoading();

		{
			final Sugar sugar = new Sugar(this);
			sugar.setPosition(100, 100);
			stage.addActor(sugar);
		}
		{
			final Sugar sugar = new Sugar(this);
			sugar.setPosition(600, 200);
			stage.addActor(sugar);
		}
		{
			final Sugar sugar = new Sugar(this);
			sugar.setPosition(200, 450);
			stage.addActor(sugar);
		}
	}

	private float timeAux = 0;

	@Override
	public void render() {
		if (timeAux >= 1 && Arrays.stream(stage.getActors().toArray()).filter(actor -> actor instanceof Ant).count() < MAX_ANTS) {
			final Ant ant = new MyAnt(this);
			ant.setPosition(WIDTH/2, HEIGHT/2);
			stage.addActor(ant);
			timeAux = 0;
		} else {
			timeAux += Gdx.graphics.getDeltaTime();
		}

		Gdx.gl.glClearColor(0.88f, 0.66f, 0.37f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0.68f, 0.46f, 0.27f, 1);
		shapeRenderer.circle(WIDTH/2, HEIGHT/2, HILL_RADIUS);
		shapeRenderer.end();

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		batch.begin();
		font.draw(batch, String.format("Points: %d", points), 5, 5);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		font.dispose();
//		skin.dispose();
	}
}