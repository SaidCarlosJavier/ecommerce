// mis-compras.js

Session.requireLogin();
renderTopbar("mis-compras");

const ordersList = document.getElementById("ordersList");
const alertBox = document.getElementById("alertBox");
const user = Session.getUser();

async function loadOrders() {
  ordersList.innerHTML = '<p class="text-muted">Cargando tus compras...</p>';
  try {
    const orders = await Api.get(`/user/${user.id}/orders`);

    if (!orders || orders.length === 0) {
      ordersList.innerHTML = `
        <div class="empty-state">
          <p>Todavía no tienes compras registradas.</p>
          <a href="productos.html" class="btn btn--primary" style="width:auto; display:inline-flex; margin-top:12px;">
            Ir al catálogo
          </a>
        </div>
      `;
      return;
    }

    ordersList.innerHTML = orders.map(renderOrderCard).join("");

    ordersList.querySelectorAll("[data-toggle]").forEach((btn) => {
      btn.addEventListener("click", () => {
        const panel = document.getElementById(`detalle-${btn.dataset.toggle}`);
        panel.classList.toggle("hidden");
        btn.textContent = panel.classList.contains("hidden") ? "Ver detalle" : "Ocultar detalle";
      });
    });
  } catch (err) {
    ordersList.innerHTML = "";
    alertBox.innerHTML = `<div class="alert alert--error">${err.message}</div>`;
  }
}

function renderOrderCard(t) {
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

  return `
    <div class="summary-box mt-16">
      <div class="flex-between">
        <div>
          <div style="font-weight:700;">Ticket ${t.numeroTicket || t.id}</div>
          <div class="text-muted" style="font-size:0.82rem;">${formatDate(t.fecha)} · ${t.metodoPago || "—"}</div>
        </div>
        <div style="text-align:right;">
          ${estadoBadge(t.estado)}
          <div style="font-weight:700; margin-top:4px;">${formatMoney(t.montoTotal)}</div>
        </div>
      </div>
      <button class="btn btn--outline btn--sm mt-16" data-toggle="${t.id}">Ver detalle</button>
      <div id="detalle-${t.id}" class="hidden mt-16">
        <div class="receipt__divider"></div>
        ${detallesHtml}
      </div>
    </div>
  `;
}

loadOrders();
