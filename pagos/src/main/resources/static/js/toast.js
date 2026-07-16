// toast.js — notificaciones flotantes reutilizables en todo el frontend

function showToast(message, type = "success", durationMs = 3200) {
  let container = document.getElementById("toastContainer");
  if (!container) {
    container = document.createElement("div");
    container.id = "toastContainer";
    Object.assign(container.style, {
      position: "fixed",
      top: "16px",
      right: "16px",
      zIndex: "9999",
      display: "flex",
      flexDirection: "column",
      gap: "8px",
      maxWidth: "320px",
    });
    document.body.appendChild(container);
  }

  const toast = document.createElement("div");
  toast.className = `alert alert--${type}`;
  Object.assign(toast.style, {
    boxShadow: "0 8px 24px rgba(0,0,0,0.18)",
    opacity: "0",
    transform: "translateY(-8px)",
    transition: "opacity 0.25s ease, transform 0.25s ease",
  });
  toast.textContent = message;
  container.appendChild(toast);

  requestAnimationFrame(() => {
    toast.style.opacity = "1";
    toast.style.transform = "translateY(0)";
  });

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateY(-8px)";
    setTimeout(() => toast.remove(), 250);
  }, durationMs);
}

function estadoBadge(estado) {
  const map = { PAGADO: "pagado", PENDIENTE: "pendiente", FALLIDO: "fallido" };
  const cls = map[estado] || "pendiente";
  return `<span class="badge badge--${cls}">${estado || "—"}</span>`;
}

function formatMoney(value) {
  const n = Number(value || 0);
  return `S/ ${n.toFixed(2)}`;
}

function formatDate(iso) {
  if (!iso) return "—";
  const d = new Date(iso);
  return d.toLocaleString("es-PE", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}
