package org.example.posFX.objects;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto (String name, BigDecimal price, List<String>ingredients){
}
