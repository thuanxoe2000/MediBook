const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

document.getElementById("submit").addEventListener("click", async () => {
  const urlParams = new URLSearchParams(window.location.search);
  const hospitalId = parseInt(urlParams.get("id"));

  const data = {
    name: document.getElementById("name").value,
    address: document.getElementById("address").value,
    email: document.getElementById("email").value,
    phoneNumber: parseInt(document.getElementById("phoneNumber").value),
    password: document.getElementById("password").value,
    gender: document.getElementById("gender").value,
    hospitalId: hospitalId,
  };

  try {
    const res = await fetch("http://localhost:8080/api/createDoctor", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
      body: JSON.stringify(data),
    });

    if (!res.ok) {
      const result = await res.json();
      const errors = result.errors || {};
      showErrors(errors);
      throw new Error("Tạo bác sĩ thất bại");
    }
    alert("Tạo bác sĩ thành công");
  } catch (err) {
    alert(err.message);
  }
});

function toCamelCase(field) {
  return field.replace(/\.(.)/g, (_, char) => char.toUpperCase());
}

function showErrors(errors) {
  // Xoá lỗi cũ
  document.querySelectorAll(".is-invalid").forEach((el) => {
    el.classList.remove("is-invalid");
  });
  document.querySelectorAll(".invalid-feedback").forEach((el) => {
    el.remove();
  });

  // Hiển thị lỗi mới
  Object.entries(errors).forEach(([field, message]) => {
    const inputId = toCamelCase(field);
    const input = document.getElementById(inputId);
    if (input) {
      input.classList.add("is-invalid");

      const feedback = document.createElement("div");
      feedback.className = "invalid-feedback";
      feedback.textContent = message;

      input.parentNode.appendChild(feedback);

      input.addEventListener(
        "input",
        () => {
          input.classList.remove("is-invalid");
          const fb = input.parentNode.querySelector(".invalid-feedback");
          if (fb) fb.remove();
        },
        { once: true }
      );
    }
  });
}
