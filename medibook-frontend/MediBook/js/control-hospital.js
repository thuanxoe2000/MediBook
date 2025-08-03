const token = localStorage.getItem("token");
if (!token) window.location.href = "/MediBook/login.html";

document.getElementById("add-hospital").addEventListener("click", () => {
  window.location.href = "create-hospital.html";
});

const searchInput = document.getElementById("searchInput");
const hospitalList = document.getElementById("hospitalList");

window.onload = loadAllHospitals;

searchInput.addEventListener("input", () => {
  clearTimeout(window.debounceTimer);
  window.debounceTimer = setTimeout(async () => {
    const keyword = searchInput.value.trim();
    if (!keyword) return loadAllHospitals();
    try {
      const res = await fetch(
        `http://localhost:8080/hospital/search?keyword=${encodeURIComponent(
          keyword
        )}`
      );
      renderHospitals(await res.json());
    } catch (err) {
      alert(err.message);
    }
  }, 500);
});

async function loadAllHospitals() {
  try {
    const res = await fetch("http://localhost:8080/hospital/list");
    renderHospitals(await res.json());
  } catch (err) {
    alert("Lỗi tải danh sách bệnh viện: " + err.message);
  }
}

function renderHospitals(data) {
  hospitalList.innerHTML = "";
  data.forEach(renderSingleHospital);
}

function renderSingleHospital(hospital) {
  const box = document.createElement("div");
  box.className = "hospital-box";

  const name = document.createElement("h5");
  name.textContent = hospital.name;

  const id = document.createElement("p");
  id.textContent = `Mã bệnh viện: ${hospital.id}`;

  const btnGroup = createHospitalButtonGroup(hospital);

  const departmentDiv = document.createElement("div");
  departmentDiv.className = "mt-3 d-none";
  renderDepartmentList(hospital.responses, departmentDiv);

  const doctorDiv = document.createElement("div");
  doctorDiv.className = "mt-3 d-none";

  btnGroup.querySelector(".toggle-dpm").onclick = () => {
    doctorDiv.classList.add("d-none");
    departmentDiv.classList.toggle("d-none");
  };

  btnGroup.querySelector(".toggle-doctor").onclick = () => {
    departmentDiv.classList.add("d-none");
    renderDoctorList(hospital.id, doctorDiv);
    doctorDiv.classList.toggle("d-none");
  };

  box.append(name, id, btnGroup, departmentDiv, doctorDiv);
  hospitalList.appendChild(box);
}

function createHospitalButtonGroup(hospital) {
  const group = document.createElement("div");
  group.className = "d-flex mb-2";

  const addDpm = document.createElement("button");
  addDpm.className = "btn btn-secondary me-2";
  addDpm.textContent = "Thêm khoa";
  addDpm.onclick = () =>
    (window.location.href = `create-dpm.html?id=${hospital.id}`);

  const listDoc = document.createElement("button");
  listDoc.className = "btn btn-outline-primary me-2 toggle-doctor";
  listDoc.textContent = "Danh sách bác sĩ";

  const addDoc = document.createElement("button");
  addDoc.className = "btn btn-primary me-2";
  addDoc.textContent = "Thêm bác sĩ";
  addDoc.onclick = () =>
    (window.location.href = `create-doctor.html?id=${hospital.id}`);

  const toggleDpm = document.createElement("button");
  toggleDpm.className = "btn btn-outline-info toggle-dpm";
  toggleDpm.textContent = "Xem thêm";

  group.append(addDpm, listDoc, addDoc, toggleDpm);
  return group;
}

function renderDepartmentList(departments, container) {
  departments.forEach((dept) =>
    container.appendChild(createDepartmentItem(dept))
  );
}

