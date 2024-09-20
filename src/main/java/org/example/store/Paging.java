package org.example.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Paging {

    public <T> Page<T> pageImpl(List<T> elements, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), elements.size());
        List<T> pageElements = elements.subList(start, end);
        return new PageImpl<>(pageElements, pageable, elements.size());
    }

}
