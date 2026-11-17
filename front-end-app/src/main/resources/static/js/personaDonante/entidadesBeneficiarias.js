document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.cajaFiltroSelect').forEach(inicializarFiltroPersonalizado);
    inicializarMasonryEntidades();
});

/**
 * Distribuye las tarjetas en columnas de izquierda a derecha (por índice, tipo "round robin"),
 * de forma que cada columna apile su propio contenido sin dejar espacios en blanco cuando
 * una tarjeta de una fila anterior es más baja que sus vecinas.
 */
function inicializarMasonryEntidades() {
    const contenedor = document.querySelector('.cuadriculaEntidades');
    if (!contenedor) return;

    const tarjetas = Array.from(contenedor.querySelectorAll('.tarjetaEntidad'));
    if (tarjetas.length === 0) return;

    const anchoMinimoTarjeta = 360;
    const espacio = 24;
    let columnasActuales = 0;

    function calcularColumnas() {
        const anchoDisponible = contenedor.getBoundingClientRect().width;
        const columnas = Math.floor((anchoDisponible + espacio) / (anchoMinimoTarjeta + espacio));
        return Math.max(1, columnas);
    }

    function distribuir() {
        const columnas = calcularColumnas();
        if (columnas === columnasActuales) return;
        columnasActuales = columnas;

        const columnasElementos = Array.from({ length: columnas }, () => {
            const columna = document.createElement('div');
            columna.className = 'columnaEntidades';
            return columna;
        });

        tarjetas.forEach((tarjeta, indice) => {
            columnasElementos[indice % columnas].appendChild(tarjeta);
        });

        contenedor.replaceChildren(...columnasElementos);
    }

    distribuir();

    let temporizadorResize;
    window.addEventListener('resize', () => {
        clearTimeout(temporizadorResize);
        temporizadorResize = setTimeout(distribuir, 150);
    });
}

function inicializarFiltroPersonalizado(contenedor) {
    const select = contenedor.querySelector('select.selectorFiltro');
    if (!select) return;

    const boton = document.createElement('button');
    boton.type = 'button';
    boton.className = 'selectorFiltro botonFiltroPersonalizado';
    boton.setAttribute('aria-haspopup', 'listbox');
    boton.setAttribute('aria-expanded', 'false');
    if (select.hasAttribute('aria-label')) {
        boton.setAttribute('aria-label', select.getAttribute('aria-label'));
    }

    const textoBoton = document.createElement('span');
    textoBoton.className = 'textoFiltroPersonalizado';
    boton.appendChild(textoBoton);

    const lista = document.createElement('ul');
    lista.className = 'listaOpcionesFiltro';
    lista.setAttribute('role', 'listbox');
    lista.hidden = true;

    const opciones = Array.from(select.options).map((option, indice) => {
        const item = document.createElement('li');
        item.className = 'opcionFiltroEntidad';
        item.setAttribute('role', 'option');
        item.tabIndex = -1;
        item.dataset.valor = option.value;
        item.textContent = option.textContent;
        item.id = `${select.name}-opcion-${indice}`;
        item.setAttribute('aria-selected', option.selected ? 'true' : 'false');
        item.addEventListener('click', () => seleccionarOpcion(item));
        lista.appendChild(item);
        return item;
    });

    function seleccionarOpcion(item) {
        opciones.forEach((o) => o.setAttribute('aria-selected', o === item ? 'true' : 'false'));
        select.value = item.dataset.valor;
        textoBoton.textContent = item.textContent;
        select.dispatchEvent(new Event('change', { bubbles: true }));
        cerrarLista();
        boton.focus();
    }

    function abrirLista() {
        lista.hidden = false;
        boton.setAttribute('aria-expanded', 'true');
        contenedor.classList.add('abierto');
        const seleccionada = opciones.find((o) => o.getAttribute('aria-selected') === 'true');
        (seleccionada || opciones[0])?.focus();
    }

    function cerrarLista() {
        lista.hidden = true;
        boton.setAttribute('aria-expanded', 'false');
        contenedor.classList.remove('abierto');
    }

    function alternarLista() {
        if (lista.hidden) {
            abrirLista();
        } else {
            cerrarLista();
        }
    }

    boton.addEventListener('click', alternarLista);

    boton.addEventListener('keydown', (evento) => {
        if (evento.key === 'ArrowDown' || evento.key === 'Enter' || evento.key === ' ') {
            evento.preventDefault();
            abrirLista();
        }
    });

    lista.addEventListener('keydown', (evento) => {
        const indiceActual = opciones.indexOf(document.activeElement);
        if (evento.key === 'ArrowDown') {
            evento.preventDefault();
            opciones[Math.min(indiceActual + 1, opciones.length - 1)].focus();
        } else if (evento.key === 'ArrowUp') {
            evento.preventDefault();
            opciones[Math.max(indiceActual - 1, 0)].focus();
        } else if (evento.key === 'Enter' || evento.key === ' ') {
            evento.preventDefault();
            if (document.activeElement.classList.contains('opcionFiltroEntidad')) {
                seleccionarOpcion(document.activeElement);
            }
        } else if (evento.key === 'Escape') {
            evento.preventDefault();
            cerrarLista();
            boton.focus();
        } else if (evento.key === 'Tab') {
            cerrarLista();
        }
    });

    document.addEventListener('click', (evento) => {
        if (!contenedor.contains(evento.target)) {
            cerrarLista();
        }
    });

    const seleccionInicial = opciones.find((o) => o.getAttribute('aria-selected') === 'true') || opciones[0];
    if (seleccionInicial) {
        textoBoton.textContent = seleccionInicial.textContent;
    }

    //El filtro vive en un form GET: al cambiar el tipo se envia solo.
    select.addEventListener('change', () => {
        const formulario = select.closest('form');
        if (formulario) formulario.requestSubmit();
    });

    select.hidden = true;
    select.setAttribute('aria-hidden', 'true');
    select.tabIndex = -1;

    contenedor.insertBefore(boton, select);
    contenedor.appendChild(lista);
}
