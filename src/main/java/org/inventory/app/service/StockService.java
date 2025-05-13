package org.inventory.app.service;

import org.inventory.app.dto.ProductDTO;
import org.inventory.app.model.Product;

public interface StockService {
    void updateStocksFromDTO(Product product, ProductDTO dto);
}
