package com.ants.game;

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

enum State {
    IDLE,
    WALKING,
    CARRYING_APPLE
}

public abstract class Ant extends Actor {
    private Sprite sprite;
    private Sprite sugar;
    private State state;
    private AntGame game;
    private GameObject goal;
    private GameObject carries;

    final float SPEED = 100;
    final float VIEW_RADIUS = 100;
    final float HIT_RADIUS = 20;

    public Ant(AntGame game) {
        state = State.IDLE;
        this.game = game;

        sprite = new Sprite(game.manager.get("guide.png", Texture.class));
        sprite.flip(false, true);

        sugar = new Sprite(game.manager.get("sugar.png", Texture.class));
        sugar.setScale(0.2f);

        setRotation(MathUtils.random(0, 360));
        setSize(10, 10);
    }

    @Override
    public void act(float delta) {
        for (GameObject object : game.getGameObjectsInRadius(getX(), getY(), VIEW_RADIUS)) {
            if (object instanceof Sugar) {
                seesSugar((Sugar)object);
            }

            if (object instanceof Apple) {
                seesApple((Apple)object);
            }

            if (object instanceof AntGame.Hill) {
                seesHill();
            }
        }

        for (GameObject object : game.getGameObjectsInRadius(getX(), getY(), HIT_RADIUS)) {
            if (object == goal) {
                setGoal(null);
                state = State.IDLE;
            }

            if (object instanceof Sugar) {
                hitsSugar((Sugar)object);
            }

            if (object instanceof Apple) {
                hitsApple((Apple)object);
            }

            if (object instanceof AntGame.Hill) {
                hitsHill();
            }
        }

        switch (state) {
            case IDLE:
                hasNothingToDo();
                break;
            case WALKING:
                final float width = sprite.getWidth() / 2;
                final float height = sprite.getHeight() / 2;

                if (getX() + width >= game.WIDTH || getX() - width <= 0 || getY() + height >= game.HEIGHT || getY() - height <= 0) {
                    rotateBy(90);
                }

                final Vector2 point = new Vector2((float)Math.cos(getRotation() * MathUtils.degreesToRadians), (float)Math.sin(getRotation() * MathUtils.degreesToRadians)).scl(SPEED * delta);
                moveBy(point.x, point.y);

                break;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setCenter(getX(), getY());
        sprite.setRotation(getRotation() + 90);
        sprite.draw(batch);

        if (carries instanceof Sugar) {
            sugar.setCenter(getX(), getY());
            sugar.setRotation(getRotation() + 90);
            sugar.draw(batch);
        }
    }

    private void setGoal(GameObject goal) {
        this.goal = goal;
    }

    private void setCarries(GameObject carries) {
        if (this.carries instanceof Apple) {
            ((Apple)this.carries).removeAnt(this);
        }
        this.carries = carries;
    }

    protected void walkAround() {
        setGoal(null);
        state = State.WALKING;
    }

    protected void walkTo(GameObject object) {
        setGoal(object);
        final float degrees = (float)Math.atan2(
            object.getCenterY() - getY(),
            object.getCenterX() - getX()
        ) * MathUtils.radiansToDegrees;
        setRotation(degrees);
    }

    protected void walkToHill() {
        walkTo(game.getHill());
    }

    protected boolean hasGoal() {
        return goal != null;
    }

    protected GameObject getGoal() {
        return goal;
    }

    protected boolean isCarrying() {
        return carries != null;
    }

    protected boolean isCarryingSugar() {
        return carries instanceof Sugar;
    }

    protected boolean isCarryingApple() {
        return carries instanceof Apple;
    }

    protected void take(Sugar sugar) {
        if (!isCarrying()) {
            if (new Vector2(getX(), getY()).dst(sugar.getCenterX(), sugar.getCenterY()) <= HIT_RADIUS) {
                setCarries(sugar);
                sugar.takeOne();
                setGoal(null);
                state = State.IDLE;
            } else {
                print("Sugar too far away.");
            }
        } else {
            print("Cannot take sugar. Already carrying something else.");
        }
    }

    protected void take(Apple apple) {
        if (!isCarrying()) {
            if (new Vector2(getX(), getY()).dst(apple.getCenterX(), apple.getCenterY()) <= HIT_RADIUS) {
                setCarries(apple);
                apple.addAnt(this);
                setGoal(null);
                state = State.CARRYING_APPLE;
            } else {
                print("Apple too far away.");
            }
        } else {
            print("Cannot take apple. Already carrying something else.");
        }
    }

    protected void turn(float deg) {
        rotateBy(deg);
    }

    protected void walk(float distance) {
        final Vector2 point = new Vector2(getX(), getY()).add(new Vector2((float)Math.cos(getRotation() * MathUtils.degreesToRadians), (float)Math.sin(getRotation() * MathUtils.degreesToRadians)).scl(distance));
        setGoal(game.createMarker(point.x, point.y));
        state = State.WALKING;
    }

    protected void unload() {
        if (isCarryingSugar()) {
            final AntGame.Hill hill = game.getHill();
            if (new Vector2(getX(), getY()).dst(hill.getCenterX(), hill.getCenterY()) <= HIT_RADIUS) {
                game.unload(carries);
                setCarries(null);
                state = State.IDLE;
            } else {
                print("Hill too far away");
            }
        }

        if (isCarryingApple()) {
            setCarries(null);
            state = State.IDLE;
        }
    }

    protected void sprayScent(int number, float radius) {
        game.addScent(new Scent(this, number, getX(), getY(), Math.min(radius, 100)));
    }

    protected void print(String str) {
        Gdx.app.log("MyAnt", str);
    }

    abstract void hasNothingToDo();
    abstract void seesSugar(Sugar sugar);
    abstract void seesApple(Apple apple);
    abstract void seesHill();
    abstract void hitsSugar(Sugar sugar);
    abstract void hitsApple(Apple apple);
    abstract void hitsHill();
    abstract void smellsNewScent(Scent scent);
}
