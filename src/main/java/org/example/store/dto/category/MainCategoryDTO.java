package org.example.store.dto.category;

import java.util.List;

public record MainCategoryDTO(Long id,
                              String name,
                              List<SubCategoryDTO> subCategory) {

}
