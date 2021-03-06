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
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.*;
import org.w3c.dom.Text;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AntGame extends ApplicationAdapter {
	protected Stage stage;
	private Group antGroup;
	private Group objectsGroup;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	protected AssetManager manager;
	private ShapeRenderer shapeRenderer;
	private Hill hill;
	private ArrayList<Marker> markers;
	private ArrayList<Scent> scents;
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

		@Override
		public String toString() {
			return "Hill";
		}

		@Override
		public Vector2 getCenter() {
			return new Vector2(getCenterX(), getCenterY());
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

		@Override
		public Vector2 getCenter() {
			return new Vector2(getCenterX(), getCenterY());
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
		for (GameObject scent : scents) {
			result.add(scent);
		}
		for (Actor actor : antGroup.getChildren()) {
			if (actor instanceof GameObject) {
				result.add((GameObject) actor);
			}
		}
		for (Actor actor : objectsGroup.getChildren()) {
			if (actor instanceof GameObject) {
				result.add((GameObject) actor);
			}
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

	public ArrayList<Actor> getActorsInRadius(float x, float y, float radius) {
		final ArrayList<Actor> result = new ArrayList<>();
		final Circle viewCircle = new Circle(x, y, radius);
        for (Actor actor : stage.getActors()) {
            if (viewCircle.contains(actor.getX(), actor.getY())) {
                result.add(actor);
            }
        }
        return result;
	}

	public GameObject createMarker(float x, float y) {
		final Marker marker = new Marker(x, y);
		markers.add(marker);
		return marker;
	}

	public void addScent(Scent scent) {
		if (scents.stream().filter(s -> s.getAnt() == scent.getAnt()).count() < 1) {
			scents.add(scent);
		}
	}

	public void unload(GameObject object) {
		if (object instanceof Sugar) {
			points += 5;
		}
		if (object instanceof Apple) {
			points += 100;
			((Apple)object).remove();
			spawnApple();
		}
	}

	@Override
	public void create() {
		manager = new AssetManager();
		manager.load("guide.png", Texture.class);
		manager.load("sugar.png", Texture.class);
		manager.load("apple.png", Texture.class);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);

		batch = new SpriteBatch();
		stage = new Stage(new FitViewport(WIDTH, HEIGHT, camera), batch);

		stage.setDebugAll(false);

		antGroup = new Group();
		objectsGroup = new Group();
		stage.addActor(objectsGroup);
		stage.addActor(antGroup);

		font = new BitmapFont();
		font.getData().setScale(1.5f, 1.5f);
		font.setColor(Color.RED);

		shapeRenderer = new ShapeRenderer();

		hill = new Hill();
		markers = new ArrayList<>();
		scents = new ArrayList<>();

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

		for (int n = 0; n < 3; ++n) {
			spawnSugar();
		}

		for (int n = 0; n < 3; ++n) {
			spawnApple();
		}
	}

	private Vector2 randomPoint(float R1, float R2) {
		final float theta = MathUtils.random(0, 360);
		final float dist = (float)Math.sqrt(Math.random()*(Math.pow(R1, 2)-Math.pow(R2, 2))+Math.pow(R2, 2));
		return new Vector2((float)(dist * Math.cos(theta)), (float)(dist * Math.sin(theta)));
	}

	private void spawnSugar() {
		final Sugar sugar = new Sugar(this);
		final Vector2 point = randomPoint(HILL_RADIUS * 6, HEIGHT/2 - 30);
		sugar.setPosition(point.x + WIDTH/2, HEIGHT - (point.y + HEIGHT/2));
		objectsGroup.addActor(sugar);
	}

	private void spawnApple() {
		final Apple apple = new Apple(this);
		final Vector2 point = randomPoint(HILL_RADIUS * 6, HEIGHT/2 - 30);
		apple.setPosition(point.x + WIDTH/2, HEIGHT - (point.y + HEIGHT/2));
		objectsGroup.addActor(apple);
	}

	private float timeAux = 0;

	@Override
	public void render() {
		if (timeAux >= 1 && Arrays.stream(stage.getActors().toArray()).filter(actor -> actor instanceof Ant).count() < MAX_ANTS) {
			final Ant ant = new MyAnt(this);
			ant.setPosition(WIDTH/2, HEIGHT/2);
			antGroup.addActor(ant);
			timeAux = 0;
		} else {
			timeAux += Gdx.graphics.getDeltaTime();
		}

		Iterator<Scent> iter = scents.iterator();
		while (iter.hasNext()) {
			Scent scent = iter.next();

			scent.tick(Gdx.graphics.getDeltaTime());

			if (scent.vanished()) {
				iter.remove();
			} else {
				for (Actor actor : getActorsInRadius(scent.getCenterX(), scent.getCenterY(), scent.getRadius())) {
					if (actor instanceof Ant) {
						final Ant ant = (Ant)actor;
						if (ant != scent.getAnt() && !scent.hasAlreadySmalled(ant)) {
							scent.smelled(ant);
							ant.smellsNewScent(scent);
						}
					}
				}
			}
		}

		for (Actor actor : stage.getActors()) {
			if (actor instanceof Sugar) {
				final Sugar sugar = (Sugar)actor;
				if (sugar.depleted()) {
					sugar.remove();
					spawnSugar();
				}
			}
		}

		Gdx.gl.glClearColor(0.88f, 0.66f, 0.37f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		shapeRenderer.setColor(0.78f, 0.56f, 0.37f, 1);
		for (Scent scent : scents) {
			shapeRenderer.circle(scent.getCenterX(), scent.getCenterY(), scent.getRadius());
		}

		shapeRenderer.setColor(0.68f, 0.46f, 0.27f, 1);
		shapeRenderer.circle(WIDTH/2, HEIGHT/2, HILL_RADIUS);

		shapeRenderer.end();

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		batch.begin();
		font.draw(batch, String.format("Points: %d", points), 5, HEIGHT-5);
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