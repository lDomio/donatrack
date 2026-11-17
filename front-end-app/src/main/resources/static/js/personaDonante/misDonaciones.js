document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.cajaSelectFiltro').forEach(inicializarSelectPersonalizado);
});

function inicializarSelectPersonalizado(contenedor) {
    const select = contenedor.querySelector('select.selectorFiltroDonacion');
    if (!select) return;

    const boton = document.createElement('button');
    boton.type = 'button';
    boton.className = 'selectorFiltroDonacion botonSelectPersonalizado';
    boton.setAttribute('aria-haspopup', 'listbox');
    boton.setAttribute('aria-expanded', 'false');
    if (select.hasAttribute('aria-label')) {
        boton.setAttribute('aria-label', select.getAttribute('aria-label'));
    }

    const textoBoton = document.createElement('span');
    textoBoton.className = 'textoSelectPersonalizado';
    boton.appendChild(textoBoton);

    const lista = document.createElement('ul');
    lista.className = 'listaOpcionesPersonalizada';
    lista.setAttribute('role', 'listbox');
    lista.hidden = true;

    const opciones = Array.from(select.options).map((option, indice) => {
        const item = document.createElement('li');
        item.className = 'opcionFiltro';
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

    function actualizarBoton(item) {
        const tieneSeleccion = item.dataset.valor !== '';
        textoBoton.textContent = item.textContent;
        boton.classList.toggle('mostrandoPlaceholder', !tieneSeleccion);
        boton.classList.toggle('conSeleccion', tieneSeleccion);
    }

    function seleccionarOpcion(item) {
        opciones.forEach((o) => o.setAttribute('aria-selected', o === item ? 'true' : 'false'));
        select.value = item.dataset.valor;
        actualizarBoton(item);
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
            if (document.activeElement.classList.contains('opcionFiltro')) {
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
        actualizarBoton(seleccionInicial);
    }

    select.hidden = true;
    select.setAttribute('aria-hidden', 'true');
    select.tabIndex = -1;

    contenedor.insertBefore(boton, select);
    contenedor.appendChild(lista);
}
