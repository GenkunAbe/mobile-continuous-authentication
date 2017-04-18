package org.genku.touchauth.Model;

import java.util.concurrent.Callable;

/**
 * Created by genku on 4/9/2017.
 */

public class TouchPostEventMethod implements Callable<Void> {

    protected TouchEvent event;
    protected StringBuilder sb;

    public void setParam(TouchEvent event, StringBuilder sb) {
        this.event = event;
        this.sb = sb;
    }

    @Override
    public Void call() throws Exception {
        return null;
    }
}
