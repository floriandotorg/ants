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
    WALKING
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
        sprite.setScale(0.2f);
        setRotation(MathUtils.random(0, 360));

        sugar = new Sprite(game.manager.get("sugar.png", Texture.class));
        sugar.setScale(0.1f);
    }

    @Override
    public void act(float delta) {
        for (GameObject object : game.getGameObjectsInRadius(getX(), getY(), VIEW_RADIUS)) {
            if (object instanceof Sugar) {
                seesSugar((Sugar)object);
            }

            if (object instanceof AntGame.Hill) {
                seesHill();
            }
        }

        for (GameObject object : game.getGameObjectsInRadius(getX(), getY(), HIT_RADIUS)) {
            if (object == goal) {
                goal = null;
                state = State.IDLE;
            }

            if (object instanceof Sugar) {
                hitsSugar((Sugar)object);
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
                final float width = (sprite.getWidth() * sprite.getScaleX()) / 2;
                final float height = (sprite.getHeight() * sprite.getScaleY()) / 2;

                if (getX() + width >= game.WIDTH) {
                    rotateBy(90);
                }
                if (getX() - width <= 0) {
                    rotateBy(90);
                }
                if (getY() + height >= game.HEIGHT) {
                    rotateBy(90);
                }
                if (getY() - height <= 0) {
                    rotateBy(90);
                }

                final float xOffset = SPEED * MathUtils.cos(MathUtils.degreesToRadians * getRotation()) * delta;
                final float yOffset = SPEED * MathUtils.sin(MathUtils.degreesToRadians * getRotation()) * delta;
                moveBy(xOffset, yOffset);

                break;
        }

        sprite.setPosition(getX() - sprite.getWidth()/2, getY() - sprite.getHeight()/2);
        sprite.setRotation(getRotation() + 90);
        sugar.setPosition(getX() - sugar.getWidth()/2, getY() - sugar.getHeight()/2);
        sugar.setRotation(sprite.getRotation());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);

        if (carries instanceof Sugar) {
            sugar.draw(batch);
        }
    }

    protected void walkAround() {
        state = State.WALKING;
    }

    protected void walkTo(GameObject object) {
        goal = object;
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

    protected boolean isCarrying() {
        return carries != null;
    }

    protected void take(Sugar sugar) {
        carries = sugar;
    }

    protected void turn(float deg) {
        rotateBy(deg);
    }

    protected void walk(float distance) {
        final Vector2 point = new Vector2(getX(), getY()).add(new Vector2((float)Math.cos(getRotation() * MathUtils.degreesToRadians), (float)Math.sin(getRotation() * MathUtils.degreesToRadians)).scl(distance));
        goal = game.createMarker(point.x, point.y);
        state = State.WALKING;
    }

    protected void unload() {
        final AntGame.Hill hill = game.getHill();
        if (isCarrying() && new Vector2(getX(), getY()).dst(hill.getCenterX(), hill.getCenterY()) <= HIT_RADIUS) {
            game.unload(carries);
            carries = null;
            state = State.IDLE;
        }
    }

    protected void print(String str) {
        Gdx.app.log("MyAnt", str);
    }

    abstract void hasNothingToDo();
    abstract void seesSugar(Sugar sugar);
    abstract void seesHill();
    abstract void hitsSugar(Sugar sugar);
    abstract void hitsHill();
}
