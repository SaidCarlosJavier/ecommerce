// register.js — lógica de register.html

if (Session.isLoggedIn()) {
  window.location.href = "productos.html";
}

const registerForm = document.getElementById("registerForm");
const alertBox = document.getElementById("alertBox");
const submitBtn = document.getElementById("submitBtn");

function showAlert(message, type = "error") {
  alertBox.innerHTML = `<div class="alert alert--${type}">${message}</div>`;
}

registerForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  alertBox.innerHTML = "";

  const nombre = document.getElementById("nombre").value.trim();
  const email = document.getElementById("email").value.trim();
  const pass = document.getElementById("pass").value;

  submitBtn.disabled = true;
  submitBtn.textContent = "Creando cuenta...";

  try {
    await Api.post("/auth/register", {
      body: { nombre, email, pass },
      auth: false,
    });

    showAlert("Cuenta creada. Redirigiendo a inicio de sesión...", "success");
    setTimeout(() => (window.location.href = "login.html"), 1200);
  } catch (err) {
    showAlert(err.message || "No se pudo crear la cuenta");
    submitBtn.disabled = false;
    submitBtn.textContent = "Crear cuenta";
  }
});
