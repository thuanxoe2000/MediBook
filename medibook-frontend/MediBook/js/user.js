const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

const payload = parseJwt(token);
if (!payload) {
  localStorage.clear();
  window.location.href = "login.html";
}

const role = payload.role;
const name = payload.name;

// Hiển thị tên từ token
document.getElementById("name").textContent = name || "Người dùng";

// Hiển thị nút theo role
if (role === "ROLE_DEPARTMENT_HEAD" || role === "ROLE_DIRECTOR") {
  document.getElementById("hospital").classList.remove("d-none");
}

if (role === "ROLE_ADMIN") {
  document.getElementById("admin").classList.remove("d-none");
}

// Event nút
document.getElementById("booking").onclick = () => {
  window.location.href = "user-booking.html";
};

document.getElementById("hospital").onclick = () => {
  window.location.href = "my-hospital.html";
};

document.getElementById("admin").onclick = () => {
  window.location.href = "admin.html";
};

document.getElementById("update").onclick = () => {
  window.location.href = "user-detail.html";
};

document.getElementById("logout").onclick = () => {
  fetch("http://localhost:8080/auth/logout", {
    method: "POST",
    headers: { Authorization: "Bearer " + token },
  }).finally(() => {
    localStorage.clear();
    window.location.href = "home.html";
  });
};

function parseJwt(token) {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
}
