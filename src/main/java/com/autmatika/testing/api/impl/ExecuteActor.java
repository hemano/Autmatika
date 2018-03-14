package com.autmatika.testing.api.impl;

import com.autmatika.testing.api.AbstractActor;
import com.autmatika.testing.api.IActor;

import java.util.List;

public class ExecuteActor extends AbstractActor implements Runnable {

    /**
     * <p> Main executable doable that will contain a list of actors that will execute during run time </p>
     */
    public ExecuteActor(List<IActor> actors) {
        this.actors = actors;
    }
    public ExecuteActor() {
    }

    @Override
    public void run() {
        super.doIt();
    }
}
