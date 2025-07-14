const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "/login.html";
}

window.addEventListener("DOMContentLoaded", renderUserDetail);

async function renderUserDetail() {
  const detail = document.getElementById("user-detail");
  detail.innerHTML = "";

  try {
    const res = await fetch("http://localhost:8080/api/user-detail", {
      headers: {
        Authorization: "Bearer " + token,
      },
    });

    if (!res.ok) throw new Error("Không thể lấy thông tin người dùng");

    const data = await res.json();

    detail.innerHTML = `
      <div class="d-flex justify-content-center align-items-center" style="min-height: 100vh;">
        <div class="card p-4 shadow" style="min-width: 350px; max-width: 500px; width: 100%;">
          <h4 class="text-center text-primary mb-4">Thông tin người dùng</h4>

          <p class="py-2"><strong>Họ tên:</strong> <span id="name" contenteditable="false" data-original="${
            data.name
          }">${data.name}</span></p>
          <p class="py-2"><strong>Email:</strong> <span id="email" contenteditable="false" data-original="${
            data.email
          }">${data.email}</span></p>
          <p class="py-2"><strong>Số điện thoại:</strong> <span id="phone" contenteditable="false" data-original="${
            data.phoneNumber || "Chưa có"
          }">${data.phoneNumber || "Chưa có"}</span></p>
          <p class="py-2"><strong>Địa chỉ:</strong> <span id="address" contenteditable="false" data-original="${
            data.address || "Chưa có"
          }">${data.address || "Chưa có"}</span></p>

          <p class="py-2"><strong>Ngày tạo:</strong> ${new Date(
            data.createdAt
          ).toLocaleDateString()}</p>

          <div class="text-center mt-4" id="button-group">
            <button class="btn btn-primary me-2" onclick="enableEdit()">Sửa thông tin</button>
            <button class="btn btn-warning" onclick="changePassword()">Đổi mật khẩu</button>
          </div>
        </div>
      </div>
    `;
  } catch (err) {
    alert("Lỗi: " + err.message);
  }
}

function enableEdit() {
  const fields = ["name", "email", "phone", "address"];
  const group = document.getElementById("button-group");

  fields.forEach((id) => {
    const el = document.getElementById(id);
    el.contentEditable = "true";
    el.classList.add("border", "rounded", "bg-white", "px-1");
  });

  group.innerHTML = `
    <button class="btn btn-success me-2" onclick="saveInfo()">Lưu</button>
    <button class="btn btn-secondary" onclick="cancelEdit()">Huỷ</button>
  `;
}

function cancelEdit() {
  const fields = ["name", "email", "phone", "address"];
  const group = document.getElementById("button-group");

  fields.forEach((id) => {
    const el = document.getElementById(id);
    const original = el.getAttribute("data-original");
    el.textContent = original;
    el.contentEditable = "false";
    el.classList.remove("border", "rounded", "bg-white", "px-1");
  });

  group.innerHTML = `
    <button class="btn btn-primary me-2" onclick="enableEdit()">Sửa thông tin</button>
    <button class="btn btn-warning" onclick="changePassword()">Đổi mật khẩu</button>
  `;
}

function saveInfo() {
  const fields = ["name", "email", "phone", "address"];
  const data = {};

  for (let id of fields) {
    const value = document.getElementById(id).innerText.trim();
    if (!value) {
      alert("Không được để trống thông tin.");
      cancelEdit();
      return;
    }
    data[getFieldKey(id)] = value;
  }

  const phone = data.phoneNumber;
  const phoneRegex = /^\d{9,15}$/;
  if (!phoneRegex.test(phone)) {
    alert("Số điện thoại không hợp lệ. Vui lòng nhập 9-15 chữ số.");
    return;
  }

  fetch("http://localhost:8080/api/update-user", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    },
    body: JSON.stringify(data),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Lỗi khi cập nhật");
      return res.text();
    })
    .then(() => {
      alert("Cập nhật thành công!");
      renderUserDetail();
    })
    .catch((err) => alert(err.message));
}

function getFieldKey(id) {
  return {
    name: "name",
    email: "email",
    phone: "phoneNumber",
    address: "address",
  }[id];
}

function changePassword() {
  window.location.href = "change-password.html";
}
