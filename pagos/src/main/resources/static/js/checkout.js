// checkout.js

Session.requireLogin();
renderTopbar("carrito");

const checkoutContent = document.getElementById("checkoutContent");
const alertBox = document.getElementById("alertBox");
const user = Session.getUser();

// Coincide con los tipos que reconoce PaymentProcessorFactory en el backend
const PAYMENT_METHODS = [
  { code: "CARD", label: "💳 Tarjeta" },
  { code: "CASH", label: "💵 Efectivo" },
  { code: "QR", label: "📱 QR" },
];

let selectedMethod = "CARD";
let currentCart = null;

async function loadSummary() {
  checkoutContent.innerHTML = '<p class="text-muted">Cargando resumen...</p>';
  try {
    currentCart = await Api.get(`/cart/${user.id}/get`);

    if (!currentCart.items || currentCart.items.length === 0) {
      checkoutContent.innerHTML = `
        <div class="empty-state">
          <p>No tienes productos en el carrito.</p>
          <a href="productos.html" class="btn btn--primary" style="width:auto; display:inline-flex; margin-top:12px;">
            Ir al catálogo
          </a>
        </div>
      `;
      return;
    }

    renderCheckoutForm();
  } catch (err) {
    checkoutContent.innerHTML = "";
    alertBox.innerHTML = `<div class="alert alert--error">${err.message}</div>`;
  }
}

function renderCheckoutForm() {
  const itemsHtml = currentCart.items
    .map(
      (item) => `
      <div class="summary-row">
        <span>${item.productName} × ${item.quantity}</span>
        <span>${formatMoney(item.subtotal)}</span>
      </div>
    `
    )
    .join("");

  const methodsHtml = PAYMENT_METHODS.map(
    (m) => `
      <div class="pay-method ${m.code === selectedMethod ? "selected" : ""}" data-method="${m.code}">
        ${m.label}
      </div>
    `
  ).join("");

  checkoutContent.innerHTML = `
    <div class="summary-box">
      ${itemsHtml}
      <div class="summary-row summary-row--total">
        <span>Subtotal</span>
        <span>${formatMoney(currentCart.totalAmount)}</span>
      </div>
      <p class="text-muted" style="font-size:0.8rem; margin-top:4px;">
        El total final (con 18% de impuesto) se confirma al procesar el pago.
      </p>
    </div>

    <h3 style="margin: 24px 0 12px;">Método de pago</h3>
    <div class="pay-methods" id="payMethods">${methodsHtml}</div>

    <button class="btn btn--accent btn--block" id="payBtn">
      Pagar ${formatMoney(currentCart.totalAmount)}
    </button>
  `;

  document.querySelectorAll(".pay-method").forEach((el) => {
    el.addEventListener("click", () => {
      selectedMethod = el.dataset.method;
      document.querySelectorAll(".pay-method").forEach((x) => x.classList.remove("selected"));
      el.classList.add("selected");
    });
  });

  document.getElementById("payBtn").addEventListener("click", processPayment);
}

async function processPayment() {
  const payBtn = document.getElementById("payBtn");
  payBtn.disabled = true;
  payBtn.textContent = "Procesando pago...";
  alertBox.innerHTML = "";

  try {
    const transaction = await Api.post(`/checkout/${user.id}/pay`, {
      params: { method: selectedMethod },
    });

    if (transaction.estado === "PAGADO") {
      showToast("¡Pago exitoso! Tu compra fue registrada.", "success");
      renderReceipt(transaction);
    } else {
      showToast("El pago no pudo completarse. Intenta de nuevo.", "error");
      payBtn.disabled = false;
      payBtn.textContent = `Pagar ${formatMoney(currentCart.totalAmount)}`;
    }
  } catch (err) {
    showToast(err.message || "Ocurrió un error al procesar el pago", "error");
    payBtn.disabled = false;
    payBtn.textContent = `Pagar ${formatMoney(currentCart.totalAmount)}`;
  }
}

function renderReceipt(t) {
  const detallesHtml = (t.detalles || [])
    .map(
      (d) => `
      <div class="receipt__row">
        <span>${d.product ? d.product.nombre : "Producto"} × ${d.cantidad}</span>
        <span>${formatMoney(d.subtotal)}</span>
      </div>
    `
    )
    .join("");

  checkoutContent.innerHTML = `
    <div class="receipt">
      <div style="text-align:center; margin-bottom:12px;">
        <div style="font-size:1.4rem;">✅</div>
        <div style="font-weight:700;">Pago confirmado</div>
        <div class="text-muted" style="font-size:0.78rem;">Ticket ${t.numeroTicket || t.id}</div>
      </div>
      <div class="receipt__divider"></div>
      ${detallesHtml}
      <div class="receipt__divider"></div>
      <div class="receipt__row"><span>Subtotal</span><span>${formatMoney(t.montoSubtotal)}</span></div>
      <div class="receipt__row"><span>Impuestos (18%)</span><span>${formatMoney(t.impuestos)}</span></div>
      <div class="receipt__row" style="font-weight:800; font-size:0.95rem;">
        <span>Total</span><span>${formatMoney(t.montoTotal)}</span>
      </div>
      <div class="receipt__divider"></div>
      <div class="receipt__row"><span>Método de pago</span><span>${t.metodoPago}</span></div>
      <div class="receipt__row"><span>Fecha</span><span>${formatDate(t.fecha)}</span></div>
    </div>

    <a href="mis-compras.html" class="btn btn--primary mt-16">Ver mis compras</a>
    <a href="productos.html" class="btn btn--outline mt-16" style="margin-top:8px;">Seguir comprando</a>
  `;
}

loadSummary();
