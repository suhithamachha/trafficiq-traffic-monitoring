/**
 * TRAFFICIQ — Authentication & Role-Based UI State Management
 */

const Auth = {
    getToken() {
        return localStorage.getItem(CONFIG.TOKEN_STORAGE_KEY);
    },

    getUser() {
        const userStr = localStorage.getItem(CONFIG.USER_STORAGE_KEY);
        try {
            return userStr ? JSON.parse(userStr) : null;
        } catch (e) {
            return null;
        }
    },

    isLoggedIn() {
        return !!this.getToken() && !!this.getUser();
    },

    isAdmin() {
        const user = this.getUser();
        return user && (user.role === "ADMIN" || user.role === "ROLE_ADMIN");
    },

    async login(email, password) {
        try {
            const res = await fetch(`${CONFIG.API_BASE}/auth/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();
            if (data.success && data.data && data.data.token) {
                localStorage.setItem(CONFIG.TOKEN_STORAGE_KEY, data.data.token);
                localStorage.setItem(CONFIG.USER_STORAGE_KEY, JSON.stringify(data.data));
                return { success: true, user: data.data };
            } else {
                return { success: false, message: data.message || "Invalid credentials." };
            }
        } catch (err) {
            console.warn("Backend unavailable, evaluating demo credentials locally:", err);
            // Resilient Demo Auth for academic presentation
            if (email.toLowerCase() === "admin@trafficiq.com" && password === "Admin@123") {
                const demoAdmin = {
                    userId: 1,
                    name: "System Administrator",
                    email: "admin@trafficiq.com",
                    role: "ADMIN",
                    token: "demo_admin_jwt_token_academic"
                };
                localStorage.setItem(CONFIG.TOKEN_STORAGE_KEY, demoAdmin.token);
                localStorage.setItem(CONFIG.USER_STORAGE_KEY, JSON.stringify(demoAdmin));
                return { success: true, user: demoAdmin };
            } else if (email.toLowerCase() === "user@trafficiq.com" && password === "User@123") {
                const demoUser = {
                    userId: 2,
                    name: "Demo Commuter",
                    email: "user@trafficiq.com",
                    role: "USER",
                    token: "demo_user_jwt_token_academic"
                };
                localStorage.setItem(CONFIG.TOKEN_STORAGE_KEY, demoUser.token);
                localStorage.setItem(CONFIG.USER_STORAGE_KEY, JSON.stringify(demoUser));
                return { success: true, user: demoUser };
            }
            return { success: false, message: "Invalid credentials. Use Admin@123 or User@123." };
        }
    },

    logout() {
        localStorage.removeItem(CONFIG.TOKEN_STORAGE_KEY);
        localStorage.removeItem(CONFIG.USER_STORAGE_KEY);
        window.location.href = "login.html";
    },

    getAuthHeaders() {
        const token = this.getToken();
        return token ? { "Authorization": `Bearer ${token}` } : {};
    },

    updateNavbarUI() {
        const container = document.getElementById("navbar-auth-section");
        if (!container) return;

        if (this.isLoggedIn()) {
            const user = this.getUser();
            const roleBadgeClass = this.isAdmin() ? "badge-severe" : "badge-low";
            container.innerHTML = `
                <div style="display: flex; align-items: center; gap: 12px;">
                    <div style="text-align: right; line-height: 1.2;">
                        <div style="font-size: 0.88rem; font-weight: 600;">${user.name || user.email}</div>
                        <span class="badge-status ${roleBadgeClass}" style="font-size: 0.7rem; padding: 1px 8px;">${user.role}</span>
                    </div>
                    <button onclick="Auth.logout()" class="btn-secondary" style="padding: 6px 12px; font-size: 0.85rem;" title="Sign Out">
                        <i class="fas fa-sign-out-alt"></i>
                    </button>
                </div>
            `;
        } else {
            container.innerHTML = `
                <a href="login.html" class="btn-primary" style="padding: 6px 14px; font-size: 0.88rem;">
                    <i class="fas fa-user-circle"></i> Sign In
                </a>
            `;
        }
    }
};

document.addEventListener("DOMContentLoaded", () => {
    Auth.updateNavbarUI();
});
