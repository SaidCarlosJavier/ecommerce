// carrito.js

Session.requireLogin();
renderTopbar("carrito");

const cartContent = document.getElementById("cartContent");
const alertBox = document.getElementById("alertBox");
const user = Session.getUser();

async function loadCart() {
  cartContent.innerHTML = '<p class="text-muted">Cargando carrito...</p>';
  try {
    const cart = await Api.get(`/cart/${user.id}/get`);

    if (!cart.items || cart.items.length === 0) {
      cartContent.innerHTML = `
        <div class="empty-state">
          <p>Tu carrito está vacío.</p>
          <a href="productos.html" class="btn btn--primary" style="width:auto; display:inline-flex; margin-top:12px;">
            Ver catálogo
          </a>
        </div>
      `;
      return;
    }

    const itemsHtml = cart.items
      .map(
        (item) => `
        <div class="cart-item">
          <div>
            <div class="cart-item__name">${item.productName}</div>
            <div class="cart-item__meta">${formatMoney(item.unitPrice)} × ${item.quantity}</div>
          </div>
          <div class="cart-item__name">${formatMoney(item.subtotal)}</div>
        </div>
      `
      )
      .join("");

    cartContent.innerHTML = `
      <div class="summary-box">
        ${itemsHtml}
        <div class="summary-row summary-row--total">
          <span>Subtotal</span>
          <span>${formatMoney(cart.totalAmount)}</span>
        </div>
        <p class="text-muted" style="font-size:0.8rem; margin-top:6px;">
          Los impuestos (18%) se calculan al confirmar el pago.
        </p>
        <button class="btn btn--primary mt-16" id="goCheckoutBtn">Ir a pagar</button>
      </div>
    `;

    document.getElementById("goCheckoutBtn").addEventListener("click", () => {
      window.location.href = "checkout.html";
    });
  } catch (err) {
    cartContent.innerHTML = "";
    alertBox.innerHTML = `<div class="alert alert--error">${err.message}</div>`;
  }
}

loadCart();
