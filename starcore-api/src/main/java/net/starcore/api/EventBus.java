package net.starcore.api;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public interface EventBus {
    void register(Listener listener);

    void unregister(Listener listener);

    void publish(Event event);
}
