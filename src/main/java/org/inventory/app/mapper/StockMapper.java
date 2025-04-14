package org.inventory.app.mapper;

import org.inventory.app.dto.StockDTO;
import org.inventory.app.model.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public StockDTO toDto(Stock stock) {
        StockDTO dto = new StockDTO();
        dto.setQuantity(stock.getQuantity());
        dto.setWarehouseLocation(stock.getWarehouseLocation());
        return dto;
    }

    public Stock toEntity(StockDTO stockDTO) {
        Stock stockEntity = new Stock();
        stockEntity.setQuantity(stockDTO.getQuantity());
        stockEntity.setWarehouseLocation(stockDTO.getWarehouseLocation());
        return stockEntity;
    }
}
