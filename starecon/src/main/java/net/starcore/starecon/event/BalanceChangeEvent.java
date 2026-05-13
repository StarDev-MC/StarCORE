package net.starcore.starecon.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.UUID;

/**
 * Called when a player's balance changes.
 * Cancellable for validation hooks.
 */
public class BalanceChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID playerId;
    private final double oldBalance;
    private double newBalance;
    private final String reason;

    public BalanceChangeEvent(UUID playerId, double oldBalance, double newBalance, String reason) {
        this.playerId = playerId;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.reason = reason;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double balance) {
        this.newBalance = balance;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
