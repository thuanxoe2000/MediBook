package com.example.MediBook.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Map;

@Getter
public enum BookingStatus {
    PENDING("処理待ち"),
    CONFIRMED("予約済み"),
    CANCELED("キャンセル済み"),
    FINISHED("完了");

    private final String message;

    BookingStatus(String message) {
        this.message = message;
    }

    @JsonValue
    public Map<String, String> toJson() {
        return Map.of(
                "name", this.name(),
                "message", this.message
        );
    }
}
