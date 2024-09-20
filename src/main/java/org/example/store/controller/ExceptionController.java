package org.example.store.controller;

import org.example.store.dto.order.OrderItemCreateDTO;
import org.example.store.exception.ErrorData;
import org.example.store.exception.ErrorMessage;
import org.example.store.exception.ValidationErrorMessage;
import org.example.store.exception.company.CompanyNotFoundException;
import org.example.store.exception.filter.FilterNotFoundException;
import org.example.store.exception.filtervalue.FilterValueNotFoundException;
import org.example.store.exception.maincategory.MainCategoryNotFoundException;
import org.example.store.exception.order.OrderNotFoundException;
import org.example.store.exception.product.ProductNotFoundException;
import org.example.store.exception.product.ProductPropertiesChanged;
import org.example.store.exception.product.SizeNotFoundException;
import org.example.store.exception.subcategory.SubcategoryNotFoundException;
import org.example.store.exception.user.MoneyNotEnoughException;
import org.example.store.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ValidationErrorMessage> validationErrorException(MethodArgumentNotValidException e) {
        List<String> details = getAllErrorsDetails(e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ValidationErrorMessage(
                        HttpStatus.CONFLICT.toString(),
                        "Validation error, check details",
                        LocalDateTime.now(),
                        details
                ));
    }

    @ExceptionHandler({
            FilterNotFoundException.class,
            FilterValueNotFoundException.class,
            MainCategoryNotFoundException.class,
            OrderNotFoundException.class,
            ProductNotFoundException.class,
            SizeNotFoundException.class,
            SubcategoryNotFoundException.class,
            CompanyNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ErrorMessage> notFoundException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(
                        HttpStatus.NOT_FOUND.toString(),
                        e.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler({
            ProductPropertiesChanged.class,
    })
    public ResponseEntity<ErrorData<OrderItemCreateDTO>> productPropertiesChanged(ProductPropertiesChanged  e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorData<>(
                        HttpStatus.NOT_FOUND.toString(),
                        e.getChangedProducts(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler({
            MoneyNotEnoughException.class,
    })
    public ResponseEntity<ErrorMessage> productPropertiesChanged(RuntimeException  e) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorMessage(
                        HttpStatus.NOT_ACCEPTABLE.toString(),
                        e.getMessage(),
                        LocalDateTime.now()));
    }

    private List<String> getAllErrorsDetails(MethodArgumentNotValidException e) {
        List<String> details = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            details.add(fieldName + ": " + errorMessage);
        });
        return details;
    }
}
