package net.starcore.starecon.model;

/**
 * Transaction type enumeration for categorizing economy operations.
 */
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    TRANSFER("Transfer"),
    ADMIN_SET("Admin Set"),
    ADMIN_ADD("Admin Add"),
    ADMIN_REMOVE("Admin Remove"),
    PAYMENT("Payment"),
    SHOP_SELL("Shop Sell"),
    SHOP_BUY("Shop Buy"),
    BANK_DEPOSIT("Bank Deposit"),
    BANK_WITHDRAW("Bank Withdraw");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
