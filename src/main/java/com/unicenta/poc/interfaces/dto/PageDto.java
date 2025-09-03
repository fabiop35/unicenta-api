package com.unicenta.poc.interfaces.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto<T> {

    private List<T> content;
    private int number;           // Current page number (0-based)
    private int size;             // Page size
    private long totalElements;   // Total number of elements
    private int totalPages;       // Total number of pages
    private boolean first;        // Is this the first page?
    private boolean last;         // Is this the last page?
    private boolean empty;        // Is the page empty?
}