function createDepartmentItem(dept) {
  const box = document.createElement("div");
  box.className = "border p-2 rounded mb-2";

  const deptName = document.createElement("h6");
  deptName.innerHTML = `<strong>Tên khoa:</strong> <span class="editable">${dept.name}</span>`;

  const head = document.createElement("p");
  head.innerHTML = `<strong>Trưởng khoa:</strong> ${
    dept.head?.name || "Chưa có"
  }`;

  const desc = document.createElement("p");
  desc.innerHTML = `<strong>Mô tả:</strong> <span class="editable">${dept.description}</span>`;

  const cost = document.createElement("p");
  cost.innerHTML = `<strong>Chi phí:</strong> <span class="editable">${dept.cost}</span> ¥`;

  const editBtn = document.createElement("button");
  editBtn.className = "btn btn-sm btn-warning";
  editBtn.textContent = "Chỉnh sửa";

  const saveBtn = document.createElement("button");
  saveBtn.className = "btn btn-sm btn-success me-2 d-none";
  saveBtn.textContent = "Lưu";

  const cancelBtn = document.createElement("button");
  cancelBtn.className = "btn btn-sm btn-secondary d-none";
  cancelBtn.textContent = "Huỷ";

  const original = {
    name: dept.name,
    description: dept.description,
    cost: dept.cost,
  };

  editBtn.onclick = () => {
    box.querySelectorAll(".editable").forEach((el) => {
      el.contentEditable = "true";
      el.classList.add("bg-white", "border", "rounded", "px-1");
    });
    editBtn.classList.add("d-none");
    saveBtn.classList.remove("d-none");
    cancelBtn.classList.remove("d-none");
  };

  cancelBtn.onclick = () => {
    deptName.querySelector(".editable").textContent = original.name;
    desc.querySelector(".editable").textContent = original.description;
    cost.querySelector(".editable").textContent = original.cost;
    box.querySelectorAll(".editable").forEach((el) => {
      el.contentEditable = "false";
      el.classList.remove("bg-white", "border", "rounded", "px-1");
    });
    saveBtn.classList.add("d-none");
    cancelBtn.classList.add("d-none");
    editBtn.classList.remove("d-none");
  };

  saveBtn.onclick = async () => {
    const updated = {
      name: deptName.querySelector(".editable").textContent.trim(),
      description: desc.querySelector(".editable").textContent.trim(),
      cost: parseInt(cost.querySelector(".editable").textContent.trim()),
    };

    try {
      const res = await fetch(
        `http://localhost:8080/hospital/${dept.id}/updateDpm`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify(updated),
        }
      );
      if (!res.ok) throw new Error(await res.text());
      alert("Cập nhật thành công!");
      Object.assign(original, updated);
      cancelBtn.click();
    } catch (err) {
      alert("Lỗi: " + err.message);
    }
  };

  box.append(deptName, head, desc, cost, editBtn, saveBtn, cancelBtn);
  return box;
}

async function renderDoctorList(hospitalId, container) {
  container.innerHTML = "";
  try {
    const res = await fetch(
      `http://localhost:8080/hospital/${hospitalId}/doctors`,
      {
        headers: { Authorization: "Bearer " + token },
      }
    );
    const doctors = await res.json();

    if (doctors.length === 0) {
      container.innerHTML = `<p class="text-muted">Chưa có bác sĩ nào.</p>`;
      return;
    }

    doctors.forEach((doctor) =>
      container.appendChild(createDoctorLine(doctor, hospitalId))
    );
  } catch (err) {
    alert("Lỗi tải danh sách bác sĩ: " + err.message);
  }
}

function createDoctorLine(doctor, hospitalId) {
  const line = document.createElement("div");
  line.className = "d-flex align-items-center justify-content-between mb-2";

  const info = document.createElement("span");
  info.textContent = `${doctor.name} – ${
    doctor.departmentName || "Chưa phân khoa"
  }`;

  const changeBtn = document.createElement("button");
  changeBtn.className = "btn btn-sm btn-outline-warning";
  changeBtn.textContent = "Chuyển khoa";
  changeBtn.onclick = async () => {
    changeBtn.classList.add("d-none");
    const select = document.createElement("select");
    select.className = "form-select form-select-sm me-2";

    const confirmBtn = document.createElement("button");
    confirmBtn.className = "btn btn-sm btn-success";
    confirmBtn.textContent = "Xác nhận";

    line.append(select, confirmBtn);

    const handleOutsideClick = (e) => {
      if (!line.contains(e.target)) {
        select.remove();
        confirmBtn.remove();
        changeBtn.classList.remove("d-none");
        document.removeEventListener("click", handleOutsideClick);
      }
    };
    setTimeout(() => {
      document.addEventListener("click", handleOutsideClick);
    }, 0);

    try {
      const res = await fetch(
        `http://localhost:8080/hospital/${hospitalId}/departments`,
        {
          headers: { Authorization: "Bearer " + token },
        }
      );
      const departments = await res.json();
      departments.forEach((d) => {
        const option = document.createElement("option");
        option.value = d.id;
        option.textContent = d.name;
        if (d.name === doctor.departmentName) option.disabled = true;
        select.appendChild(option);
      });
    } catch (err) {
      alert("Lỗi tải danh sách khoa: " + err.message);
      return;
    }

    confirmBtn.onclick = async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/hospital/${doctor.id}/change-department`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + token,
            },
            body: JSON.stringify({ departmentId: select.value }),
          }
        );
        if (!res.ok) throw new Error(await res.text());
        alert("Chuyển khoa thành công!");
        location.reload();
      } catch (err) {
        alert("Lỗi: " + err.message);
      }
    };
  };

  line.append(info, changeBtn);
  return line;
}
