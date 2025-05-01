const adminEmail = "admin@academia.com";
const adminPassword = "admin123";

document.getElementById("admin-login-form").addEventListener("submit", function (e) {
  e.preventDefault();

  const email = document.getElementById("admin-email").value;
  const password = document.getElementById("admin-password").value;

  if (email === adminEmail && password === adminPassword) {
    localStorage.setItem("adminAutenticado", "true");
    window.location.href = "admin_panel.html";
  } else {
    document.getElementById("login-error").innerText = "Correo o contraseña incorrectos";
  }
});
