package online.elves.third.fish.model.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.elves.third.fish.model.FPagination;

import java.util.List;

/**
 * 文章列表对象.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticlesObj {
    private List<Articles> articles;
    private FPagination pagination;
    private Tag tag;
}
