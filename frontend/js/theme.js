/**
 * TRAFFICIQ — Theme Switcher (Dark / Light Theme Persistence)
 */

(function () {
    const THEME_KEY = "trafficiq_theme";

    // Immediate execution to prevent flash-of-unstyled-theme
    const savedTheme = localStorage.getItem(THEME_KEY) || "dark";
    document.documentElement.setAttribute("data-theme", savedTheme);

    window.toggleTheme = function () {
        const currentTheme = document.documentElement.getAttribute("data-theme") || "dark";
        const newTheme = currentTheme === "dark" ? "light" : "dark";
        document.documentElement.setAttribute("data-theme", newTheme);
        localStorage.setItem(THEME_KEY, newTheme);
        updateThemeIcons(newTheme);

        // Notify charts or maps if registered
        window.dispatchEvent(new CustomEvent("trafficiq-theme-changed", { detail: { theme: newTheme } }));
    };

    function updateThemeIcons(theme) {
        const toggleButtons = document.querySelectorAll(".theme-toggle-btn");
        toggleButtons.forEach(btn => {
            const icon = btn.querySelector("i");
            if (icon) {
                if (theme === "light") {
                    icon.className = "fas fa-moon";
                    btn.setAttribute("title", "Switch to Dark Mode");
                } else {
                    icon.className = "fas fa-sun";
                    btn.setAttribute("title", "Switch to Light Mode");
                }
            }
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        const current = document.documentElement.getAttribute("data-theme") || "dark";
        updateThemeIcons(current);

        const buttons = document.querySelectorAll(".theme-toggle-btn");
        buttons.forEach(btn => {
            btn.addEventListener("click", window.toggleTheme);
        });
    });
})();
