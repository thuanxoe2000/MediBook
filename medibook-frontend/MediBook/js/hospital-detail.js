const token = window.localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

const urlParams = new URLSearchParams(window.location.search);
const hospitalId = urlParams.get("id");

let selectedDepartmentId = null;
let selectedDate = null;
let selectedTime = null;
let selectedDepartmentName = null;
let selectedTimeText = null;
let bookingData = null;

window.onload = async () => {
  try {
    const res = await fetch(
      `http://localhost:8080/hospital/hospital-detail?id=${hospitalId}`,
      {
        method: "GET",
        headers: { Authorization: "Bearer " + token },
      }
    );
    const data = await res.json();

    const container = document.getElementById("hospital-detail");
    const card = document.createElement("div");
    card.className = "card shadow p-4";

    const row = document.createElement("div");
    row.className = "row";

    const imgCol = document.createElement("div");
    imgCol.className = "col-md-4 text-center mb-3";
    const img = document.createElement("img");
    img.src = data.imageUrl;
    img.className = "img-fluid rounded";
    img.style.maxHeight = "200px";
    imgCol.appendChild(img);

    const infoCol = document.createElement("div");
    infoCol.className = "col-md-8";

    const name = document.createElement("h2");
    name.textContent = data.name;

    const address = document.createElement("p");
    address.textContent = data.address;

    const workingTime = document.createElement("p");
    workingTime.textContent = data.startTime + " - " + data.endTime;

    const cost = document.createElement("p");
    cost.textContent = data.cost + " VND";

    const email = document.createElement("p");
    email.textContent = data.email;

    const phoneNumber = document.createElement("p");
    phoneNumber.textContent = data.phoneNumber;

    const description = document.createElement("p");
    description.textContent = data.description;

    infoCol.append(
      name,
      address,
      workingTime,
      cost,
      email,
      phoneNumber,
      description
    );
    row.append(imgCol, infoCol);
    card.appendChild(row);

    const dpmTitle = document.createElement("h5");
    dpmTitle.className = "mt-4 mb-3";
    dpmTitle.textContent = "Danh sách khoa";

    const listGroup = document.createElement("div");
    listGroup.className = "list-group";

    (data.responses || []).forEach((e) => {
      const item = document.createElement("div");
      item.className = "list-group-item list-group-item-action";

      const dpmName = document.createElement("h6");
      dpmName.className = "fw-bold";
      dpmName.textContent = e.name;

      const dpmCost = document.createElement("p");
      dpmCost.innerHTML = `<strong>Chi phí:</strong> ${e.cost} VND`;

      const dpmDescription = document.createElement("small");
      dpmDescription.textContent = e.description;

      const bookBtn = document.createElement("button");
      bookBtn.className = "btn btn-sm btn-primary mt-2";
      bookBtn.textContent = "Đặt lịch";
      bookBtn.addEventListener("click", () => {
        selectedDepartmentId = e.id;
        selectedDepartmentName = e.name;

        const oldCalendar = document.getElementById("calendar");
        if (oldCalendar) oldCalendar.remove();

        const cal = document.createElement("div");
        cal.id = "calendar";
        cal.style.maxWidth = "300px";
        cal.style.fontSize = "12px";
        bookBtn.parentElement.appendChild(cal);

        createCalendar(e.id);
      });

      item.append(dpmName, dpmCost, dpmDescription, bookBtn);
      listGroup.appendChild(item);
    });

    card.append(dpmTitle, listGroup);
    container.appendChild(card);
  } catch (err) {
    alert(err.message);
  }
};

