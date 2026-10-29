/**
 * TRAFFICIQ — Interactive Leaflet.js Map Engine with Satellite & Street View Modals
 */

const MapEngine = {
    CONGESTION_COLORS: {
        LOW: "#10B981",       // Subtle Green
        MODERATE: "#F59E0B",  // Amber / Yellow
        HEAVY: "#F97316",     // Orange
        SEVERE: "#EF4444"     // Crimson Red
    },

    createMap(containerId, options = {}) {
        const center = options.center || CONFIG.MAP_CENTER;
        const zoom = options.zoom || CONFIG.MAP_DEFAULT_ZOOM;

        const map = L.map(containerId, {
            center: center,
            zoom: zoom,
            zoomControl: true
        });

        // 1. OpenStreetMap Standard Layer
        const osmLayer = L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        });

        // 2. Esri World Imagery (Satellite View)
        const satelliteLayer = L.tileLayer("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}", {
            maxZoom: 19,
            attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'
        });

        // 3. CartoDB Dark Matter Layer
        const darkLayer = L.tileLayer("https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png", {
            maxZoom: 19,
            attribution: '&copy; CartoDB'
        });

        // Add default base layer based on current theme
        const currentTheme = document.documentElement.getAttribute("data-theme") || "dark";
        if (currentTheme === "dark") {
            darkLayer.addTo(map);
        } else {
            osmLayer.addTo(map);
        }

        // Layer Control
        const baseMaps = {
            "Street Map (OSM)": osmLayer,
            "Satellite View (Esri)": satelliteLayer,
            "Night Mode (CartoDB)": darkLayer
        };

        L.control.layers(baseMaps, null, { position: "topright" }).addTo(map);

        return map;
    },

    renderRoadSegments(map, roads, onRoadClick = null) {
        const layerGroup = L.layerGroup().addTo(map);

        roads.forEach(road => {
            const startLat = road.start_lat || (road.startLatitude ? parseFloat(road.startLatitude) : null);
            const startLng = road.start_lng || (road.startLongitude ? parseFloat(road.startLongitude) : null);
            const endLat = road.end_lat || (road.endLatitude ? parseFloat(road.endLatitude) : null);
            const endLng = road.end_lng || (road.endLongitude ? parseFloat(road.endLongitude) : null);

            if (!startLat || !startLng || !endLat || !endLng) return;

            const color = this.CONGESTION_COLORS[road.congestion_level] || this.CONGESTION_COLORS.LOW;

            const polyline = L.polyline([
                [startLat, startLng],
                [endLat, endLng]
            ], {
                color: color,
                weight: 6,
                opacity: 0.85,
                lineCap: "round"
            }).addTo(layerGroup);

            // Midpoint for Street View launch
            const midLat = (startLat + endLat) / 2;
            const midLng = (startLng + endLng) / 2;

            const popupContent = `
                <div style="font-family: var(--font-family); min-width: 200px;">
                    <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 4px;">${road.road_name}</div>
                    <div style="font-size: 0.8rem; color: #64748B; margin-bottom: 8px;">Code: ${road.road_code} | Type: ${road.road_type}</div>
                    <div style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 4px;">
                        <span>Congestion:</span>
                        <strong style="color: ${color};">${road.congestion_level}</strong>
                    </div>
                    <div style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 4px;">
                        <span>Current Speed:</span>
                        <strong>${road.current_speed_kmh} km/h</strong>
                    </div>
                    <div style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 12px;">
                        <span>Est. Duration:</span>
                        <strong>${road.estimated_travel_time_min} mins</strong>
                    </div>
                    <button onclick="MapEngine.openStreetView('${road.road_name.replace(/'/g, "\\'")}', ${midLat}, ${midLng})" 
                            class="btn-secondary" style="width: 100%; padding: 6px; font-size: 0.8rem; justify-content: center;">
                        <i class="fas fa-street-view"></i> Street View Preview
                    </button>
                </div>
            `;

            polyline.bindPopup(popupContent);

            polyline.on("mouseover", function () {
                this.setStyle({ weight: 9, opacity: 1 });
            });
            polyline.on("mouseout", function () {
                this.setStyle({ weight: 6, opacity: 0.85 });
            });

            if (onRoadClick) {
                polyline.on("click", () => onRoadClick(road));
            }
        });

        return layerGroup;
    },

    renderIncidents(map, incidents) {
        const layerGroup = L.layerGroup().addTo(map);

        incidents.forEach(inc => {
            if (!inc.latitude || !inc.longitude) return;

            const isCritical = inc.severity === "CRITICAL" || inc.severity === "HIGH";
            const iconColor = isCritical ? "#EF4444" : "#F59E0B";

            const customMarker = L.divIcon({
                className: "custom-incident-marker",
                html: `
                    <div style="
                        width: 28px; height: 28px;
                        background: ${iconColor};
                        color: white;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 0 10px ${iconColor};
                        border: 2px solid #FFFFFF;
                        cursor: pointer;
                    ">
                        <i class="fas fa-exclamation-triangle" style="font-size: 13px;"></i>
                    </div>
                `,
                iconSize: [28, 28],
                iconAnchor: [14, 14]
            });

            const marker = L.marker([inc.latitude, inc.longitude], { icon: customMarker }).addTo(layerGroup);

            const popup = `
                <div style="font-family: var(--font-family); min-width: 220px;">
                    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 6px;">
                        <span class="badge-status ${isCritical ? 'badge-severe' : 'badge-moderate'}">${inc.severity}</span>
                        <span style="font-size: 0.75rem; color: #64748B;">${inc.status}</span>
                    </div>
                    <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 4px;">${inc.incident_type}</div>
                    <div style="font-size: 0.85rem; margin-bottom: 8px;">${inc.description}</div>
                    <div style="font-size: 0.8rem; color: #64748B;">Impact Delay: +${inc.impact_delay_minutes} mins</div>
                </div>
            `;
            marker.bindPopup(popup);
        });

        return layerGroup;
    },

    openStreetView(roadName, lat, lng) {
        let modal = document.getElementById("streetview-modal");
        if (!modal) {
            modal = document.createElement("div");
            modal.id = "streetview-modal";
            modal.className = "modal-overlay";
            modal.innerHTML = `
                <div class="modal-content" style="max-width: 650px;">
                    <div class="modal-header">
                        <div class="modal-title" id="streetview-title">Corridor Street View</div>
                        <button class="modal-close-btn" onclick="document.getElementById('streetview-modal').classList.remove('active')">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="streetview-preview-card">
                            <img id="streetview-image" src="" alt="Corridor View" class="streetview-img">
                            <div class="streetview-badge" id="streetview-badge">Hyderabad Metropolitan Corridor</div>
                        </div>
                        <div style="margin-bottom: 16px; font-size: 0.9rem; color: var(--text-secondary);" id="streetview-desc">
                            Real-world junction imagery along this corridor.
                        </div>
                        <div style="display: flex; justify-content: flex-end; gap: 10px;">
                            <a id="streetview-external-link" href="#" target="_blank" rel="noopener noreferrer" class="btn-primary">
                                <i class="fas fa-external-link-alt"></i> Open Full Google Street View
                            </a>
                            <button onclick="document.getElementById('streetview-modal').classList.remove('active')" class="btn-secondary">Close</button>
                        </div>
                    </div>
                </div>
            `;
            document.body.appendChild(modal);
        }

        // Realistic imagery map for Hyderabad corridors
        const sampleImages = [
            "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80", // Cyber Towers / Modern highway
            "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?auto=format&fit=crop&w=800&q=80", // Urban junction
            "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80", // Bus corridor
            "https://images.unsplash.com/photo-1519003722824-194d4455a60c?auto=format&fit=crop&w=800&q=80"  // Expressway
        ];
        const randomImg = sampleImages[Math.floor(Math.random() * sampleImages.length)];

        document.getElementById("streetview-title").innerText = roadName;
        document.getElementById("streetview-image").src = randomImg;
        document.getElementById("streetview-badge").innerText = `Coordinates: ${lat.toFixed(4)}, ${lng.toFixed(4)}`;
        document.getElementById("streetview-desc").innerText = `Visual preview for ${roadName}. You can inspect junction lanes, signage, flyovers, and elevated corridors.`;
        document.getElementById("streetview-external-link").href = `https://www.google.com/maps/@?api=1&map_action=pano&viewpoint=${lat},${lng}`;

        modal.classList.add("active");
    }
};
