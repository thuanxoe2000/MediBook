package com.example.MediBook.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    EMAIL_ALREADY_EXISTS(1001, "このメールアドレスは既に登録されています"),
    USERNAME_TAKEN(1002, "このユーザー名は既に存在します");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
