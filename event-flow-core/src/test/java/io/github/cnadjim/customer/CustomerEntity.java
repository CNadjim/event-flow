package io.github.cnadjim.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    private String id;
    private String name;
    private Boolean enabled;
}
