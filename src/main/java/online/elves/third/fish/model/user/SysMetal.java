package online.elves.third.fish.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户勋章
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysMetal {
    private String name;
    private String description;
    private String data;
    private String attr;
    private boolean enabled;
}
