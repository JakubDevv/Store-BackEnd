package org.example.store.dto.category;

import java.time.LocalDateTime;
import java.util.List;

public record MainCategoryAdminDTO(Long id,
                                   String name,
                                   int products,
                                   int sales,
                                   LocalDateTime deleted,
                                   List<SubCategoryAdminDTO> subCategories) {

}
