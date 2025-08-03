const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

document
  .getElementById("createHospitalForm")
  .addEventListener("submit", async (e) => {
    e.preventDefault();

    const data = {
      name: document.getElementById("name").value.trim(),
      address: document.getElementById("address").value.trim(),
      email: document.getElementById("email").value.trim(),
      phoneNumber: document.getElementById("phoneNumber").value.trim(),
      cost: document.getElementById("cost").value,
      startTime: document.getElementById("startTime").value,
      endTime: document.getElementById("endTime").value,
      description: document.getElementById("description").value,
      imageUrl: document.getElementById("imageUrl").value,
      director: {
        name: document.getElementById("directorName").value.trim(),
        email: document.getElementById("directorEmail").value.trim(),
        password: document.getElementById("directorPassword").value,
        gender: document.getElementById("directorGender").value,
        phoneNumber: document
          .getElementById("directorPhoneNumber")
          .value.trim(),
        address: document.getElementById("directorAddress").value.trim(),
      },
    };

    const phone = document.getElementById("phoneNumber").value.trim();
    const directorPhone = document
      .getElementById("directorPhoneNumber")
      .value.trim();

    const phoneRegex = /^[0-9]{9,11}$/;

    if (!phoneRegex.test(phone)) {
      alert("Số điện thoại bệnh viện không hợp lệ!");
      return;
    }

    if (!phoneRegex.test(directorPhone)) {
      alert("Số điện thoại viện trưởng không hợp lệ!");
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/hospital/create", {
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
        throw new Error("Tạo bệnh viện thất bại");
      }

      alert("Tạo bệnh viện thành công!");
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
