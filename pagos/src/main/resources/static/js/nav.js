// nav.js — topbar compartido, se adapta según el rol del usuario logueado

function renderTopbar(active) {
  const user = Session.getUser();
  const isAdmin = Session.isAdmin();
  const el = document.getElementById("topbar");
  if (!el) return;

  const links = isAdmin
    ? [
        { href: "admin-dashboard.html", label: "Reportes", key: "admin-dashboard" },
        { href: "admin-productos.html", label: "Productos", key: "admin-productos" },
      ]
    : [
        { href: "productos.html", label: "Catálogo", key: "productos" },
        { href: "carrito.html", label: "Carrito", key: "carrito" },
        { href: "mis-compras.html", label: "Mis compras", key: "mis-compras" },
      ];

  const navHtml = links
    .map(
      (l) =>
        `<a href="${l.href}" style="${l.key === active ? "color:#fff;font-weight:700;" : ""}">${l.label}</a>`
    )
    .join("");

  el.innerHTML = `
    <a class="topbar__brand" href="${isAdmin ? "admin-dashboard.html" : "productos.html"}">🛒 Mercado Fresco</a>
    <div class="topbar__nav">
      ${navHtml}
      <span class="topbar__user">${user ? user.nombre : ""} ${isAdmin ? '<span class="badge badge--admin">ADMIN</span>' : ""}</span>
      <button class="btn btn--outline btn--sm" id="logoutBtn">Salir</button>
    </div>
  `;

  document.getElementById("logoutBtn").addEventListener("click", () => {
    Session.clear();
    window.location.href = "login.html";
  });
}
