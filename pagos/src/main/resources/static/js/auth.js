// auth.js — lógica de login.html

// Si ya hay sesión activa, no tiene sentido ver el login de nuevo
if (Session.isLoggedIn()) {
  window.location.href = "productos.html";
}

const loginForm = document.getElementById("loginForm");
const alertBox = document.getElementById("alertBox");
const submitBtn = document.getElementById("submitBtn");

function showAlert(message, type = "error") {
  alertBox.innerHTML = `<div class="alert alert--${type}">${message}</div>`;
}

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  alertBox.innerHTML = "";

  const email = document.getElementById("email").value.trim();
  const pass = document.getElementById("pass").value;

  submitBtn.disabled = true;
  submitBtn.textContent = "Entrando...";

  try {
    const data = await Api.post("/auth/login", {
      body: { email, pass },
      auth: false,
    });

    Session.save(data.token, data.user);

    // Si es admin lo mandamos directo al panel, si es cliente al catálogo
    window.location.href = data.user.role === "ADMIN"
      ? "admin-dashboard.html"
      : "productos.html";
  } catch (err) {
    showAlert(err.message || "No se pudo iniciar sesión");
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = "Entrar";
  }
});
