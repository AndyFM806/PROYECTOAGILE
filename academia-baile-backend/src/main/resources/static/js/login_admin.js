const adminUsername = "admin123";
const adminPassword = "123456789";

document.getElementById("admin-login-form").addEventListener("submit", function (e) {
  e.preventDefault();

  const email = document.getElementById("admin-email").value.trim();
  const password = document.getElementById("admin-password").value.trim();

  if (email === adminUsername && password === adminPassword) {
    localStorage.setItem("adminAutenticado", "true");
    window.location.href = "admin_panel.html";
  } else {
    document.getElementById("login-error").innerText = "Usuario o contraseña incorrectos";
  }
});