const createCalendar = async function (departmentId) {
  const calendarEl = document.getElementById("calendar");

  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",
    fixedWeekCount: false,
    dayCellDidMount: function (info) {
      const today = new Date();
      const cellDate = info.date;
      if (cellDate < today.setHours(0, 0, 0, 0)) {
        info.el.classList.add("fc-day-past-disabled");
      }
    },
    dateClick: function (info) {
      const date = new Date(info.dateStr);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      if (date < today) return;

      selectedDate = info.dateStr;

      const oldContainer = document.getElementById("time-slot-container");
      if (oldContainer) oldContainer.remove();

      fetch(
        `http://localhost:8080/timeslots/${departmentId}/get-slots?date=${selectedDate}`,
        {
          headers: { Authorization: "Bearer " + token },
        }
      )
        .then((res) => res.json())
        .then((data) => {
          const timeSlotContainer = document.createElement("div");
          timeSlotContainer.id = "time-slot-container";
          timeSlotContainer.className = "mt-2";
          calendarEl.appendChild(timeSlotContainer);
          renderHours(data);
        });
    },
  });

  calendar.render();

  setTimeout(() => {
    document.addEventListener("click", function handler(e) {
      if (calendarEl && !calendarEl.contains(e.target)) {
        calendarEl.remove();
        document.removeEventListener("click", handler);
      }
    });
  }, 0);
};

const renderHours = function (data) {
  const container = document.getElementById("time-slot-container");
  container.innerHTML = "";

  if (data.length === 0) {
    container.innerHTML = "<p>Không có khung giờ khả dụng.</p>";
    return;
  }

  const label = document.createElement("label");
  label.textContent = "Chọn khung giờ:";

  const select = document.createElement("select");
  select.className = "form-select mt-1";
  select.style.maxWidth = "200px";

  data.forEach((slot) => {
    const option = document.createElement("option");
    option.value = slot.id;
    option.textContent = slot.time;
    select.appendChild(option);
  });

  if (data.length > 0) {
    selectedTime = data[0].id;
    selectedTimeText = data[0].time;
  }

  select.addEventListener("change", () => {
    selectedTime = select.value;
    selectedTimeText = select.options[select.selectedIndex].text || "Không rõ";
  });

  const noteLabel = document.createElement("label");
  noteLabel.className = "mt-2";
  noteLabel.textContent = "Ghi chú (nếu có):";

  const noteInput = document.createElement("input");
  noteInput.type = "text";
  noteInput.className = "form-control mb-2";
  noteInput.id = "note-input";

  const confirmBtn = document.createElement("button");
  confirmBtn.className = "btn btn-success btn-sm mt-2";
  confirmBtn.textContent = "Xác nhận đặt lịch";

  confirmBtn.addEventListener("click", () => {
    const selectedOption = select.options[select.selectedIndex];
    selectedTime = selectedOption.value;
    selectedTimeText = selectedOption.text;

    if (!selectedDepartmentId || !selectedTime) {
      alert("Vui lòng chọn đầy đủ khoa, ngày và giờ.");
      return;
    }

    const note = noteInput.value || "";

    document.querySelector(".modal-body").innerHTML = `
      <p><strong>Khoa:</strong> ${selectedDepartmentName}</p>
      <p><strong>Ngày:</strong> ${selectedDate}</p>
      <p><strong>Giờ:</strong> ${selectedTimeText}</p>
      <p><strong>Ghi chú:</strong> ${note || "(Không có)"}</p>
    `;

    bookingData = {
      timeSlotId: selectedTime,
      dpmId: selectedDepartmentId,
      note: note,
    };

    console.log(bookingData);

    const modal = new bootstrap.Modal(document.getElementById("confirmModal"));
    modal.show();
  });

  document.getElementById("confirmYes").addEventListener("click", () => {
    if (!bookingData) return;

    booking(bookingData);

    const modal = bootstrap.Modal.getInstance(
      document.getElementById("confirmModal")
    );
    modal.hide();

    bookingData = null;
  });

  container.append(label, select, noteLabel, noteInput, confirmBtn);
};

const booking = function (data) {
  fetch("http://localhost:8080/timeslots/book", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + token,
    },
    body: JSON.stringify(data),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Đặt lịch thất bại");
    })
    .then(() => {
      alert("Đặt lịch thành công!");
      document.getElementById("calendar")?.remove();
      document.getElementById("time-slot-container")?.remove();
    })
    .catch((err) => alert(err.message));
};
