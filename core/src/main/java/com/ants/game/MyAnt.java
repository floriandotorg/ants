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

        Unloads whatever the ant is carrying if its close to the hill
        void unload()

        Returns ture if ant is walking to a goal
        boolean hasGoal()

        Return true is ant is carrying something
        boolean isCarrying()
     */

    public MyAnt(AntGame game) {
        super(game);
    }

    @Override
    void hasNothingToDo() {

    }

    @Override
    void seesSugar(Sugar sugar) {

    }

    @Override
    void hitsSugar(Sugar sugar) {

    }

    @Override
    void seesHill() {

    }

    @Override
    void hitsHill() {

    }
}
