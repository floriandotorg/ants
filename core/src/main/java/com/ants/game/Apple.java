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

import java.util.ArrayList;
import java.util.Iterator;

class Apple extends Actor implements GameObject {
    final int MAX_ANTS = 10;
    final float HIT_RADIUS = 20;
    final float SPEED = 30;

    private Sprite sprite;
    private ArrayList<Ant> ants;
    private AntGame game;

    public Apple(AntGame game) {
        sprite = new Sprite(game.manager.get("apple.png", Texture.class));
        ants = new ArrayList<>();
        this.game = game;
    }

    @Override
    public void act(float delta) {
        final AntGame.Hill hill = game.getHill();
        final Vector2 point = hill.getCenter().sub(getCenter()).nor().scl(SPEED * delta * (ants.stream().count() / (float)MAX_ANTS));

        moveBy(point.x, point.y);
        for (Ant ant : ants) {
            ant.moveBy(point.x, point.y);
        }

        if (new Vector2(getCenterX(), getCenterY()).dst(hill.getCenterX(), hill.getCenterY()) < HIT_RADIUS) {
            for (Ant ant : (ArrayList<Ant>)ants.clone()) {
                ant.unload();
            }
            game.unload(this);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setCenter(getX(), getY());
        sprite.setRotation(getRotation());
        sprite.draw(batch);
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    public void removeAnt(Ant ant) {
        ants.remove(ant);
    }

    public boolean needsMoreAnts() {
        return ants.stream().count() < MAX_ANTS;
    }

    public float getCenterX() {
        return getX() + getWidth()/2;
    }

    public float getCenterY() {
        return getY() + getHeight()/2;
    }

    public Vector2 getCenter() {
        return new Vector2(getCenterX(), getCenterY());
    }

    public String toString() {
        return String.format("Apple at %f, %f", getCenterX(), getCenterY());
    }
}
