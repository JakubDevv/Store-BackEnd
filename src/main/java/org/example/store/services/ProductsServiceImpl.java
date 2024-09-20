package org.example.store.services;

import lombok.RequiredArgsConstructor;
import org.example.store.Paging;
import org.example.store.ProductSorter;
import org.example.store.dto.category.MainCategoryDTO;
import org.example.store.dto.category.SubCategoryDTO;
import org.example.store.dto.category.SubCategoryDTO2;
import org.example.store.dto.filters.FilterDTO;
import org.example.store.dto.order.OrderItemCreateDTO;
import org.example.store.dto.product.*;
import org.example.store.dto.stats.HomeStats;
import org.example.store.exception.product.ProductNotFoundException;
import org.example.store.mapper.ProductMapper;
import org.example.store.model.*;
import org.example.store.repository.*;
import org.example.store.s3.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {

    @Value("${bucket.name}")
    private String bucketName;

    private final ProductRepository productRepository;
    private final S3Service s3Service;
    private final SubCategoryRepository subCategoryRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final ProductMapper productMapper;
    private final ProductSorter productSorter;
    private final Paging paging;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> getProductPhoto(Long productId, int photoNumber) {
        try {
            Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
            if (product.getImages().size() > photoNumber) {
                Image image1 = product.getImages().get(photoNumber);
                byte[] image = s3Service.getObject(
                        bucketName,
                        "products/" + image1.getName()
                );
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.valueOf("image/png"))
                        .body(image);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new ArithmeticException();
        }
    }

    @Override
    public List<SubCategoryDTO> getSubCategories() {
        return subCategoryRepository.getSubCategories();
    }

    @Override
    public List<ProductDTO3> getProductsSortedByRating() {
        return productRepository.getMostRatedProducts(Pageable.ofSize(3)).stream().map(productMapper::mapProductToProductDTO3).toList();
    }

    @Override
    public ProductRatingDTO calculateProductRating(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return ProductRatingDTO.builder()
                .average(product.getReviews().stream().mapToInt(ProductReview::getRating).average().orElse(0.0))
                .amount(product.getReviews().size())
                .fiveStar(product.getReviews().stream().filter(product1 -> product1.getRating() == 5).count())
                .fourStar(product.getReviews().stream().filter(product2 -> product2.getRating() == 4).count())
                .threeStar(product.getReviews().stream().filter(product3 -> product3.getRating() == 3).count())
                .twoStar(product.getReviews().stream().filter(product4 -> product4.getRating() == 2).count())
                .oneStar(product.getReviews().stream().filter(product5 -> product5.getRating() == 1).count())
                .build();
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return productMapper.mapProductToProductDTO(product);
    }

    @Override
    public Page<ProductDTO3> getFormattedProductsByQuery(String query, int page) {
        Pageable amountOfElements = PageRequest.of(page, 9);
        Page<Product> products = productRepository.getProductsByQuery(query, amountOfElements);
        return products.map(productMapper::mapProductToProductDTO3);
    }

    @Override
    public List<MainCategoryDTO> getCategories() {
        return mainCategoryRepository.findMainCategoriesByDeletedIsNull()
                .stream()
                .map(product -> new MainCategoryDTO(
                        product.getId(),
                        product.getName(),
                        product.getSubCategories().stream()
                                .filter(subCategory -> subCategory.getDeleted() == null)
                                .map(subCategory -> new SubCategoryDTO(subCategory.getId(), subCategory.getName()))
                                .toList()
                ))
                .toList();
    }

    @Override
    public List<FilterDTO> getFiltersForSubcategory(String category) {
        return subCategoryRepository.getSubCategoryByName(category).getFilters().stream().map(productMapper::mapFilterToFilterDTO).toList();
    }

    @Override
    public Page<ProductDTO3> getNewProducts(int page) {
        Pageable amountOfElements = PageRequest.of(page, 12);
        Page<Product> productsPremiered = productRepository.getPremieredProducts(LocalDateTime.now().minusDays(120), amountOfElements);
        return productsPremiered.map(productMapper::mapProductToProductDTO3);
    }

    @Override
    public Page<ProductDTO3> getProductsByCategory(String category, Integer page) {
        Pageable amountOfElements = PageRequest.of(page, 9);
        Page<Product> products = subCategoryRepository.getProductsBySubCategory(category, amountOfElements);
        return products.map(productMapper::mapProductToProductDTO3);
    }

    @Override
    public Page<ProductDTO3> getFilteredProducts(String category, Map<String, String> allFilters, BigDecimal from, BigDecimal to) {
        int pageNum = allFilters.get("page") != null ? Integer.parseInt(allFilters.get("page")) - 1 : 0;
        String sorting = allFilters.get("sort");
        allFilters.remove("sort");
        allFilters.remove("from");
        allFilters.remove("to");
        allFilters.remove("page");

        List<Product> products = subCategoryRepository.getSubCategoryByName(category)
                .getProducts()
                .stream()
                .filter(product -> product.getRetired() == null)
                .filter(product -> {
                    BigDecimal productPrice = product.getDiscount_price() != null ? product.getDiscount_price() : product.getPrice();
                    return (from == null || productPrice.compareTo(from) > 0) && (to == null || productPrice.compareTo(to) < 0); })
                .toList();

        List<Product> products1 = new ArrayList<>();
        for (Product product : products) {
            int filters = allFilters.size();
            for (Parameter parameter : product.getParameters()) {
                for (Map.Entry<String, String> entry : allFilters.entrySet()) {
                    if (parameter.compareFilters(entry.getKey(), entry.getValue())) {
                        filters--;
                    }
                }
            }
            if (filters <= 0) {
                products1.add(product);
            }
        }
        List<ProductDTO3> list = new ArrayList<>(products1.stream().map(productMapper::mapProductToProductDTO3).toList());

        productSorter.sortBy(list, sorting);

        return paging.pageImpl(list, pageNum, 9);
    }

    @Override
    public Page<ProductReviewDTO> getProductReviews(Long id, int pageNum) {
        Page<ProductReview> productReviews = productRepository.getProductReviewsByProductId(id, PageRequest.of(pageNum, 2));
        return productReviews.map(productMapper::mapProductReviewToProductReviewDTO);
    }

    @Override
    public List<SubCategoryDTO2> getProductsSortedBySales() {
        return subCategoryRepository.findAll().stream().map(category -> new SubCategoryDTO2(category.getId(), category.getName(), productRepository.getProductsByCategoryIdAndSortBySales(category.getId()).stream().map(productMapper::mapProductToProductDTO4).toList())).toList();
    }

    @Override
    public Page<ProductDTO3> getDiscountedProducts(Integer pageNum) {
        Page<Product> discountedProducts = productRepository.getDiscountedProducts(PageRequest.of(pageNum, 9));
        return discountedProducts.map(productMapper::mapProductToProductDTO3);
    }

    @Override
    public HomeStats getStats() {
        return new HomeStats(productRepository.count(), orderRepository.count(), userRepository.count());
    }

    @Override
    public ResponseEntity<?> getUserPhoto(Long id) {
        try {
            byte[] image = s3Service.getObject(
                    bucketName,
                    "profilePhoto/" + id
            );

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(image);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<OrderItemCreateDTO> validateProducts(List<OrderItemCreateDTO> products) {
        List<OrderItemCreateDTO> validProducts = new ArrayList<>();

        for (OrderItemCreateDTO productDTO : products) {
            Long productId = productDTO.id();
            int quantity = productDTO.quantity();
            String sizeId = productDTO.sizeId();
            String size = productDTO.size();
            String name = productDTO.name();

            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                continue;
            }
            Product product = productOpt.get();

            if(!Objects.equals(product.getName(), name)){
                productDTO = new OrderItemCreateDTO(productId, quantity, product.getName(), productDTO.price(), sizeId, size);
            }
            Optional<Size> pSize = product.getSizes().stream().filter(pSizee -> String.valueOf(pSizee.getId()).equals(sizeId)).findFirst();
            if (pSize.isEmpty()) {
                continue;
            }
            Size size1 = pSize.get();
            if(!size1.getSizevalue().equals(productDTO.size())) {
                continue;
            }

            int availableQuantity = size1.getQuantity();
            if (quantity > availableQuantity) {
                productDTO = new OrderItemCreateDTO(productId, availableQuantity, productDTO.name(), productDTO.price(), productDTO.sizeId(), productDTO.size());
            }

            BigDecimal currentPrice = product.getDiscount_price() == null ? product.getPrice() : product.getDiscount_price();
            if (!currentPrice.setScale(0, BigDecimal.ROUND_DOWN).equals(productDTO.price().setScale(0, BigDecimal.ROUND_DOWN))) {
                productDTO = new OrderItemCreateDTO(productId, productDTO.quantity(), productDTO.name(), currentPrice, productDTO.sizeId(), productDTO.size());
            }
            validProducts.add(productDTO);
        }

        return validProducts;
    }
}
