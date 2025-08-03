const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

const payload = parseJwt(token);
if (!payload) {
  localStorage.clear();
  window.location.href = "login.html";
}

window.addEventListener("DOMContentLoaded", () => {
  renderName();
  userBooking();

  // Toggle các phần div theo nút
  document
    .getElementById("pending")
    .addEventListener("click", () => toggleDiv("pendingDiv"));
  document
    .getElementById("confirm")
    .addEventListener("click", () => toggleDiv("confirmDiv"));
  document
    .getElementById("cancel")
    .addEventListener("click", () => toggleDiv("cancelDiv"));
  document
    .getElementById("finish")
    .addEventListener("click", () => toggleDiv("finished"));
});

function toggleDiv(id) {
  const allDivs = ["pendingDiv", "confirmDiv", "cancelDiv", "finished"];

  allDivs.forEach((divId) => {
    const div = document.getElementById(divId);
    if (divId === id) {
      const isHidden = window.getComputedStyle(div).display === "none";
      div.style.display = isHidden ? "block" : "none";
    } else {
      div.style.display = "none";
    }
  });
}

function renderName() {
  const name = document.getElementById("username");
  name.textContent = payload.name;
}

async function userBooking() {
  try {
    console.log("Gọi API user-booking...");
    const res = await fetch("http://localhost:8080/timeslots/user-booking", {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
    const data = await res.json();
    console.log("Dữ liệu nhận được:", data);

    // Xoá nội dung cũ và reset thành div.row
    ["pendingDiv", "confirmDiv", "cancelDiv", "finished"].forEach((id) => {
      const container = document.getElementById(id);
      container.innerHTML = "";
      container.className = "row"; // gán class row cho layout 3 cột
    });

    data.forEach((booking) => {
      const col = document.createElement("div");
      col.className = "col-md-4 mb-3";

      const borderClass =
        {
          PENDING: "border-warning",
          CONFIRMED: "border-primary",
          CANCELED: "border-danger",
          FINISHED: "border-success",
        }[booking.status] || "border-secondary";

      const formatDate = (d) => new Date(d).toISOString().slice(0, 10);
      const formatTime = (t) => {
        const [hour, minute] = t.split(":");
        return `${hour}h${minute}`;
      };

      const div = document.createElement("div");
      div.className = `border ${borderClass} p-2 rounded bg-light h-100`;

      div.innerHTML = `
        <div><strong>Mã đặt lịch:</strong> ${booking.code}</div>
        <div><strong>Ngày khám:</strong> ${formatDate(booking.date)}</div>
        <div><strong>Giờ khám:</strong> ${formatTime(booking.time)}</div>
        <div><strong>Trạng thái:</strong> ${booking.statusMessage}</div>
        <div><strong>Ghi chú:</strong> ${booking.note || "Không có"}</div>
        <div><small class="text-muted">Tạo lúc: ${formatDate(
          booking.createdAt
        )}</small></div>
      `;

      col.appendChild(div);

      const containerId = {
        PENDING: "pendingDiv",
        CONFIRMED: "confirmDiv",
        CANCELED: "cancelDiv",
        FINISHED: "finished",
      }[booking.status];

      if (containerId) {
        document.getElementById(containerId).appendChild(col);
      } else {
        console.warn("Status không hợp lệ:", booking.status);
      }
    });
  } catch (err) {
    alert("Lỗi khi lấy lịch: " + err.message);
  }
}

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
