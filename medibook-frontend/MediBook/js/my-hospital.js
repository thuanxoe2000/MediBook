const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

window.addEventListener("DOMContentLoaded", loadHospitalInfo);

async function loadHospitalInfo() {
  const hospital = document.getElementById("hospital");
  hospital.innerHTML = "";

  try {
    const res = await fetch("http://localhost:8080/hospital/my-hospital", {
      headers: {
        Authorization: "Bearer " + token,
      },
    });

    if (!res.ok) throw new Error("Không thể lấy thông tin bệnh viện");

    const data = await res.json();

    renderHospitalInfo(hospital, data.name);
    data.responses.forEach((dept) => {
      renderDepartment(hospital, dept);
    });
  } catch (err) {
    alert(err.message);
  }
}

function renderHospitalInfo(container, name) {
  const title = document.createElement("h4");
  title.textContent = `🏥 Bệnh viện: ${name}`;
  title.classList.add("mb-4", "text-primary", "fw-bold");
  container.appendChild(title);
}

function renderDepartment(container, dept) {
  const deptContainer = document.createElement("div");
  deptContainer.className =
    "mb-4 p-3 border rounded bg-white position-relative";

  const deptName = document.createElement("h5");
  deptName.textContent = `Khoa: ${dept.name}`;
  deptName.classList.add("text-success");
  deptContainer.appendChild(deptName);

  const grouped = {};
  dept.bookingResponses?.forEach((b) => {
    if (!grouped[b.status]) {
      grouped[b.status] = {
        statusMessage: b.statusMessage,
        bookings: [],
      };
    }
    grouped[b.status].bookings.push(b);
  });

  const { divMap } = createBookingGroup(deptContainer, grouped);

  Object.entries(grouped).forEach(([status, group]) => {
    group.bookings.forEach((b) => {
      renderBooking(b, divMap[status]);
    });
  });

  container.appendChild(deptContainer);

  document.addEventListener("click", (e) => {
    if (!deptContainer.contains(e.target)) {
      Object.values(divMap).forEach((div) => (div.style.display = "none"));
    }
  });
}

function createBookingGroup(parentContainer) {
  const buttonGroup = document.createElement("div");
  buttonGroup.className = "d-grid gap-2 d-md-flex mb-3";

  const divMap = {};

  const buttons = [
    { id: "PENDING", label: "Đang chờ", class: "btn-outline-warning" },
    { id: "CONFIRMED", label: "Đã xác nhận", class: "btn-outline-primary" },
    { id: "FINISHED", label: "Đã hoàn thành", class: "btn-outline-success" },
    { id: "CANCELED", label: "Đã bị huỷ", class: "btn-outline-danger" },
  ];

  parentContainer.appendChild(buttonGroup);

  buttons.forEach(({ id, label, class: btnClass }) => {
    const btn = document.createElement("button");
    btn.textContent = label;
    btn.className = `btn btn-sm ${btnClass}`;
    buttonGroup.appendChild(btn);

    const contentDiv = document.createElement("div");
    contentDiv.style.display = "none";
    contentDiv.className = "mt-2 row";
    divMap[id] = contentDiv;

    btn.addEventListener("click", (e) => {
      e.stopPropagation();
      toggleDiv(divMap, id);
    });

    parentContainer.appendChild(contentDiv);
  });

  return { divMap };
}

function toggleDiv(divMap, currentId) {
  Object.entries(divMap).forEach(([id, div]) => {
    div.style.display =
      id === currentId
        ? div.style.display === "none"
          ? "block"
          : "none"
        : "none";
  });
}

function renderBooking(booking, container) {
  if (!container) return;

  const formatDate = (d) => new Date(d).toISOString().slice(0, 10);
  const formatTime = (t) => {
    const [hour, minute] = t.split(":");
    return `${hour}h${minute}`;
  };

  const col = document.createElement("div");
  col.className = "col-md-4 mb-3";

  const borderClass =
    {
      PENDING: "border-warning",
      CONFIRMED: "border-primary",
      FINISHED: "border-success",
      CANCELED: "border-danger",
    }[booking.status] || "border-secondary";

  const bookingBox = document.createElement("div");
  bookingBox.className = `border ${borderClass} p-2 rounded bg-light position-relative`;

  bookingBox.innerHTML = `
    <div><strong>Mã đặt lịch:</strong> ${booking.code}</div>
    <div><strong>Ngày khám:</strong> ${formatDate(booking.date)}</div>
    <div><strong>Giờ khám:</strong> ${formatTime(booking.time)}</div>
    <div><strong>Trạng thái:</strong> ${booking.statusMessage}</div>
    <div><strong>Ghi chú:</strong> ${booking.note || "Không có"}</div>
    <div><small class="text-muted">Tạo lúc: ${formatDate(
      booking.createdAt
    )}</small></div>
  `;

  if (booking.status === "PENDING") {
    const actionGroup = document.createElement("div");
    actionGroup.className = "mt-2 d-flex gap-2 flex-wrap";

    const confirmBtn = document.createElement("button");
    confirmBtn.className = "btn btn-sm btn-success";
    confirmBtn.textContent = "Xác nhận";
    confirmBtn.onclick = () => confirmBooking(booking.id, true);

    const cancelBtn = document.createElement("button");
    cancelBtn.className = "btn btn-sm btn-danger";
    cancelBtn.textContent = "Huỷ";

    const reasonWrapper = document.createElement("div");
    reasonWrapper.className =
      "mt-2 w-100 d-flex gap-2 align-items-start d-none";

    const reasonInput = document.createElement("input");
    reasonInput.type = "text";
    reasonInput.placeholder = "Nhập lý do huỷ...";
    reasonInput.className = "form-control form-control-sm";

    const submitCancelBtn = document.createElement("button");
    submitCancelBtn.className = "btn btn-sm btn-outline-danger";
    submitCancelBtn.textContent = "Xác nhận huỷ";
    submitCancelBtn.onclick = () => {
      const reason = reasonInput.value.trim();
      if (!reason) return alert("Vui lòng nhập lý do huỷ.");
      confirmBooking(booking.id, false, reason);
    };

    reasonWrapper.appendChild(reasonInput);
    reasonWrapper.appendChild(submitCancelBtn);

    cancelBtn.onclick = (e) => {
      e.stopPropagation();
      reasonWrapper.classList.remove("d-none");
      reasonInput.focus();
    };

    actionGroup.appendChild(confirmBtn);
    actionGroup.appendChild(cancelBtn);
    bookingBox.appendChild(actionGroup);
    bookingBox.appendChild(reasonWrapper);

    document.addEventListener("click", (e) => {
      if (!bookingBox.contains(e.target)) {
        reasonWrapper.classList.add("d-none");
      }
    });
  }

  col.appendChild(bookingBox);
  container.appendChild(col);
}

async function confirmBooking(id, isConfirm, note = null) {
  const options = {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    },
  };

  if (!isConfirm && note !== null) {
    options.body = JSON.stringify({ note });
  }

  const res = await fetch(
    `http://localhost:8080/timeslots/confirm/${id}?isConfirm=${isConfirm}`,
    options
  );

  if (!res.ok) {
    alert("Lỗi xử lý xác nhận/hủy");
    return;
  }

  alert(isConfirm ? "Xác nhận thành công" : "Huỷ thành công");
  loadHospitalInfo();
}
