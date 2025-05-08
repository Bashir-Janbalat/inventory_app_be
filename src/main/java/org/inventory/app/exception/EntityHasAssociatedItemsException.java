package org.inventory.app.exception;

public class EntityHasAssociatedItemsException extends RuntimeException {


    public EntityHasAssociatedItemsException(String entityName, Long entityId) {
        super(String.format("%s with ID %d has associated items and cannot be deleted. Please delete associated items first.", entityName, entityId));
    }

}
