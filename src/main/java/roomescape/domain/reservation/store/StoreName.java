package roomescape.domain.reservation.store;

public record StoreName (
        String value
){
    public static final int NAME_MAX_LENGTH = 20;

    public StoreName {
        if (value.isBlank()) {
            throw new IllegalArgumentException("매장 이름은 비어 있을 수 없습니다.");
        }

        if (value.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("매장 이름은 %d자를 초과할 수 없습니다.".formatted(NAME_MAX_LENGTH));
        }
    }

    public static StoreName parse(String value) {
        return new StoreName(value);
    }
}
