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

class Sugar extends Image implements GameObject {
    public Sugar(AntGame game) {
        super(game.manager.get("sugar.png", Texture.class));
    }

    @Override
    public float getCenterX() {
        return getX() + getWidth()/2;
    }

    @Override
    public float getCenterY() {
        return getY() + getHeight()/2;
    }

    @Override
    public String toString() {
        return String.format("Sugar at %f, %f", getCenterX(), getCenterY());
    }
}
