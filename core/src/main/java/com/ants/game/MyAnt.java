package com.ants.game;

import com.badlogic.gdx.assets.AssetManager;

public class MyAnt extends Ant {
    /*
        Walks around indefinitely
        void walkAround()

        Walk a certain distance
        void walk(float distance)

        Walks to object
        void walkTo(GameObject object)

        Walks to the ant hill
        void walkToHill()

        Turns around n degrees
        void turn(float deg)

        Takes sugar from ground if some is there
        void take(Sugar sugar)

        Takes apple from ground if some is there
        void take(Apple apple)

        Unloads whatever the ant is carrying if its close to the hill
        void unload()

        Spray a scent mark with a number and radius (max. 100).
        One Ant can only spray one mark at the time.
        Ants cannot smell their own scents.
        void sprayScent(int number, float radius)

        Returns ture if ant is walking to a goal
        boolean hasGoal()

        Returns the current goal or null
        boolean getGoal()

        Return true is ant is carrying something
        boolean isCarrying()

        Return true is ant is carrying sugar
        boolean isCarryingSugar()

        Return true is ant is carrying an apple
        boolean isCarryingApple()
     */

    public MyAnt(AntGame game) {
        super(game);
    }

    @Override
    void hasNothingToDo() {
        walkAround();
    }

    @Override
    void seesSugar(Sugar sugar) {
        //sprayScent(1, 100);


    }

    @Override
    void seesApple(Apple apple) {
        if (!isCarrying() && apple.needsMoreAnts()) {
            walkTo(apple);
        }
    }

    @Override
    void hitsSugar(Sugar sugar) {

    }

    @Override
    void hitsApple(Apple apple) {
        if (!isCarrying() && apple.needsMoreAnts()) {
            take(apple);
        }
    }

    @Override
    void seesHill() {

    }

    @Override
    void hitsHill() {

    }

    @Override
    void smellsNewScent(Scent scent) {
        if (!hasGoal() && !isCarrying()) {
            walkTo(scent);
        }
    }
}
