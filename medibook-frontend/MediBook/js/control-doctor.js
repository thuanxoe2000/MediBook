const token = window.localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

let allDoctor = [];

const renderDoctor = async function () {
  const listDoctor = document.getElementById("list-doctor");

  try {
    const res = await fetch("http://localhost:8080/api/list-doctor", {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
    const data = await res.json();
    allDoctors = data;

    renderDoctorTable(data);
  } catch (err) {
    alert("Lỗi khi tải danh sách bác sĩ: " + err.message);
  }
};

const renderDoctorTable = (doctors) => {
  const listDoctor = document.getElementById("list-doctor");
  listDoctor.innerHTML = "";

  doctors.forEach((doctor) => {
    const row = document.createElement("tr");

    const idCell = document.createElement("td");
    idCell.textContent = doctor.id;

    const nameCell = document.createElement("td");
    nameCell.textContent = doctor.name;

    const roleCell = document.createElement("td");
    roleCell.textContent = doctor.role;

    const hospitalCell = document.createElement("td");
    hospitalCell.textContent = doctor.hospitalName;

    const actionCell = document.createElement("td");
    const actionDiv = document.createElement("div");
    actionDiv.className = "d-flex flex-column gap-2";

    const lockBtn = document.createElement("button");
    lockBtn.className = doctor.enabled
      ? "btn btn-danger btn-sm"
      : "btn btn-success btn-sm";
    lockBtn.textContent = doctor.enabled ? "Khóa" : "Mở khóa";

    const reasonInput = document.createElement("input");
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
      if (doctor.enabled) {
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

        const confirmMsg = `Bạn có chắc muốn khóa tài khoản này?\nLý do: ${reason}`;
        if (!confirm(confirmMsg)) return;

        await toggleUser(doctor.id, reason);
      } else {
        if (!confirm("Bạn có chắc muốn mở khóa tài khoản này?")) return;
        await toggleUser(doctor.id, null);
      }

      renderDoctor(); // refresh lại
    });

    actionDiv.appendChild(lockBtn);
    actionDiv.appendChild(reasonInput);
    actionCell.appendChild(actionDiv);

    row.appendChild(idCell);
    row.appendChild(nameCell);
    row.appendChild(roleCell);
    row.appendChild(hospitalCell);
    row.appendChild(actionCell);

    listDoctor.appendChild(row);
  });
};

document.getElementById("search-keyword").addEventListener("input", (e) => {
  const keyword = e.target.value.trim().toLowerCase();

  const filtered = allDoctors.filter((d) => {
    return (
      d.name.toLowerCase().includes(keyword) ||
      d.hospitalName.toLowerCase().includes(keyword)
    );
  });

  renderDoctorTable(filtered);
});

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
    alert("Lỗi khi thay đổi trạng thái");
  }
};

window.addEventListener("DOMContentLoaded", renderDoctor);
