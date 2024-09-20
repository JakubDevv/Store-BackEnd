package org.example.store;

import org.example.store.dto.product.ProductDTO3;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ProductSorter {

    public void sortBy(List<ProductDTO3> list, String sorting) {
        Comparator<ProductDTO3> comparator = null;

        if (Objects.equals(sorting, "rate")) {
            comparator = Comparator.comparing(ProductDTO3::starsRate).reversed();
        } else if (Objects.equals(sorting, "asc")) {
            comparator = (o1, o2) -> {
                BigDecimal o1ActualPrice = o1.discountPrice() == null ? o1.price() : o1.discountPrice();
                BigDecimal o2ActualPrice = o2.discountPrice() == null ? o2.price() : o2.discountPrice();
                return o1ActualPrice.compareTo(o2ActualPrice);
            };
        } else if (Objects.equals(sorting, "desc")) {
            comparator = (o1, o2) -> {
                BigDecimal o1ActualPrice = o1.discountPrice() == null ? o1.price() : o1.discountPrice();
                BigDecimal o2ActualPrice = o2.discountPrice() == null ? o2.price() : o2.discountPrice();
                return o2ActualPrice.compareTo(o1ActualPrice);
            };
        } else if (Objects.equals(sorting, "sales")) {
            comparator = Comparator.comparingInt(ProductDTO3::sales).reversed();
        }

        if (comparator != null) {
            list.sort(comparator);
        }
    }
}
