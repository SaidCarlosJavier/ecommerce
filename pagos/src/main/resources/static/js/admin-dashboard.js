// admin-dashboard.js

Session.requireAdmin();
renderTopbar("admin-dashboard");

const statsRow = document.getElementById("statsRow");
const ordersTableWrap = document.getElementById("ordersTableWrap");
const alertBox = document.getElementById("alertBox");
const detailModal = document.getElementById("detailModal");
const modalBody = document.getElementById("modalBody");

let allTransactions = [];

async function loadReports() {
  ordersTableWrap.innerHTML = '<p class="text-muted">Cargando compras...</p>';
  try {
    allTransactions = await Api.get("/admin/reports");
    renderStats();
    renderTable();
  } catch (err) {
    ordersTableWrap.innerHTML = "";
    alertBox.innerHTML = `<div class="alert alert--error">${err.message}</div>`;
  }
}

function renderStats() {
  const total = allTransactions.length;
  const pagadas = allTransactions.filter((t) => t.estado === "PAGADO");
  const montoTotal = pagadas.reduce((sum, t) => sum + Number(t.montoTotal || 0), 0);
  const fallidas = allTransactions.filter((t) => t.estado === "FALLIDO").length;
  const pendientes = allTransactions.filter((t) => t.estado === "PENDIENTE").length;

  const cards = [
    { label: "Total de órdenes", value: total },
    { label: "Ventas pagadas", value: pagadas.length },
    { label: "Ingresos confirmados", value: formatMoney(montoTotal) },
    { label: "Pendientes / fallidas", value: `${pendientes} / ${fallidas}` },
  ];

  statsRow.innerHTML = cards
    .map(
      (c) => `
      <div class="summary-box" style="flex:1; min-width:160px;">
        <div class="text-muted" style="font-size:0.78rem; text-transform:uppercase;">${c.label}</div>
        <div style="font-size:1.3rem; font-weight:800; color:var(--color-primary); margin-top:4px;">${c.value}</div>
      </div>
    `
    )
    .join("");
}

function renderTable() {
  if (allTransactions.length === 0) {
    ordersTableWrap.innerHTML = `<div class="empty-state">Todavía no hay compras registradas.</div>`;
    return;
  }

  // Más recientes primero
  const sorted = [...allTransactions].sort(
    (a, b) => new Date(b.fecha) - new Date(a.fecha)
  );

  const rows = sorted
    .map(
      (t) => `
      <tr>
        <td>${t.numeroTicket || t.id}</td>
        <td>${t.user ? t.user.nombre : "—"}</td>
        <td>${formatDate(t.fecha)}</td>
        <td>${t.metodoPago || "—"}</td>
        <td>${formatMoney(t.montoTotal)}</td>
        <td>${estadoBadge(t.estado)}</td>
        <td><button class="btn btn--outline btn--sm" data-detail="${t.id}">Ver</button></td>
      </tr>
    `
    )
    .join("");

  ordersTableWrap.innerHTML = `
    <table class="data-table">
      <thead>
        <tr>
          <th>Ticket</th>
          <th>Cliente</th>
          <th>Fecha</th>
          <th>Método</th>
          <th>Total</th>
          <th>Estado</th>
          <th></th>
        </tr>
      </thead>
      <tbody>${rows}</tbody>
    </table>
  `;

  ordersTableWrap.querySelectorAll("[data-detail]").forEach((btn) => {
    btn.addEventListener("click", () => openDetail(btn.dataset.detail));
  });
}

function openDetail(id) {
  const t = allTransactions.find((x) => String(x.id) === String(id));
  if (!t) return;

  const detallesHtml = (t.detalles || [])
    .map(
      (d) => `
      <div class="summary-row">
        <span>${d.product ? d.product.nombre : "Producto"} × ${d.cantidad}</span>
        <span>${formatMoney(d.subtotal)}</span>
      </div>
    `
    )
    .join("");

  modalBody.innerHTML = `
    <div class="receipt__row"><span>Cliente</span><span>${t.user ? t.user.nombre : "—"}</span></div>
    <div class="receipt__row"><span>Email</span><span>${t.user ? t.user.email : "—"}</span></div>
    <div class="receipt__row"><span>Estado</span><span>${estadoBadge(t.estado)}</span></div>
    <div class="receipt__divider"></div>
    ${detallesHtml || '<p class="text-muted">Sin detalle de productos.</p>'}
    <div class="receipt__divider"></div>
    <div class="receipt__row"><span>Subtotal</span><span>${formatMoney(t.montoSubtotal)}</span></div>
    <div class="receipt__row"><span>Impuestos</span><span>${formatMoney(t.impuestos)}</span></div>
    <div class="receipt__row" style="font-weight:800;"><span>Total</span><span>${formatMoney(t.montoTotal)}</span></div>
  `;

  detailModal.classList.remove("hidden");
}

document.getElementById("closeModalBtn").addEventListener("click", () => {
  detailModal.classList.add("hidden");
});
detailModal.addEventListener("click", (e) => {
  if (e.target === detailModal) detailModal.classList.add("hidden");
});

loadReports();
