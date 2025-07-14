const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

const urlParams = new URLSearchParams(window.location.search);
const hospitalId = urlParams.get("id");

document
  .getElementById("createDepartmentForm")
  .addEventListener("submit", async (e) => {
    e.preventDefault();

    const data = {
      name: document.getElementById("name").value.trim(),
      description: document.getElementById("description").value.trim(),
      cost: parseInt(document.getElementById("cost").value),
      hospitalId: parseInt(hospitalId),
      head: {
        name: document.getElementById("headName").value.trim(),
        email: document.getElementById("headEmail").value.trim(),
        password: document.getElementById("headPassword").value,
        gender: document.getElementById("headGender").value,
        phoneNumber: document.getElementById("headPhoneNumber").value.trim(),
        address: document.getElementById("headAddress").value.trim(),
      },
    };

    const phone = data.head.phoneNumber;
    const phoneRegex = /^[0-9]{9,11}$/;

    if (!phoneRegex.test(phone)) {
      alert("Số điện thoại trưởng khoa không hợp lệ!");
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/hospital/createDpm", {
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
        throw new Error("Tạo khoa thất bại");
      }

      alert("Tạo khoa và trưởng khoa thành công!");
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
