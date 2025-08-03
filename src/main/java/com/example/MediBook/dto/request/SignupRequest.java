package com.example.MediBook.dto.request;

import com.example.MediBook.annotation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "password", second = "confirmPassword", message = "パスワードが一致しません")
public class SignupRequest {

    @Size(min = 8, message = "パスワードは8文字以上でなければなりません")
    @NotBlank(message = "空欄にできません")
    private String password;

    @NotBlank(message = "空欄にできません")
    private String confirmPassword;

    @NotBlank(message = "空欄にできません")
    private String name;

    @Email(message = "有効なメールアドレスを入力してください")
    @NotBlank(message = "空欄にできません")
    private String email;

    @NotBlank(message = "空欄にできません")
    private String gender;

    @NotBlank(message = "空欄にできません")
    private String phoneNumber;

    @NotBlank(message = "空欄にできません")
    private String address;
}
