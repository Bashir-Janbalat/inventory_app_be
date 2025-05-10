package org.inventory.app.mapper;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.StockDTO;
import org.inventory.app.model.Stock;
import org.inventory.app.repository.WarehouseRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockMapper {

    private final WarehouseMapper warehouseMapper;
    private final WarehouseRepository warehouseRepository;

    public StockDTO toDto(Stock stock) {
        StockDTO dto = new StockDTO();
        dto.setQuantity(stock.getQuantity());
        dto.setWarehouse(warehouseMapper.toDto(stock.getWarehouse()));
        return dto;
    }

    public Stock toEntity(StockDTO stockDTO) {
        Stock stockEntity = new Stock();
        stockEntity.setQuantity(stockDTO.getQuantity());
        warehouseRepository.findById(stockDTO.getWarehouse().getId()).ifPresent(stockEntity::setWarehouse);
        return stockEntity;
    }
}
