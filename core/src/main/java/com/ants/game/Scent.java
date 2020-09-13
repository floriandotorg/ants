package com.ants.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Scent implements GameObject {
    private int number;
    private float x;
    private float y;
    private float radius;
    private float ttl;
    private Ant ant;
    private ArrayList<Ant> alreadySmelled;

    final float TTL = 2;

    public Scent(Ant ant, int number, float x, float y, float radius) {
        this.ant = ant;
        this.number = number;
        this.x = x;
        this.y = y;
        this.radius = radius;
        alreadySmelled = new ArrayList<>();
        ttl = TTL;
    }

    public int getNumber() {
        return number;
    }

    public float getCenterX() {
        return x;
    }

    public float getCenterY() {
        return y;
    }

    public Vector2 getCenter() {
        return new Vector2(getCenterX(), getCenterY());
    }

    public void tick(float delta) {
        ttl -= delta;
    }

    public Ant getAnt() {
        return ant;
    }

    public boolean vanished() {
        return ttl <= 0;
    }

    public float getRadius() {
        return radius;
    }

    public void smelled(Ant ant) {
        alreadySmelled.add(ant);
    }

    public boolean hasAlreadySmalled(Ant ant) {
        return alreadySmelled.contains(ant);
    }

    @Override
    public String toString() {
        return String.format("Scent %d at %f, %f", getNumber(), getCenterX(), getCenterY());
    }
}
