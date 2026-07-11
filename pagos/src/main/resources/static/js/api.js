document.addEventListener('DOMContentLoaded', () => {
    APP.updateNavbar();
    APP.showPage('home');
});

const APP = {
    handleError: (err, context) => {
        console.error(`Error en ${context}:`, err);
        alert(`Error al ${context}. Revisa la conexión o intenta más tarde.`);
    },

    getUser: () => {
        const session = sessionStorage.getItem('user');
        return session ? JSON.parse(session) : null;
    },

    updateNavbar: () => {
        const navSection = document.getElementById('nav-auth-section');
        const user = APP.getUser();

        if (user) {
            let adminLink = user.role === 'ADMIN' ? `<a class="nav-link text-warning" href="#" onclick="APP.showPage('admin')">Admin</a>` : '';
            navSection.innerHTML = `
                ${adminLink}
                <a class="nav-link text-info" href="#" onclick="APP.showPage('profile')">Hola, ${user.nombre || 'Usuario'}</a>
                <a class="nav-link text-danger" href="#" onclick="logout()">Salir</a>
            `;
        } else {
            navSection.innerHTML = `
                <a class="nav-link" href="#" onclick="APP.showPage('login')">Iniciar Sesión</a>
            `;
        }
    },

    showPage: (pageId) => {
        document.querySelectorAll('.page').forEach(p => {
            p.classList.remove('active');
            p.style.display = 'none';
        });

        const target = document.getElementById(pageId);
        if (target) {
            target.classList.add('active');
            target.style.display = 'block';

            const routers = {
                'home': cargarProductos,
                'cart': cargarCarrito,
                'admin': cargarReportes,
                'profile': cargarPerfil
            };
            if (routers[pageId]) routers[pageId]();
        }
    }
};

// --- FASE 1: Catálogo ---
async function cargarProductos() {
    const container = document.getElementById('product-list');
    try {
        const res = await fetch('/api/products');
        if (!res.ok) throw new Error('Servidor no responde');
        const productos = await res.json();

        container.innerHTML = productos.map(p => `
            <div class="col-md-4 mb-4">
                <div class="card p-3 h-100">
                    <h5 class="card-title">${p.nombre}</h5>
                    <p class="card-text text-primary fw-bold">$${p.precio}</p>
                    <button class="btn btn-outline-primary mt-auto" onclick="agregarAlCarrito(${p.id})">Añadir al Carrito</button>
                </div>
            </div>
        `).join('');
    } catch (e) { APP.handleError(e, 'cargar catálogo'); }
}

// --- FASE 3: Carrito ---
async function agregarAlCarrito(productId) {
    const user = APP.getUser();
    if (!user) return alert("Debes iniciar sesión para comprar");

    try {
        const res = await fetch(`/api/cart/${user.id}/add?productId=${productId}&quantity=1`, { method: 'POST' });
        if (!res.ok) throw new Error();
        alert("Producto añadido");
    } catch (e) { APP.handleError(e, 'añadir producto'); }
}

async function cargarCarrito() {
    const user = APP.getUser();
    const container = document.getElementById('cart-items');

    if (!user) {
        container.innerHTML = '<tr><td colspan="3">Inicia sesión para ver tu carrito</td></tr>';
        document.getElementById('total-amount').innerText = "0.00";
        return;
    }

    try {
        const res = await fetch(`/api/cart/${user.id}/get`);
        if (!res.ok) throw new Error();
        const cart = await res.json();

        container.innerHTML = cart.items && cart.items.length > 0
            ? cart.items.map(i => `<tr><td>${i.productName}</td><td>${i.quantity}</td><td>$${i.unitPrice}</td></tr>`).join('')
            : '<tr><td colspan="3" class="text-center">Carrito vacío</td></tr>';

        document.getElementById('total-amount').innerText = cart.totalAmount ? cart.totalAmount.toFixed(2) : "0.00";
    } catch (e) { APP.handleError(e, 'cargar carrito'); }
}

// --- FASE 7: Checkout ---
async function procesarPago() {
    const user = APP.getUser();
    if (!user) return APP.showPage('login');

    const method = document.getElementById('payment-method').value;
    try {
        const res = await fetch(`/api/checkout/${user.id}/pay?method=${method}`, { method: 'POST' });
        if (res.ok) {
            alert("¡Pago exitoso!");
            APP.showPage('home');
        } else {
            const err = await res.json();
            alert(`Error: ${err.message || 'Fondos insuficientes o stock agotado'}`);
        }
    } catch (e) { APP.handleError(e, 'procesar pago'); }
}

// --- FASE 6: Autenticación ---
async function autenticar() {
    const email = document.getElementById('login-email').value;
    const pass = document.getElementById('login-pass').value;
    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, pass })
        });
        if (!res.ok) throw new Error();

        const userData = await res.json();
        sessionStorage.setItem('user', JSON.stringify(userData));
        APP.updateNavbar();
        APP.showPage('home');
    } catch (e) { alert("Credenciales incorrectas"); }
}

async function registrar() {
    const usuario = {
        nombre: document.getElementById('reg-name').value,
        email: document.getElementById('reg-email').value,
        pass: document.getElementById('reg-pass').value
    };
    try {
        const res = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuario)
        });
        if (!res.ok) throw new Error();
        alert("Registro exitoso. Inicia sesión.");
        APP.showPage('login');
    } catch (e) { APP.handleError(e, 'registrar'); }
}

function logout() {
    sessionStorage.removeItem('user');
    APP.updateNavbar();
    APP.showPage('home');
}

// --- FASE 4 & 5: Admin y Perfil ---
async function cargarReportes() {
    try {
        const res = await fetch('/api/admin/reports');
        if (res.status === 403) return document.getElementById('report-list').innerHTML = '<div class="alert alert-danger">Acceso Denegado</div>';

        const data = await res.json();
        document.getElementById('report-list').innerHTML = data.map(t => `
            <div class="p-2 border-bottom">Ticket: <b>${t.numeroTicket}</b> | Total: $${t.montoTotal} | Estado: ${t.estado}</div>
        `).join('');
    } catch (e) { APP.handleError(e, 'cargar reportes'); }
}

async function cargarPerfil() {
    const user = APP.getUser();
    if (!user) return APP.showPage('login');

    try {
        document.getElementById('user-info').innerHTML = `<p><b>Nombre:</b> ${user.nombre}</p><p><b>Email:</b> ${user.email}</p>`;

        const res = await fetch(`/api/user/${user.id}/orders`);
        const orders = await res.json();
        document.getElementById('user-orders').innerHTML = orders.length > 0
            ? orders.map(o => `<div class="p-2 border-bottom">Ticket #${o.numeroTicket} - $${o.montoTotal}</div>`).join('')
            : '<p>Sin compras.</p>';
    } catch (e) { APP.handleError(e, 'cargar perfil'); }
}