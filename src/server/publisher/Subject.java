package server.publisher;

import model.HotType;
import server.observer.Listener;

public interface Subject {

    boolean addSubscriber(HotType type, Listener listener);

    void removeSubscriber(HotType topic, Listener listener);
    void notifySubscriber();
}
