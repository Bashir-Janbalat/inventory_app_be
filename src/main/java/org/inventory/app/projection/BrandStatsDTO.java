package org.inventory.app.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandStatsDTO implements Serializable {

    private long id;
    private String name;
    private long productCount;
    private long totalStock;

}
