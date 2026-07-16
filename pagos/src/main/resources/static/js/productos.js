// productos.js

Session.requireLogin();
renderTopbar("productos");

const grid = document.getElementById("productGrid");
const alertBox = document.getElementById("alertBox");

async function loadProducts() {
  grid.innerHTML = '<p class="text-muted">Cargando productos...</p>';
  try {
    const products = await Api.get("/products", { auth: false });

    if (!products || products.length === 0) {
      grid.innerHTML = `<div class="empty-state">No hay productos disponibles por ahora.</div>`;
      return;
    }

    grid.innerHTML = products.map(renderCard).join("");

    grid.querySelectorAll("[data-add]").forEach((btn) => {
      btn.addEventListener("click", () => addToCart(btn.dataset.add));
    });
  } catch (err) {
    alertBox.innerHTML = `<div class="alert alert--error">${err.message}</div>`;
  }
}

function renderCard(p) {
  const img =
    p.imagenUrl && p.imagenUrl.trim() !== ""
      ? p.imagenUrl
      : `https://placehold.co/300x225?text=${encodeURIComponent(p.name || "Producto")}`;

  const sinStock = !p.stock || p.stock <= 0;

  return `
    <div class="product-card">
      <img class="product-card__image" src="${img}" alt="${p.name}"
           onerror="this.src='https://placehold.co/300x225?text=Sin+imagen'" />
      <div class="product-card__body">
        <div class="product-card__name">${p.name}</div>
        <div class="product-card__desc">${p.descripcion || ""}</div>
        <div class="product-card__price">${formatMoney(p.price)}</div>
        <div class="product-card__stock">${sinStock ? "Sin stock" : `${p.stock} disponibles`}</div>
        <div class="product-card__footer">
          <input type="number" class="qty-input" min="1" max="${p.stock || 1}"
                 value="1" id="qty-${p.id}" ${sinStock ? "disabled" : ""} />
          <button class="btn btn--accent btn--sm" data-add="${p.id}" ${sinStock ? "disabled" : ""}>
            Agregar
          </button>
        </div>
      </div>
    </div>
  `;
}

async function addToCart(productId) {
  const qtyInput = document.getElementById(`qty-${productId}`);
  const quantity = Math.max(1, parseInt(qtyInput.value, 10) || 1);
  const user = Session.getUser();

  try {
    await Api.post(`/cart/${user.id}/add`, { params: { productId, quantity } });
    showToast("Producto agregado al carrito", "success");
  } catch (err) {
    showToast(err.message || "No se pudo agregar el producto", "error");
  }
}

loadProducts();
