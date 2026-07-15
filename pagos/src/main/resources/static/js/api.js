// api.js
// Capa única para hablar con el backend. Todas las páginas usan esto,
// nunca fetch() directo, para no repetir el manejo de token/errores.

const Session = {
  getToken() {
    return localStorage.getItem("token");
  },
  getUser() {
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) : null;
  },
  save(token, user) {
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(user));
  },
  clear() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },
  isLoggedIn() {
    return !!this.getToken();
  },
  isAdmin() {
    const user = this.getUser();
    return !!user && user.role === "ADMIN";
  },
  // Llama esto al inicio de páginas que requieren login
  requireLogin() {
    if (!this.isLoggedIn()) {
      window.location.href = "login.html";
    }
  },
  // Llama esto al inicio de páginas exclusivas de ADMIN
  requireAdmin() {
    if (!this.isLoggedIn()) {
      window.location.href = "login.html";
      return;
    }
    if (!this.isAdmin()) {
      window.location.href = "productos.html";
    }
  },
};

const Api = {
  async _request(method, path, { params, body, auth = true } = {}) {
    let url = `${API_BASE_URL}${path}`;

    if (params) {
      const query = new URLSearchParams(params).toString();
      url += `?${query}`;
    }

    const headers = { "Content-Type": "application/json" };
    if (auth && Session.getToken()) {
      headers["Authorization"] = `Bearer ${Session.getToken()}`;
    }

    let response;
    try {
      response = await fetch(url, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
      });
    } catch (networkError) {
      throw new ApiError(
        "No se pudo conectar con el servidor. ¿Está corriendo el backend?",
        0
      );
    }

    // Sesión inválida o expirada -> a login
    if (response.status === 401) {
      Session.clear();
      window.location.href = "login.html";
      throw new ApiError("Sesión expirada", 401);
    }

    if (response.status === 403) {
      throw new ApiError("No tienes permiso para hacer esto", 403);
    }

    let data = null;
    const text = await response.text();
    if (text) {
      try {
        data = JSON.parse(text);
      } catch {
        data = text;
      }
    }

    if (!response.ok) {
      const message =
        (data && data.message) || `Error ${response.status}`;
      throw new ApiError(message, response.status, data);
    }

    return data;
  },

  get(path, opts) {
    return this._request("GET", path, opts);
  },
  post(path, opts) {
    return this._request("POST", path, opts);
  },
  put(path, opts) {
    return this._request("PUT", path, opts);
  },
  patch(path, opts) {
    return this._request("PATCH", path, opts);
  },
  delete(path, opts) {
    return this._request("DELETE", path, opts);
  },
};

class ApiError extends Error {
  constructor(message, status, data) {
    super(message);
    this.status = status;
    this.data = data;
  }
}
