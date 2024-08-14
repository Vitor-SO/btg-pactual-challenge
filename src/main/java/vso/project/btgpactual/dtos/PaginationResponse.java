package vso.project.btgpactual.dtos;

import org.springframework.data.domain.Page;

public record PaginationResponse(
        Integer page,
        Integer pageSize,
        Long totalItems,
        Integer totalPages
) {
    public static PaginationResponse fromPage(Page<?> page){
        return new PaginationResponse(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
