package org.example.store.dto.category;

import org.example.store.dto.product.ProductDTO4;

import java.util.List;

public record SubCategoryDTO2 (Long id, String name, List<ProductDTO4> products){

}
