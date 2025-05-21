package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.model.Product;
import org.inventory.app.service.StockService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class StockServiceImpl implements StockService {

    private final StockUpdater stockUpdater;

    @Override
    public void updateStocksFromDTO(Product product, ProductDTO dto) {
        stockUpdater.updateStocksFromDTO(product, dto);
    }

}
