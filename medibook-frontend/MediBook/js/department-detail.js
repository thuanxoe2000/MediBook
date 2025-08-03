const token = localStorage.getItem("token");
console.log(token);
if (!token) {
  window.location.href = "login.html";
}

const urlParams = new URLSearchParams(window.location.search);
const dpmId = urlParams.get("id");

window.addEventListener("DOMContentLoaded", () => {
  renderName();
  renderBooking();
});

const renderName = function () {
  const name = document.getElementById("username");
  const payload = JSON.parse(atob(token.split(".")[1]));
  name.textContent = payload.name;
};

document.getElementById("pending").addEventListener("click", () => {
  const div = document.getElementById("pendingDiv");
  div.style.display = div.style.display === "none" ? "block" : "none";
});

document.getElementById("confirm").addEventListener("click", () => {
  const div = document.getElementById("confirmDiv");
  div.style.display = div.style.display === "none" ? "block" : "none";
});

document.getElementById("cancel").addEventListener("click", () => {
  const div = document.getElementById("cancelDiv");
  div.style.display = div.style.display === "none" ? "block" : "none";
});

document.getElementById("finish").addEventListener("click", () => {
  const div = document.getElementById("finished");
  div.style.display = div.style.display === "none" ? "block" : "none";
});

const renderBooking = async function () {
  try {
    const res = await fetch(
      `http://localhost:8080/hospital/${dpmId}/department-detail`,
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
    const data = await res.json();

    const notifications = data.notifications || [];

    document.getElementById("pendingDiv").innerHTML = "";
    document.getElementById("confirmDiv").innerHTML = "";
    document.getElementById("cancelDiv").innerHTML = "";
    document.getElementById("finished").innerHTML = "";

    const formatDateTime = (str) => {
      const d = new Date(str);
      const yyyy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, "0"); // tháng từ 0–11
      const dd = String(d.getDate()).padStart(2, "0");
      const hh = String(d.getHours()).padStart(2, "0");
      const mi = String(d.getMinutes()).padStart(2, "0");
      return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
    };

    const renderItem = (targetId, n) => {
      const div = document.createElement("div");
      div.className = "border rounded p-2 mb-2 bg-light";
      div.innerHTML = `
        <p><strong>Ngày:</strong> ${n.date}</p>
        <p><strong>Giờ:</strong> ${n.time}</p>
        <p><strong>Ghi chú:</strong> ${n.userNote || "Không có"}</p>
        <p><strong>Gửi lúc:</strong> ${formatDateTime(n.sendAt)}</p>
      `;
      document.getElementById(targetId).appendChild(div);
    };

    notifications.forEach((n) => {
      switch (n.type) {
        case "PENDING":
          renderItem("pendingDiv", n);
          break;
        case "CONFIRMED":
          renderItem("confirmDiv", n);
          break;
        case "CANCELED":
          renderItem("cancelDiv", n);
          break;
        case "FINISHED":
          renderItem("finished", n);
          break;
      }
    });
  } catch (err) {
    alert(err.message);
  }
};
