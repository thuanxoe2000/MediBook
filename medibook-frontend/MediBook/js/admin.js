const token = window.localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

document.getElementById("control-user").addEventListener("click", () => {
  window.location.href = "control-user.html";
});

document.getElementById("control-doctor").addEventListener("click", () => {
  window.location.href = "control-doctor.html";
});

document.getElementById("control-hospital").addEventListener("click", () => {
  window.location.href = "control-hospital.html";
});
