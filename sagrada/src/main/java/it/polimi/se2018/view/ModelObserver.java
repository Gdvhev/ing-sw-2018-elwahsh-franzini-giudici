package it.polimi.se2018.view;

import it.polimi.se2018.view.app.App;

import java.util.Observable;
import java.util.Observer;

/**
 * Class for View -> Model communication
 *
 */

public class ModelObserver implements Observer {

    private App app;

    @Override
    public void update(Observable o, Object arg) {

    }
}
