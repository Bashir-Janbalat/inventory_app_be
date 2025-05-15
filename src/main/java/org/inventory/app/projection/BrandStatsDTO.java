package org.inventory.app.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandStatsDTO implements Serializable {

    private Long id;
    private String name;
    private Long productCount;
    private Long totalStock;

}
