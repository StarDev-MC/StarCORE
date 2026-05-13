package net.starcore.starecon.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import net.starcore.starecon.model.Transaction;

/**
 * Called when any economy transaction occurs.
 */
public class EconomyTransactionEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Transaction transaction;

    public EconomyTransactionEvent(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
