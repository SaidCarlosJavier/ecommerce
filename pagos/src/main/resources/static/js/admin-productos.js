// admin-productos.js

Session.requireAdmin();
renderTopbar("admin-productos");

const productsTableWrap = document.getElementById("productsTableWrap");
const alertBox = document.getElementById("alertBox");
const productModal = document.getElementById("productModal");
const productForm = document.getElementById("productForm");
const modalTitle = document.getElementById("modalTitle");

let allProducts = [];

async function loadProducts() {
  productsTableWrap.innerHTML = '<p class="text-muted">Cargando productos...</p>';
  try {
    allProducts = await Api.get("/admin/products");
    renderTable();
  } catch (err) {
    productsTableWrap.innerHTML = "";
    alertBox.innerHTML = `<div class="alert alert--error">${err.message}</div>`;
  }
}

function renderTable() {
  if (allProducts.length === 0) {
    productsTableWrap.innerHTML = `<div class="empty-state">No hay productos registrados todavía.</div>`;
    return;
  }

  const rows = allProducts
    .map(
      (p) => `
      <tr>
        <td>${p.nombre}</td>
        <td>${formatMoney(p.precio)}</td>
        <td>${p.stock}</td>
        <td>
          <span class="badge ${p.activo ? "badge--pagado" : "badge--fallido"}">
            ${p.activo ? "Activo" : "Inactivo"}
          </span>
        </td>
        <td style="display:flex; gap:6px;">
          <button class="btn btn--outline btn--sm" data-edit="${p.id}">Editar</button>
          <button class="btn ${p.activo ? "btn--danger" : "btn--accent"} btn--sm" data-toggle="${p.id}">
            ${p.activo ? "Desactivar" : "Activar"}
          </button>
        </td>
      </tr>
    `
    )
    .join("");

  productsTableWrap.innerHTML = `
    <table class="data-table">
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Precio</th>
          <th>Stock</th>
          <th>Estado</th>
          <th></th>
        </tr>
      </thead>
      <tbody>${rows}</tbody>
    </table>
  `;

  productsTableWrap.querySelectorAll("[data-edit]").forEach((btn) => {
    btn.addEventListener("click", () => openEdit(btn.dataset.edit));
  });
  productsTableWrap.querySelectorAll("[data-toggle]").forEach((btn) => {
    btn.addEventListener("click", () => toggleActivo(btn.dataset.toggle));
  });
}

function openModal() {
  productModal.classList.remove("hidden");
}
function closeModal() {
  productModal.classList.add("hidden");
  productForm.reset();
  document.getElementById("productId").value = "";
}

function openNew() {
  modalTitle.textContent = "Nuevo producto";
  productForm.reset();
  document.getElementById("productId").value = "";
  openModal();
}

function openEdit(id) {
  const p = allProducts.find((x) => String(x.id) === String(id));
  if (!p) return;

  modalTitle.textContent = "Editar producto";
  document.getElementById("productId").value = p.id;
  document.getElementById("nombre").value = p.nombre || "";
  document.getElementById("descripcion").value = p.descripcion || "";
  document.getElementById("precio").value = p.precio || 0;
  document.getElementById("stock").value = p.stock || 0;
  document.getElementById("imagenUrl").value = p.imagenUrl || "";
  openModal();
}

async function toggleActivo(id) {
  try {
    await Api.patch(`/admin/products/${id}/toggle`);
    showToast("Estado del producto actualizado", "success");
    loadProducts();
  } catch (err) {
    showToast(err.message || "No se pudo actualizar el estado", "error");
  }
}

productForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const saveBtn = document.getElementById("saveProductBtn");
  saveBtn.disabled = true;
  saveBtn.textContent = "Guardando...";

  const id = document.getElementById("productId").value;
  const payload = {
    nombre: document.getElementById("nombre").value.trim(),
    descripcion: document.getElementById("descripcion").value.trim(),
    precio: parseFloat(document.getElementById("precio").value),
    stock: parseInt(document.getElementById("stock").value, 10),
    imagenUrl: document.getElementById("imagenUrl").value.trim(),
  };

  try {
    if (id) {
      await Api.put(`/admin/products/${id}`, { body: payload });
      showToast("Producto actualizado", "success");
    } else {
      payload.activo = true;
      await Api.post("/admin/products", { body: payload });
      showToast("Producto creado", "success");
    }
    closeModal();
    loadProducts();
  } catch (err) {
    showToast(err.message || "No se pudo guardar el producto", "error");
  } finally {
    saveBtn.disabled = false;
    saveBtn.textContent = "Guardar";
  }
});

document.getElementById("newProductBtn").addEventListener("click", openNew);
document.getElementById("closeProductModalBtn").addEventListener("click", closeModal);
productModal.addEventListener("click", (e) => {
  if (e.target === productModal) closeModal();
});

loadProducts();
