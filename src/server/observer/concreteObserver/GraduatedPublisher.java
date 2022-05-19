package server.observer.concreteObserver;

import model.HotType;
import server.observer.Listener;

public class GraduatedPublisher implements Listener {
    private HotType topic;

    public GraduatedPublisher(HotType topic) {
        this.topic = topic;
    }

    @Override
    public void update(String message) {

    }
}
