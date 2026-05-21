package roomescape.domain.member;

import java.util.Arrays;
import roomescape.common.exception.NotFoundException;

public enum Role {
    GENERAL("general"),
    MANAGER("manager"),
    ;

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static Role from(String roleName) {
        return Arrays.stream(values())
                .filter(role -> role.roleName.equals(roleName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("적절하지 않은 역할 이름입니다."));
    }
}
