// Fecha (YYYY-MM-DD) en hora local. toISOString() devuelve la fecha UTC, que en Argentina
// ya es "mañana" a partir de las 21 h.
function fechaLocal(fecha) {
  const mes = String(fecha.getMonth() + 1).padStart(2, '0');
  const dia = String(fecha.getDate()).padStart(2, '0');
  return fecha.getFullYear() + '-' + mes + '-' + dia;
}

document.addEventListener('DOMContentLoaded', function () {
  const hoy = fechaLocal(new Date());

  // 1. Establecer fecha de hoy en el campo de fecha
  const campoFecha = document.getElementById('campoFechaDonacion');
  if (campoFecha && !campoFecha.value) {
    campoFecha.value = hoy;
  }

  // =========================================================================
  // 2. BUSCADOR Y SELECCIÓN DE DONANTE
  // =========================================================================
  const donantes = Array.isArray(window.listaDonantesGlobal) ? window.listaDonantesGlobal : [];
  const campoBuscar = document.getElementById('campoBuscarDonante');
  const contenedorResultados = document.getElementById('listaResultadosDonante');
  const ulResultados = document.getElementById('ulResultadosDonantes');
  const tarjetaSeleccionado = document.getElementById('tarjetaDonanteSeleccionado');
  const contenedorBuscador = document.getElementById('contenedorBuscadorDonante');
  const inputIdDonante = document.getElementById('idDonanteSeleccionado');
  const btnCambiarDonante = document.getElementById('btnCambiarDonante');

  const avatarEl = document.getElementById('avatarDonante');
  const nombreEl = document.getElementById('nombreDonanteSeleccionado');
  const detalleEl = document.getElementById('detalleDonanteSeleccionado');

  function obtenerIniciales(nombre) {
    if (!nombre) return 'D';
    const partes = nombre.trim().split(/\s+/);
    if (partes.length >= 2) {
      return (partes[0][0] + partes[1][0]).toUpperCase();
    }
    return partes[0].substring(0, 2).toUpperCase();
  }

  function obtenerContactoTexto(donante) {
    if (donante.contactos && donante.contactos.length > 0) {
      return donante.contactos[0].valorDelContacto || '';
    }
    return '';
  }

  function seleccionarDonante(donante) {
    inputIdDonante.value = donante.id;
    avatarEl.textContent = obtenerIniciales(donante.nombre);
    nombreEl.textContent = donante.nombre || ('Donante #' + donante.id);
    const contacto = obtenerContactoTexto(donante);
    const tipoTexto = donante.tipo === 'JURIDICA' ? 'Empresa' : 'Persona Humana';
    detalleEl.textContent = (donante.documento ? ('Doc: ' + donante.documento + ' · ') : '') + (contacto ? (contacto + ' · ') : '') + tipoTexto;

    tarjetaSeleccionado.style.display = 'flex';
    contenedorBuscador.style.display = 'none';
    contenedorResultados.style.display = 'none';
    campoBuscar.value = '';
  }

  function deseleccionarDonante() {
    inputIdDonante.value = '';
    tarjetaSeleccionado.style.display = 'none';
    contenedorBuscador.style.display = 'flex';
    campoBuscar.value = '';
    campoBuscar.focus();
  }

  if (btnCambiarDonante) {
    btnCambiarDonante.addEventListener('click', deseleccionarDonante);
  }

  if (campoBuscar) {
    campoBuscar.addEventListener('input', function () {
      const query = this.value.trim().toLowerCase();
      if (query.length === 0) {
        contenedorResultados.style.display = 'none';
        ulResultados.innerHTML = '';
        return;
      }

      const filtrados = donantes.filter(d => {
        const nom = (d.nombre || '').toLowerCase();
        const doc = (d.documento || '').toLowerCase();
        const idStr = String(d.id || '');
        const contactosStr = (d.contactos || []).map(c => (c.valorDelContacto || '').toLowerCase()).join(' ');
        return nom.includes(query) || doc.includes(query) || idStr.includes(query) || contactosStr.includes(query);
      });

      ulResultados.innerHTML = '';
      if (filtrados.length === 0) {
        const li = document.createElement('li');
        li.innerHTML = '<span class="textoAyuda" style="padding: 12px; display: block; text-align: center;">No se encontraron donantes con "' + query + '".</span>';
        ulResultados.appendChild(li);
      } else {
        filtrados.slice(0, 6).forEach(d => {
          const li = document.createElement('li');
          const iniciales = obtenerIniciales(d.nombre);
          const contacto = obtenerContactoTexto(d);
          const esEmpresa = d.tipo === 'JURIDICA';
          const btn = document.createElement('button');
          btn.type = 'button';
          btn.className = 'resultadoDonante';
          btn.innerHTML = `
            <span class="avatarResultado" aria-hidden="true">${iniciales}</span>
            <span class="textoResultado">
              <span class="nombreResultado">${d.nombre || ('Donante #' + d.id)}</span>
              <span class="metaResultado">${d.documento ? ('Doc: ' + d.documento + ' · ') : ''}${contacto ? (contacto + ' · ') : ''}ID: ${d.id}</span>
            </span>
            <span class="insigniaRecurrente">${esEmpresa ? 'Empresa' : 'Persona'}</span>
          `;
          btn.addEventListener('click', function () {
            seleccionarDonante(d);
          });
          li.appendChild(btn);
          ulResultados.appendChild(li);
        });
      }
      contenedorResultados.style.display = 'block';
    });

    // Ocultar resultados si hace clic fuera
    document.addEventListener('click', function (e) {
      if (!contenedorBuscador.contains(e.target)) {
        contenedorResultados.style.display = 'none';
      }
    });
  }

  // Si hay donantes y ninguno seleccionado, preseleccionar el primero o dejar listo el buscador
  if (donantes.length > 0 && !inputIdDonante.value) {
    seleccionarDonante(donantes[0]);
  }

  // =========================================================================
  // 3. GESTIÓN DINÁMICA DE ÍTEMS / BIENES
  // =========================================================================
  // Lote de prueba del Flujo 3 (sillas usadas, mesa nueva, fideos, conservas).
  // La subcategoría se resuelve por nombre desde el selector del modal para no depender de IDs.
  function crearItemPrecargado(nombreSubcategoria, datos) {
    const selectSubcat = document.getElementById('modalCategoriaItem');
    const opcion = selectSubcat
      ? Array.from(selectSubcat.options).find(o => (o.getAttribute('data-nombre') || '').toUpperCase() === nombreSubcategoria)
      : null;
    if (!opcion) {
      console.warn('Subcategoría "' + nombreSubcategoria + '" no encontrada; se omite el ítem precargado "' + datos.nombre + '".');
      return null;
    }
    return {
      nombre: datos.nombre,
      descripcion: datos.descripcion,
      categoria: opcion.getAttribute('data-categoria') || 'General',
      cantidad: datos.cantidad,
      unidadDeMedida: (opcion.getAttribute('data-unidad') || 'UNIDADES').toUpperCase(),
      subcategoriaId: parseInt(opcion.value) || null,
      estado: datos.estado,
      fechaVencimiento: datos.fechaVencimiento || null,
      urlFoto: null
    };
  }

  const vencimientoLataPrecargada = new Date();
  vencimientoLataPrecargada.setMonth(vencimientoLataPrecargada.getMonth() + 6);

  let items = [
    crearItemPrecargado('SILLAS', {
      nombre: 'Sillas de madera', descripcion: 'Sillas de comedor con desgaste leve.',
      cantidad: 6, estado: 'USADO'
    }),
    crearItemPrecargado('MESAS', {
      nombre: 'Mesa de comedor', descripcion: 'Mesa de madera sin uso.',
      cantidad: 1, estado: 'NUEVO'
    }),
    crearItemPrecargado('FIDEOS SECOS', {
      nombre: 'Fideos secos 500 g', descripcion: 'Paquetes cerrados, no perecederos.',
      cantidad: 100, estado: 'NUEVO'
    }),
    crearItemPrecargado('LATAS DE CONSERVA', {
      nombre: 'Latas de conserva', descripcion: 'Latas de conserva con vencimiento a más de 6 meses.',
      cantidad: 50, estado: 'NUEVO', fechaVencimiento: fechaLocal(vencimientoLataPrecargada)
    })
  ].filter(item => item !== null);

  const listaItemsUl = document.getElementById('listaItems');
  const contadorItemsSpan = document.getElementById('contadorItems');
  const mensajeSinItems = document.getElementById('mensajeSinItems');
  const inputBienesJson = document.getElementById('bienesJson');

  function actualizarContador() {
    const total = items.length;
    if (total === 1) {
      contadorItemsSpan.textContent = '1 ítem cargado';
    } else {
      contadorItemsSpan.textContent = total + ' ítems cargados';
    }
    if (total === 0) {
      mensajeSinItems.style.display = 'block';
    } else {
      mensajeSinItems.style.display = 'none';
    }
    inputBienesJson.value = JSON.stringify(items);
  }

  function abrirDetalleItem(index) {
    const item = items[index];
    if (!item) return;
    document.getElementById('detalleTitulo').textContent = item.nombre;
    document.getElementById('detalleSubtitulo').textContent = item.categoria || 'Bien donado';
    document.getElementById('detalleCategoria').textContent = item.categoria || 'General';
    document.getElementById('detalleEstado').textContent = item.estado === 'NUEVO' ? 'Nuevo' : 'Usado';
    let textoCant = item.cantidad + ' ' + (item.unidadDeMedida || 'unidades');
    if (item.unidadOriginal && item.unidadOriginal !== item.unidadDeMedida) {
      textoCant += ' (equivalente a ' + item.cantidadOriginal + ' ' + item.unidadOriginal + ')';
    }
    document.getElementById('detalleCantidad').textContent = textoCant;
    
    const filaVenc = document.getElementById('filaDetalleVencimiento');
    if (item.fechaVencimiento) {
      document.getElementById('detalleVencimiento').textContent = item.fechaVencimiento;
      filaVenc.style.display = 'flex';
    } else {
      filaVenc.style.display = 'none';
    }
    document.getElementById('detalleEstado').textContent = item.estado === 'NUEVO' ? 'Nuevo' : 'Usado';
    document.getElementById('detalleDescripcion').textContent = item.descripcion || '-';

    const imgDetalle = document.getElementById('detalleFoto');
    if (item.urlFoto) {
      imgDetalle.src = item.urlFoto;
      imgDetalle.hidden = false;
    } else {
      imgDetalle.hidden = true;
      imgDetalle.removeAttribute('src');
    }
    window.location.hash = 'modalDetalleGenerico';
  }

  function renderItems() {
    listaItemsUl.innerHTML = '';
    items.forEach((item, index) => {
      const li = document.createElement('li');
      li.className = 'tarjetaItem';

      const esNuevo = item.estado === 'NUEVO';
      const claseEstado = esNuevo ? 'estadoNuevo' : 'estadoUsado';
      const textoEstado = esNuevo ? 'Nuevo' : 'Usado';

      li.innerHTML = `
        <div class="miniaturaItem">
          <img src="${item.urlFoto || '/img/images (1).jpg'}" alt="Foto del bien donado" class="imagenItem" onerror="this.src='/img/voluntarios.jpg'">
        </div>

        <div class="datosItem">
          <p class="nombreItem">${item.nombre}</p>
          <p class="descripcionItem">${item.descripcion || ''}</p>
          <div class="etiquetasItem">
            <span class="etiquetaDatoItem"><i class="fas fa-tag" aria-hidden="true"></i> ${item.categoria || 'Bienes'}</span>
            <span class="etiquetaEstadoItem ${claseEstado}">${textoEstado}</span>
            ${item.unidadOriginal && item.unidadOriginal !== item.unidadDeMedida ? `<span class="badgeConversion" title="Ingresado originalmente como ${item.cantidadOriginal} ${item.unidadOriginal}"><i class="fas fa-calculator" aria-hidden="true"></i> ${item.cantidadOriginal} ${item.unidadOriginal} → ${item.cantidad} ${item.unidadDeMedida}</span>` : ''}
          </div>
        </div>

        <div class="grupoCampo campoItemCantidad">
          <label class="etiquetaItem" for="cantidadItem_${index}">CANTIDAD (${item.unidadDeMedida || 'U'})</label>
          <div class="controlCantidad">
            <button type="button" class="botonCantidad btnMenos" data-index="${index}" aria-label="Disminuir cantidad">
              <i class="fas fa-minus" aria-hidden="true"></i>
            </button>
            <input type="number" id="cantidadItem_${index}" class="campoCantidad" value="${item.cantidad}" min="0.001" step="any" data-index="${index}">
            <button type="button" class="botonCantidad btnMas" data-index="${index}" aria-label="Aumentar cantidad">
              <i class="fas fa-plus" aria-hidden="true"></i>
            </button>
          </div>
        </div>

        <div class="accionesItem">
          <button type="button" class="botonAccionItem btnInfoItem" data-index="${index}" title="Ver más información">
            <i class="fas fa-circle-info" aria-hidden="true"></i>
          </button>
          <button type="button" class="botonAccionItem botonQuitarItem btnEliminarItem" data-index="${index}" title="Quitar ítem">
            <i class="fas fa-trash-can" aria-hidden="true"></i>
          </button>
        </div>
      `;

      // Eventos en los controles de la tarjeta
      li.querySelector('.btnMenos').addEventListener('click', function () {
        const step = items[index].cantidad <= 1 ? 0.5 : 1;
        if (items[index].cantidad > step) {
          items[index].cantidad = Math.round((items[index].cantidad - step) * 1000) / 1000;
          renderItems();
        }
      });

      li.querySelector('.btnMas').addEventListener('click', function () {
        const step = items[index].cantidad < 1 ? 0.5 : 1;
        items[index].cantidad = Math.round((items[index].cantidad + step) * 1000) / 1000;
        renderItems();
      });

      li.querySelector('.campoCantidad').addEventListener('change', function () {
        const val = parseFloat(this.value);
        items[index].cantidad = (val && val > 0) ? Math.round(val * 1000) / 1000 : 1;
        renderItems();
      });

      li.querySelector('.btnInfoItem').addEventListener('click', function () {
        abrirDetalleItem(index);
      });

      li.querySelector('.btnEliminarItem').addEventListener('click', function () {
        items.splice(index, 1);
        renderItems();
      });

      listaItemsUl.appendChild(li);
    });

    actualizarContador();
  }

  // Render inicial de ítems
  renderItems();

  // =========================================================================
  // 4. TABLA DE MAGNITUDES Y CONVERSIÓN AUTOMÁTICA (OPCIÓN A)
  // =========================================================================
  const tablaConversiones = {
    'KG': [
      { label: 'Kilogramos (KG)', valor: 'KG', factor: 1 },
      { label: 'Gramos (GR)', valor: 'GR', factor: 0.001 },
      { label: 'Toneladas (TON)', valor: 'TON', factor: 1000 }
    ],
    'L': [
      { label: 'Litros (L)', valor: 'L', factor: 1 },
      { label: 'Mililitros (ML)', valor: 'ML', factor: 0.001 },
      { label: 'Centímetros cúbicos (CM3)', valor: 'CM3', factor: 0.001 },
      { label: 'Metros cúbicos (M3)', valor: 'M3', factor: 1000 }
    ],
    'M': [
      { label: 'Metros (M)', valor: 'M', factor: 1 },
      { label: 'Centímetros (CM)', valor: 'CM', factor: 0.01 },
      { label: 'Milímetros (MM)', valor: 'MM', factor: 0.001 }
    ],
    'UNIDADES': [
      { label: 'Unidades', valor: 'UNIDADES', factor: 1 },
      { label: 'Docenas (x12)', valor: 'DOCENAS', factor: 12 },
      { label: 'Pares (x2)', valor: 'PARES', factor: 2 },
      { label: 'Cajas (x10)', valor: 'CAJAS_10', factor: 10 },
      { label: 'Cajas (x20)', valor: 'CAJAS_20', factor: 20 }
    ],
    'PAQUETES': [
      { label: 'Paquetes', valor: 'PAQUETES', factor: 1 },
      { label: 'Docenas de paquetes (x12)', valor: 'DOCENAS', factor: 12 },
      { label: 'Bulto x10 paquetes', valor: 'BULTO_10', factor: 10 },
      { label: 'Bulto x20 paquetes', valor: 'BULTO_20', factor: 20 }
    ]
  };

  const modalCatSelect = document.getElementById('modalCategoriaItem');
  const modalUniSelect = document.getElementById('modalUnidadItem');
  const modalCantInput = document.getElementById('modalCantidadItem');
  const bannerConversion = document.getElementById('bannerConversionUnidad');
  const panelCrearSubcat = document.getElementById('panelCrearSubcategoria');
  const inputNuevaSubcatNombre = document.getElementById('nuevaSubcatNombre');
  const selectNuevaSubcatCat = document.getElementById('nuevaSubcatCategoria');
  const selectNuevaSubcatUni = document.getElementById('nuevaSubcatUnidad');
  const btnGuardarNuevaSubcat = document.getElementById('btnGuardarNuevaSubcat');
  const btnCancelarNuevaSubcat = document.getElementById('btnCancelarNuevaSubcat');

  function obtenerUnidadBaseActual() {
    if (!modalCatSelect || !modalCatSelect.value || modalCatSelect.value === '__CREAR_NUEVA__') return 'UNIDADES';
    const opt = modalCatSelect.options[modalCatSelect.selectedIndex];
    return (opt && opt.getAttribute('data-unidad') ? opt.getAttribute('data-unidad') : 'UNIDADES').toUpperCase();
  }

  function actualizarOpcionesDeUnidad() {
    if (!modalUniSelect) return;
    const baseUnidad = obtenerUnidadBaseActual();
    const opciones = tablaConversiones[baseUnidad] || [
      { label: baseUnidad, valor: baseUnidad, factor: 1 }
    ];

    modalUniSelect.innerHTML = '';
    opciones.forEach((opc, i) => {
      const optEl = document.createElement('option');
      optEl.value = opc.valor;
      optEl.textContent = opc.label;
      optEl.dataset.factor = opc.factor;
      if (i === 0) optEl.selected = true;
      modalUniSelect.appendChild(optEl);
    });

    actualizarBannerConversion();
  }

  function actualizarBannerConversion() {
    if (!bannerConversion || !modalCantInput || !modalUniSelect) return;
    const baseUnidad = obtenerUnidadBaseActual();
    const cantVal = parseFloat(modalCantInput.value);
    const cant = (cantVal && cantVal > 0) ? cantVal : 0;
    const optUnidad = modalUniSelect.options[modalUniSelect.selectedIndex];
    if (!optUnidad || cant === 0) {
      bannerConversion.style.display = 'none';
      return;
    }

    const factor = parseFloat(optUnidad.dataset.factor) || 1;
    const nombreUnidad = optUnidad.value;

    if (factor === 1) {
      bannerConversion.className = 'bannerConversion';
      bannerConversion.style.display = 'flex';
      bannerConversion.innerHTML = `
        <i class="fas fa-check-circle" aria-hidden="true"></i>
        <span>Unidad canónica del sistema: <strong>${cant} ${baseUnidad}</strong>.</span>
      `;
    } else {
      const convertido = Math.round((cant * factor) * 10000) / 10000;
      bannerConversion.className = 'bannerConversion conversionDestacada';
      bannerConversion.style.display = 'flex';
      bannerConversion.innerHTML = `
        <i class="fas fa-wand-magic-sparkles" aria-hidden="true"></i>
        <span><strong>Conversión automática:</strong> ${cant} ${nombreUnidad} equivalen a <strong>${convertido} ${baseUnidad}</strong> (se guardará como ${convertido} ${baseUnidad} en el stock).</span>
      `;
    }
  }

  if (modalCatSelect) {
    modalCatSelect.addEventListener('change', function () {
      if (this.value === '__CREAR_NUEVA__') {
        if (panelCrearSubcat) panelCrearSubcat.style.display = 'block';
        if (inputNuevaSubcatNombre) inputNuevaSubcatNombre.focus();
        if (bannerConversion) bannerConversion.style.display = 'none';
      } else {
        if (panelCrearSubcat) panelCrearSubcat.style.display = 'none';
        actualizarOpcionesDeUnidad();
      }
    });
  }

  if (modalUniSelect) {
    modalUniSelect.addEventListener('change', actualizarBannerConversion);
  }

  if (modalCantInput) {
    modalCantInput.addEventListener('input', actualizarBannerConversion);
  }

  // Alta inline de subcategoría
  if (btnCancelarNuevaSubcat) {
    btnCancelarNuevaSubcat.addEventListener('click', function () {
      if (panelCrearSubcat) panelCrearSubcat.style.display = 'none';
      if (modalCatSelect) modalCatSelect.selectedIndex = 0;
      actualizarOpcionesDeUnidad();
    });
  }

  if (btnGuardarNuevaSubcat) {
    btnGuardarNuevaSubcat.addEventListener('click', function () {
      const rawNombre = inputNuevaSubcatNombre ? inputNuevaSubcatNombre.value.trim() : '';
      if (!rawNombre) {
        alert('Por favor, ingresá el nombre de la subcategoría.');
        if (inputNuevaSubcatNombre) inputNuevaSubcatNombre.focus();
        return;
      }
      // Normalizar: mayúsculas y sin acentos
      const nombre = rawNombre.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toUpperCase().trim();
      const catId = parseInt(selectNuevaSubcatCat.value);
      const catNombre = selectNuevaSubcatCat.options[selectNuevaSubcatCat.selectedIndex].text;
      const unidad = selectNuevaSubcatUni.value;

      btnGuardarNuevaSubcat.disabled = true;
      btnGuardarNuevaSubcat.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

      const csrfInput = document.querySelector('input[name="_csrf"]');
      const csrfToken = csrfInput ? csrfInput.value : '';
      const requestHeaders = {
        'Content-Type': 'application/json'
      };
      if (csrfToken) {
        requestHeaders['X-CSRF-TOKEN'] = csrfToken;
      }

      fetch('/personaAdministradora/subcategorias', {
        method: 'POST',
        headers: requestHeaders,
        body: JSON.stringify({
          nombre: nombre,
          categoriaId: catId,
          unidadDeMedida: unidad
        })
      })
      .then(async res => {
        const contentType = res.headers.get('content-type') || '';
        if (!res.ok) {
          let errorMsg = 'Error en el servidor (' + res.status + ')';
          if (contentType.includes('application/json')) {
            try {
              const errJson = await res.json();
              if (errJson && errJson.error) errorMsg = errJson.error;
            } catch (_) {}
          } else {
            try {
              const text = await res.text();
              if (text && !text.includes('<html')) errorMsg = text;
            } catch (_) {}
          }
          throw new Error(errorMsg);
        }
        if (contentType.includes('application/json')) {
          return res.json();
        } else {
          throw new Error('La respuesta del servidor no fue JSON. Por favor reiniciá la aplicación en IntelliJ para cargar los cambios.');
        }
      })
      .then(data => {
        const nuevaOpt = document.createElement('option');
        nuevaOpt.value = data.id;
        nuevaOpt.setAttribute('data-categoria', catNombre);
        nuevaOpt.setAttribute('data-nombre', data.nombre);
        nuevaOpt.setAttribute('data-unidad', data.unidadDeMedida || unidad);
        nuevaOpt.textContent = catNombre + ' - ' + data.nombre + ' (' + (data.unidadDeMedida || unidad) + ')';

        const optCrear = modalCatSelect.querySelector('option[value="__CREAR_NUEVA__"]');
        if (optCrear) {
          modalCatSelect.insertBefore(nuevaOpt, optCrear);
        } else {
          modalCatSelect.appendChild(nuevaOpt);
        }

        nuevaOpt.selected = true;
        if (panelCrearSubcat) panelCrearSubcat.style.display = 'none';
        if (inputNuevaSubcatNombre) inputNuevaSubcatNombre.value = '';

        actualizarOpcionesDeUnidad();
        alert('¡Subcategoría "' + data.nombre + '" creada y seleccionada correctamente!');
      })
      .catch(err => {
        console.error(err);
        alert('No se pudo crear la subcategoría: ' + err.message);
      })
      .finally(() => {
        btnGuardarNuevaSubcat.disabled = false;
        btnGuardarNuevaSubcat.innerHTML = '<i class="fas fa-check" aria-hidden="true"></i> Guardar y seleccionar';
      });
    });
  }

  // Inicialización al abrir modal
  const btnAbrirModal = document.getElementById('btnAbrirModalAgregar');
  if (btnAbrirModal) {
    btnAbrirModal.addEventListener('click', function () {
      actualizarOpcionesDeUnidad();
    });
  }

  // --- Vencimiento: no se aceptan bienes ya vencidos ---
  const inputVencimiento = document.getElementById('modalVencimientoItem');
  const errorVencimiento = document.getElementById('errorVencimientoItem');
  if (inputVencimiento) inputVencimiento.min = hoy;

  function mostrarError(input, spanError, mensaje) {
    spanError.textContent = mensaje;
    spanError.hidden = false;
    input.classList.add('campoConError');
    input.setAttribute('aria-invalid', 'true');
    input.focus();
  }

  function limpiarError(input, spanError) {
    spanError.textContent = '';
    spanError.hidden = true;
    input.classList.remove('campoConError');
    input.removeAttribute('aria-invalid');
  }

  if (inputVencimiento && errorVencimiento) {
    inputVencimiento.addEventListener('input', function () {
      limpiarError(inputVencimiento, errorVencimiento);
    });
  }

  // --- Foto del bien: preview local y subida al confirmar el ítem ---
  const inputFoto = document.getElementById('modalFotoItem');
  const previewFoto = document.getElementById('previewFotoItem');
  const errorFoto = document.getElementById('errorFotoItem');

  function limpiarFoto() {
    if (previewFoto.src && previewFoto.src.startsWith('blob:')) URL.revokeObjectURL(previewFoto.src);
    inputFoto.value = '';
    previewFoto.hidden = true;
    previewFoto.removeAttribute('src');
    limpiarError(inputFoto, errorFoto);
  }

  if (inputFoto && previewFoto && errorFoto) {
    inputFoto.addEventListener('change', function () {
      limpiarError(inputFoto, errorFoto);
      if (previewFoto.src && previewFoto.src.startsWith('blob:')) URL.revokeObjectURL(previewFoto.src);
      const archivo = inputFoto.files[0];
      if (!archivo) {
        previewFoto.hidden = true;
        previewFoto.removeAttribute('src');
        return;
      }
      previewFoto.src = URL.createObjectURL(archivo);
      previewFoto.hidden = false;
    });
  }

  // Sube la imagen al front-end-app y devuelve la URL pública que se guarda en urlFoto.
  async function subirFoto(archivo) {
    const csrfInput = document.querySelector('input[name="_csrf"]');
    const formData = new FormData();
    formData.append('archivo', archivo);

    const headers = {};
    if (csrfInput && csrfInput.value) headers['X-CSRF-TOKEN'] = csrfInput.value;

    const res = await fetch('/personaAdministradora/fotos', { method: 'POST', headers: headers, body: formData });
    let cuerpo = null;
    try { cuerpo = await res.json(); } catch (_) {}
    if (!res.ok) {
      throw new Error((cuerpo && cuerpo.error) || ('No se pudo subir la imagen (' + res.status + ').'));
    }
    return cuerpo.url;
  }

  const btnConfirmar = document.getElementById('btnConfirmarAgregarItem');
  if (btnConfirmar) {
    btnConfirmar.addEventListener('click', async function () {
      const nombreInput = document.getElementById('modalNombreItem');
      const categoriaSelect = document.getElementById('modalCategoriaItem');
      const cantidadInput = document.getElementById('modalCantidadItem');
      const unidadSelect = document.getElementById('modalUnidadItem');
      const descInput = document.getElementById('modalDescripcionItem');
      const vencInput = document.getElementById('modalVencimientoItem');
      const estadoRadio = document.querySelector('input[name="modalEstadoItem"]:checked');

      const nombre = nombreInput.value.trim();
      if (!nombre) {
        alert('Por favor, ingresá el nombre del ítem.');
        nombreInput.focus();
        return;
      }

      if (!categoriaSelect.value || categoriaSelect.value === '__CREAR_NUEVA__') {
        alert('Por favor, seleccioná una subcategoría para clasificar el bien.');
        categoriaSelect.focus();
        return;
      }

      const selectedOption = categoriaSelect.options[categoriaSelect.selectedIndex];
      const subcatId = parseInt(categoriaSelect.value) || null;
      const catNombre = selectedOption ? (selectedOption.getAttribute('data-categoria') || 'General') : 'General';
      const baseUnidad = (selectedOption ? (selectedOption.getAttribute('data-unidad') || 'UNIDADES') : 'UNIDADES').toUpperCase();

      const cantIngresada = parseFloat(cantidadInput.value);
      if (!cantIngresada || cantIngresada <= 0) {
        alert('Por favor, ingresá una cantidad válida mayor a 0.');
        cantidadInput.focus();
        return;
      }

      if (vencInput.value && vencInput.value < hoy) {
        mostrarError(vencInput, errorVencimiento,
          'La fecha de vencimiento (' + vencInput.value + ') es anterior a hoy (' + hoy + '). No se pueden registrar bienes vencidos.');
        return;
      }

      const selectedUnitOpt = unidadSelect ? unidadSelect.options[unidadSelect.selectedIndex] : null;
      const factor = selectedUnitOpt ? (parseFloat(selectedUnitOpt.dataset.factor) || 1) : 1;
      const unidadIngresada = selectedUnitOpt ? selectedUnitOpt.value : baseUnidad;

      // Conversión exacta a la unidad canónica del sistema (Opción A)
      const cantidadConvertida = Math.round((cantIngresada * factor) * 10000) / 10000;

      const estado = estadoRadio ? estadoRadio.value : 'USADO';
      const descripcion = descInput.value.trim();
      const vencimiento = vencInput.value || null;

      let urlFoto = null;
      const archivoFoto = inputFoto && inputFoto.files[0];
      if (archivoFoto) {
        const textoOriginal = btnConfirmar.innerHTML;
        btnConfirmar.disabled = true;
        btnConfirmar.textContent = 'Subiendo foto…';
        try {
          urlFoto = await subirFoto(archivoFoto);
        } catch (err) {
          mostrarError(inputFoto, errorFoto, err.message);
          return;
        } finally {
          btnConfirmar.disabled = false;
          btnConfirmar.innerHTML = textoOriginal;
        }
      }

      items.push({
        nombre: nombre,
        descripcion: descripcion,
        categoria: catNombre,
        cantidad: cantidadConvertida,
        unidadDeMedida: baseUnidad,
        cantidadOriginal: cantIngresada,
        unidadOriginal: unidadIngresada,
        subcategoriaId: subcatId,
        estado: estado,
        fechaVencimiento: vencimiento,
        urlFoto: urlFoto
      });

      // Limpiar inputs del modal
      nombreInput.value = '';
      categoriaSelect.selectedIndex = 0;
      cantidadInput.value = '1';
      descInput.value = '';
      vencInput.value = '';
      limpiarError(vencInput, errorVencimiento);
      limpiarFoto();
      if (bannerConversion) bannerConversion.style.display = 'none';

      // Cerrar modal
      window.location.hash = '';

      // Actualizar lista
      renderItems();
    });
  }

  // =========================================================================
  // 5. VALIDACIÓN ANTES DE ENVIAR EL FORMULARIO
  // =========================================================================
  const formDonacion = document.getElementById('formularioRegistrarDonacion');
  if (formDonacion) {
    formDonacion.addEventListener('submit', function (e) {
      if (!inputIdDonante.value) {
        e.preventDefault();
        alert('Por favor seleccioná un donante para asociar la donación.');
        contenedorBuscador.scrollIntoView({ behavior: 'smooth' });
        campoBuscar.focus();
        return false;
      }

      if (items.length === 0) {
        e.preventDefault();
        alert('Debés agregar al menos un ítem a la donación.');
        document.getElementById('btnAbrirModalAgregar').scrollIntoView({ behavior: 'smooth' });
        return false;
      }

      // Asignar el JSON final de bienes antes del post
      inputBienesJson.value = JSON.stringify(items);
    });
  }
});
