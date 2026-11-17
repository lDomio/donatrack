(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        const mapEl = document.getElementById('donatrack-leaflet-map');
        if (!mapEl || typeof L === 'undefined') {
            return;
        }

        const modo = mapEl.dataset.modo || 'seguimiento';
        if (modo === 'publico') {
            inicializarMapaPublico(mapEl);
        } else {
            inicializarMapaSeguimiento(mapEl);
        }
    });

    function crearIconoHtml(claseColor, iconoFa, conPulso) {
        const pulsoHtml = conPulso ? '<span class="dt-pin-camion-pulso"></span>' : '';
        return L.divIcon({
            className: 'dt-leaflet-pin-wrapper',
            html: pulsoHtml + '<div class="dt-pin-circulo ' + claseColor + '"><i class="fas ' + iconoFa + '"></i></div>',
            iconSize: conPulso ? [44, 44] : [38, 38],
            iconAnchor: conPulso ? [22, 22] : [19, 19],
            popupAnchor: [0, -20]
        });
    }

    function inicializarMapaSeguimiento(mapEl) {
        const origenLat = parseFloat(mapEl.dataset.origenLat) || -34.598500;
        const origenLng = parseFloat(mapEl.dataset.origenLng) || -58.420100;
        const destinoLat = parseFloat(mapEl.dataset.destinoLat) || -34.617500;
        const destinoLng = parseFloat(mapEl.dataset.destinoLng) || -58.367500;
        const destinoNombre = mapEl.dataset.destinoNombre || 'Entidad Beneficiaria';
        const viajeActivo = mapEl.dataset.viajeActivo === 'true';

        const map = L.map(mapEl, {
            zoomControl: true,
            scrollWheelZoom: true
        }).setView([(origenLat + destinoLat) / 2, (origenLng + destinoLng) / 2], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        const iconOrigen = crearIconoHtml('dt-pin-origen', 'fa-warehouse', false);
        const iconDestino = crearIconoHtml('dt-pin-destino', 'fa-hand-holding-heart', false);
        const iconCamion = crearIconoHtml('dt-pin-camion', 'fa-truck', true);

        L.marker([origenLat, origenLng], { icon: iconOrigen })
            .addTo(map)
            .bindPopup('<strong>Depósito Central DonaTrack</strong><br>Av. Medrano 951, CABA');

        L.marker([destinoLat, destinoLng], { icon: iconDestino })
            .addTo(map)
            .bindPopup('<strong>' + escaparHtml(destinoNombre) + '</strong><br>Punto de entrega');

        const markerCamion = L.marker([origenLat, origenLng], {
            icon: iconCamion,
            zIndexOffset: 1000
        }).addTo(map);
        markerCamion.bindPopup('<strong>Camión DonaTrack</strong><br>Destino: ' + escaparHtml(destinoNombre));

        let routeLine = null;

        // 1. Obtener o asegurar ruta en backend SIN reiniciar el progreso actual
        fetch('/api/rutas/calcular?iniciarSiActivo=' + encodeURIComponent(viajeActivo), {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                origenLat: origenLat,
                origenLng: origenLng,
                destinoLat: destinoLat,
                destinoLng: destinoLng
            })
        })
            .then(function (resp) { return resp.json(); })
            .then(function (ruta) {
                const coords = ruta && (ruta.geometry || ruta.coordenadas);
                if (Array.isArray(coords) && coords.length > 0) {
                    if (routeLine) {
                        map.removeLayer(routeLine);
                        routeLine = null;
                    }
                    // No agregamos la polyline al mapa para que solo se mueva el camión sin trazado
                    const bounds = L.latLngBounds(coords);
                    map.fitBounds(bounds, { padding: [45, 45] });
                }

                // 2. Consultar posición actual del camión correspondiente a este destino sin reiniciar el recorrido
                return fetch('/api/rutas/posicion-actual?destinoLat=' + encodeURIComponent(destinoLat) + '&destinoLng=' + encodeURIComponent(destinoLng));
            })
            .then(function (resp) {
                if (resp && resp.status === 200) {
                    return resp.json();
                }
                return null;
            })
            .then(function (posActual) {
                if (posActual) {
                    markerCamion.setLatLng([posActual.latitud, posActual.longitud]);
                    actualizarTelemetria(posActual);
                }
            })
            .catch(function (err) {
                console.warn('Error al cargar estado de ruta OSRM:', err);
            });

        // 3. Polling cada 1 segundo filtrado por destino (permite múltiples camiones/mapas en simultáneo sin pisarse)
        if (viajeActivo) {
            setInterval(function () {
                fetch('/api/rutas/posicion-actual?destinoLat=' + encodeURIComponent(destinoLat) + '&destinoLng=' + encodeURIComponent(destinoLng))
                    .then(function (r) { return r.ok ? r.json() : null; })
                    .then(function (pos) {
                        if (pos) {
                            markerCamion.setLatLng([pos.latitud, pos.longitud]);
                            actualizarTelemetria(pos);
                        }
                    })
                    .catch(function () {});
            }, 1000);
        }
    }

    function actualizarTelemetria(pos) {
        const elProgresoTexto = document.getElementById('dt-progreso-texto');
        const elProgresoBarra = document.getElementById('dt-progreso-barra');
        const elDistancia = document.getElementById('dt-distancia-restante');
        const elTiempo = document.getElementById('dt-tiempo-estimado');
        const elCoords = document.getElementById('dt-coordenadas-actuales');
        const elBadgeVivo = document.getElementById('dt-badge-vivo');
        const elTextoEstado = document.getElementById('dt-texto-estado');

        const porcentaje = pos.porcentaje !== undefined ? pos.porcentaje : (pos.porcentajeProgreso || 0);
        const estado = pos.estado || 'EN_CAMINO';
        const enDestino = estado === 'DESTINO_ALCANZADO';
        const enDeposito = estado === 'EN_DEPOSITO';
        const enParada = estado === 'EN_PARADA' || pos.enParada === true;

        const pct = Math.min(100, Math.max(0, Math.round(porcentaje)));
        if (elProgresoTexto) elProgresoTexto.textContent = pct + '%';
        if (elProgresoBarra) elProgresoBarra.style.width = pct + '%';
        if (elCoords && pos.latitud !== undefined && pos.longitud !== undefined) {
            elCoords.textContent = pos.latitud.toFixed(4) + ', ' + pos.longitud.toFixed(4);
        }

        const kmRestantes = pos.distanciaRestanteKm !== undefined ? pos.distanciaRestanteKm : 0;
        const minRestantes = pos.tiempoRestanteMin !== undefined ? Math.ceil(pos.tiempoRestanteMin) : 0;

        if (elDistancia) {
            elDistancia.textContent = (enDestino || enParada) ? '0.00 km' : kmRestantes.toFixed(2) + ' km';
        }
        if (elTiempo) {
            if (enDestino) {
                elTiempo.textContent = 'Entregado';
            } else if (enParada) {
                elTiempo.textContent = 'En parada';
            } else if (enDeposito) {
                elTiempo.textContent = 'Esperando salida';
            } else {
                elTiempo.textContent = minRestantes + ' min';
            }
        }

        const valoresTiempoVista = document.querySelectorAll('.tarjetaDonacionEnRuta .valorTiempo');
        valoresTiempoVista.forEach(function (el) {
            el.textContent = enDestino ? 'Llegó a destino' : (enParada ? 'Camión en sede' : (enDeposito ? 'En depósito' : minRestantes + ' min'));
        });

        if (elBadgeVivo && elTextoEstado) {
            if (enDestino) {
                elBadgeVivo.classList.add('entregado');
                elTextoEstado.textContent = 'Llegó a destino';
            } else if (enParada) {
                elBadgeVivo.classList.remove('entregado');
                const paradaTxt = pos.numeroParadaActual ? 'En Parada #' + pos.numeroParadaActual : 'En parada';
                elTextoEstado.textContent = paradaTxt + ' · Esperando confirmación';
            } else if (enDeposito) {
                elBadgeVivo.classList.remove('entregado');
                elTextoEstado.textContent = 'En depósito · Esperando inicio de viaje';
            } else {
                elBadgeVivo.classList.remove('entregado');
                elTextoEstado.textContent = 'En traslado en vivo';
            }
        }
    }

    function inicializarMapaPublico(mapEl) {
        const map = L.map(mapEl, {
            zoomControl: true,
            scrollWheelZoom: false
        }).setView([-34.6083, -58.3950], 12);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        let marcadoresCargados = false;
        let markersGroup = L.layerGroup().addTo(map);

        function renderizarPuntos(listaPuntos) {
            if (!Array.isArray(listaPuntos) || listaPuntos.length === 0) return;
            markersGroup.clearLayers();
            const bounds = [];

            listaPuntos.forEach(function (punto) {
                const lat = punto.latitud || -34.6085;
                const lng = punto.longitud || -58.3840;
                bounds.push([lat, lng]);

                const fotoUrl = (punto.fotoEntrega && punto.fotoEntrega.trim() !== '')
                    ? punto.fotoEntrega
                    : 'https://images.unsplash.com/photo-1593113598332-cd288d649433?auto=format&fit=crop&w=600&q=80';

                const esDeposito = punto.estado === 'BASE_CENTRAL' || (punto.nombreEntidad && punto.nombreEntidad.includes('Depósito'));
                const esHeladera = punto.tipoEntidad && punto.tipoEntidad.includes('Heladera');
                
                let iconoFa = 'fa-heart';
                let clasePin = 'dt-pin-caso-exito con-foto';
                let badgeTxt = 'Caso de Éxito';
                let badgeIcon = 'fa-check-circle';

                if (esDeposito) {
                    iconoFa = 'fa-warehouse';
                    clasePin = 'dt-pin-origen';
                    badgeTxt = 'Base Logística';
                    badgeIcon = 'fa-warehouse';
                } else if (esHeladera) {
                    iconoFa = 'fa-snowflake';
                    clasePin = 'dt-pin-destino';
                    badgeTxt = 'Punto Activo 24hs';
                    badgeIcon = 'fa-clock';
                }

                const iconPunto = crearIconoHtml(clasePin, iconoFa, false);

                const popupHtml =
                    '<div class="dt-popup-caso-exito">' +
                        '<div class="dt-popup-foto-contenedor">' +
                            '<img class="dt-popup-foto" src="' + fotoUrl + '" alt="' + escaparHtml(punto.nombreEntidad) + '"/>' +
                        '</div>' +
                        '<span class="dt-popup-badge"><i class="fas ' + badgeIcon + '"></i> ' + badgeTxt + '</span>' +
                        '<h4 class="dt-popup-titulo">' + escaparHtml(punto.nombreEntidad || 'Punto de entrega') + '</h4>' +
                        '<p class="dt-popup-donacion"><strong>' + escaparHtml(punto.tituloDonacion || 'Donación asignada') + '</strong></p>' +
                        (punto.descripcionDonacion ? '<p style="font-size: 0.8rem; color: #64748b; margin: 4px 0 6px 0;">' + escaparHtml(punto.descripcionDonacion) + '</p>' : '') +
                        '<p class="dt-popup-direccion"><i class="fas fa-location-dot"></i> ' + escaparHtml(punto.direccionEntidad || 'CABA') + '</p>' +
                    '</div>';

                const marker = L.marker([lat, lng], { icon: iconPunto });
                marker.bindPopup(popupHtml, { maxWidth: 280, closeButton: false });

                marker.on('mouseover', function () {
                    this.openPopup();
                });

                markersGroup.addLayer(marker);
            });

            if (bounds.length > 1) {
                map.fitBounds(bounds, { padding: [40, 40] });
            }
        }

        // 1. Cargar directamente desde el endpoint público (incluye fotos base64 reales de las entregas confirmadas)
        fetch('/api/puntos-entrega')
            .then(function (res) {
                if (!res.ok) throw new Error('Status ' + res.status);
                return res.json();
            })
            .then(function (puntos) {
                if (Array.isArray(puntos) && puntos.length > 0) {
                    marcadoresCargados = true;
                    renderizarPuntos(puntos);
                }
            })
            .catch(function (err) {
                console.warn('[MapaPublico] Error al consultar /api/puntos-entrega:', err);
            });
    }

    function escaparHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }
})();
