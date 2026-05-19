package roomescape.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull @NotBlank(message = "ID는 비어 있을 수 없습니다.")
        String loginId,

        @NotNull @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
        String password
) {
}
