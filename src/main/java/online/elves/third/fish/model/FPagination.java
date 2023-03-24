package online.elves.third.fish.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页对象.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPagination {
    private int paginationPageCount;
    private List<Integer> paginationPageNums;
}
