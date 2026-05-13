package net.starcore.starecon.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.UUID;

/**
 * Called when a player pays another player.
 */
public class PlayerPayEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID senderId;
    private final UUID receiverId;
    private final double amount;

    public PlayerPayEvent(UUID senderId, UUID receiverId, double amount) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
