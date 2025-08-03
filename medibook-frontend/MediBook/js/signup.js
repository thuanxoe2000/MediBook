"use strict";

const signupBtn = document.getElementById("submit");
const overlay = document.getElementById("overlay");

if (signupBtn) {
  signupBtn.addEventListener("click", (e) => {
    e.preventDefault();

    // Hiện loading + disable nút
    if (overlay) overlay.style.display = "block";
    signupBtn.disabled = true;

    // Lấy giá trị input
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const gender = document.getElementById("gender").value;
    const phoneNumber = document.getElementById("phoneNumber").value.trim();
    const address = document.getElementById("address").value.trim();

    const fields = [
      "name",
      "email",
      "password",
      "confirmPassword",
      "gender",
      "phoneNumber",
      "address",
    ];

    // Xóa lỗi cũ
    fields.forEach((field) => {
      const errEl = document.getElementById(`${field}-error`);
      if (errEl) errEl.innerText = "";
    });

    const successEl = document.getElementById("form-success");
    if (successEl) successEl.innerText = "";

    // Kiểm tra số điện thoại hợp lệ
    if (!/^\d{9,11}$/.test(phoneNumber)) {
      const phoneError = document.getElementById("phoneNumber-error");
      if (phoneError)
        phoneError.innerText = "Số điện thoại phải từ 9 đến 11 chữ số.";

      if (overlay) overlay.style.display = "none";
      signupBtn.disabled = false;
      return;
    }

    // Gửi dữ liệu lên server
    fetch("http://localhost:8080/api/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name,
        email,
        password,
        confirmPassword,
        gender,
        phoneNumber,
        address,
      }),
    })
      .then(async (res) => {
        const data = await res.json();

        // Tắt loading + enable nút
        if (overlay) overlay.style.display = "none";
        signupBtn.disabled = false;

        if (!res.ok) {
          if (data.errors) {
            for (const field in data.errors) {
              const errorElement = document.getElementById(`${field}-error`);
              if (errorElement) {
                errorElement.innerText = data.errors[field];
              }
            }
          }

          if (data.error) {
            alert(data.error);
          }

          return;
        }

        if (successEl) successEl.innerText = data.message;
        setTimeout(() => {
          window.location.href = "signup-success.html";
        }, 2000);
      })
      .catch((err) => {
        if (overlay) overlay.style.display = "none";
        signupBtn.disabled = false;
        alert(err.message);
      });
  });

  // Xóa lỗi khi nhập lại
  const fields = [
    "name",
    "email",
    "password",
    "confirmPassword",
    "gender",
    "phoneNumber",
    "address",
  ];
  fields.forEach((field) => {
    const input = document.getElementById(field);
    input?.addEventListener("input", () => {
      const errorElement = document.getElementById(`${field}-error`);
      if (errorElement) errorElement.innerText = "";
    });
  });
}
