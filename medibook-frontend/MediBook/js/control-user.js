const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

let allUser = [];

const renderUser = async function () {
  try {
    const res = await fetch("http://localhost:8080/api/list-user", {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
    const data = await res.json();
    allUser = data;
    renderUserTable(data);
  } catch (err) {
    alert("Lỗi khi tải danh sách người dùng: " + err.message);
  }
};

const renderUserTable = (users) => {
  const tableBody = document.getElementById("list-user");
  tableBody.innerHTML = "";

  users.forEach((u) => {
    const row = document.createElement("tr");

    row.innerHTML = `
      <td>${u.id}</td>
      <td>${u.name}</td>
      <td>${u.email}</td>
      <td>${u.gender || ""}</td>
      <td>${u.enabled ? "Đang hoạt động" : "Đã khóa"}</td>
      <td></td>
    `;

    const actionCell = row.querySelector("td:last-child");
    const actionDiv = document.createElement("div");
    actionDiv.className = "d-flex flex-column gap-2";

    const lockBtn = document.createElement("button");
    lockBtn.className = u.enabled
      ? "btn btn-danger btn-sm"
      : "btn btn-success btn-sm";
    lockBtn.textContent = u.enabled ? "Khóa" : "Mở khóa";

    const reasonInput = document.createElement("input");
    reasonInput.type = "text";
    reasonInput.className = "form-control form-control-sm";
    reasonInput.placeholder = "Nhập lý do khóa...";
    reasonInput.style.display = "none";

    reasonInput.addEventListener("blur", () => {
      setTimeout(() => {
        reasonInput.value = "";
        reasonInput.style.display = "none";
      }, 200);
    });

    lockBtn.addEventListener("click", async () => {
      if (u.enabled) {
        // Nếu đang hoạt động thì cần nhập lý do khóa
        if (reasonInput.style.display === "none") {
          reasonInput.style.display = "block";
          reasonInput.focus();
          return;
        }

        const reason = reasonInput.value.trim();
        if (!reason) {
          alert("Vui lòng nhập lý do khóa.");
          return;
        }

        if (!confirm(`Khóa người dùng?\nLý do: ${reason}`)) return;

        await toggleUser(u.id, reason);
      } else {
        // Nếu đang bị khóa thì chỉ cần xác nhận
        if (!confirm("Mở khóa người dùng này?")) return;

        await toggleUser(u.id, null);
      }

      renderUser(); // refresh
    });

    actionDiv.appendChild(lockBtn);
    actionDiv.appendChild(reasonInput);
    actionCell.appendChild(actionDiv);
    tableBody.appendChild(row);
  });
};

const toggleUser = async (userId, reason) => {
  try {
    await fetch(`http://localhost:8080/api/toggle-user/${userId}`, {
      method: "PUT",
      headers: {
        Authorization: "Bearer " + token,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ reason: reason }),
    });
  } catch (err) {
    alert("Lỗi khi thay đổi trạng thái người dùng");
  }
};

document.getElementById("search-keyword").addEventListener("input", (e) => {
  const keyword = e.target.value.trim().toLowerCase();

  const filtered = allUser.filter((u) =>
    u.name.toLowerCase().includes(keyword)
  );

  renderUserTable(filtered);
});

window.addEventListener("DOMContentLoaded", renderUser);
