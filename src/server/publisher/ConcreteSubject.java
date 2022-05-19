package server.publisher;

import model.HotType;
import server.observer.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcreteSubject implements Subject{

    private Map<HotType, List<Listener>> listeners = new HashMap<>();
    private String message;
    private HotType topic;

    public ConcreteSubject() {
        for (HotType topic: HotType.values()) {
            this.listeners.put(topic, new ArrayList<>());
        }
    }

    @Override
    public boolean addSubscriber(HotType type, Listener listener) {
    List<Listener> subscribers = this.listeners.get(type);
    if(subscribers.contains(listener)){
        return false;
    } else {

        subscribers.add(listener);
        return true;
        }
    }


    @Override
    public void removeSubscriber(HotType topic, Listener listener) {
    List<Listener> subscribers = this.listeners.get(topic);
    subscribers.remove(listener);
    }

    @Override
    public void notifySubscriber() {
        List<Listener> subscribers = this.listeners.get(this.getTopic());
        System.out.println("Broadcast to: " + subscribers.size()
                            + " subscribers in : " +
                                    this.getTopic() +
                                        "topic.");
        for(Listener user : subscribers){
            if(!user.equals(this)) {
                user.update(this.getMessage());
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageContext(HotType topic, String message) {
        this.message = message;
        this.topic = topic;
        notifySubscriber();
    }

    public HotType getTopic() {
        return topic;
    }

    public void setTopic(HotType topic) {
        this.topic = topic;
    }
}
