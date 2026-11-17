function toggleRecurrencia() {
    const radioHabitual = document.getElementById('recurrente');
    const fieldsetRecurrencia = document.getElementById('fieldset-recurrencia');
    const fechaInicio = document.getElementById('fechaDeInicio');

    if (radioHabitual.checked) {
        // Si es habitual, mostramos y hacemos obligatoria la fecha
        fieldsetRecurrencia.style.display = 'block';
        fechaInicio.required = true;
    } else {
        // Si es extraordinaria, ocultamos y quitamos la obligatoriedad
        fieldsetRecurrencia.style.display = 'none';
        fechaInicio.required = false;

        // Opcional: Reiniciar los valores si el usuario se arrepiente
        document.getElementById('dias').value = 0;
        document.getElementById('meses').value = 0;
        document.getElementById('anios').value = 0;
        fechaInicio.value = '';
    }
}

// 2. Ejecutar la función al cargar la página
// (Por si Thymeleaf ya trajo "HABITUAL" seleccionado tras un error de validación)
window.addEventListener('DOMContentLoaded', (event) => {
    toggleRecurrencia();
    cargarOpcionesDeUnidades();
});
document.getElementById('formulario-nueva-necesidad').addEventListener('submit', function(e) {
    const dias = parseInt(document.getElementById('dias').value) || 0;
    const meses = parseInt(document.getElementById('meses').value) || 0;
    const anios = parseInt(document.getElementById('anios').value) || 0;

    let isoDuration = 'P'; // El formato empieza con P de Period
    if (anios > 0) isoDuration += anios + 'Y';
    if (meses > 0) isoDuration += meses + 'M';
    if (dias > 0) isoDuration += dias + 'D';

    // Si todo es 0, enviamos 'P0D' por defecto para evitar un error en el backend
    if (isoDuration === 'P') {
        isoDuration = 'P0D';
    }
    console.log(isoDuration);
    // Asignamos el valor al input oculto antes de que viaje la petición
    document.getElementById('periodo').value = isoDuration;
});
function cargarSubcategorias(categoriaSeleccionada){
    const fieldsetSubcat = document.getElementById('fieldset-subcategorias');
    const contenedorSubcat = document.getElementById('subcategorias-container');

    fieldsetSubcat.style.display = 'block';
    contenedorSubcat.innerHTML = '<div class="spinner-carga"></div>';

    fetch(`/entidadBeneficiaria/subcategorias?categoriaId=${categoriaSeleccionada}`)
        .then(response => response.json())
        .then(data => {
            // Limpiamos el mensaje de carga
            contenedorSubcat.innerHTML = '';

            if (data.length === 0) {
                contenedorSubcat.innerHTML = '<p>No hay subcategorías para esta categoría.</p>';
                return;
            }

            // Iteramos sobre la lista de subcategorías que devuelve el backend
            data.forEach(subcategoria => {
                // Crear el label
                const label = document.createElement('label');
                label.setAttribute('for', subcategoria.nombre);
                label.innerText = subcategoria.nombre;

                // Crear el input radio
                const input = document.createElement('input');
                input.type = 'radio';
                input.id = subcategoria.nombre;
                input.name = 'subcategoriaId';
                input.value = subcategoria.id;
                input.dataset.unidad = subcategoria.unidadDeMedida;
                input.required = true;


                input.addEventListener('change', cargarCantidadNecesitada);
                // Insertarlos en el HTML
                contenedorSubcat.appendChild(label);
                contenedorSubcat.appendChild(input);
            });
        })
        .catch(error => {
            console.error('Error al cargar subcategorías:', error);
            contenedorSubcat.innerHTML = '<p>Ocurrió un error al cargar.</p>';
        });
}
const magnitudes = [
    {
        nombre:"longitud",
        unidadPrincipal:"M",
        unidades:[
            {nombre:"M", funcion:(x) => x},
            {nombre:"CM", funcion:(x) => x/100}
        ]
    },
    {
        nombre:"peso",
        unidadPrincipal:"KG",
        unidades:[
            {nombre:"KG", funcion:(x) => x}
        ]
    },
    {
        nombre:"unidades",
        unidadPrincipal:"UNIDADES",
        unidades:[
            {nombre:"UNIDADES", funcion:(x) => x}
        ]
    },
    {
        nombre:"volumen",
        unidadPrincipal:"L",
        unidades:[
            {nombre:"M3", funcion:(x) => x*1000},
            {nombre:"L", funcion:(x) => x}
        ]
    }
]

function cargarOpcionesDeUnidades(){
    const opcionSeleccionada = document.querySelector('input[name="subcategoriaId"]:checked');
    console.log(opcionSeleccionada);
    const unidadSeleccionada = opcionSeleccionada.dataset.unidad;
    const unidades = document.getElementById('unidad');
    const unidadPrevia = unidades.dataset.unidadPrevia;
    console.log("unidad seleccionada " +unidadSeleccionada);
    const unidadesRelacionadas = magnitudes.find(m=>m.unidadPrincipal===unidadSeleccionada).unidades;
    console.log(unidadesRelacionadas);
    unidades.replaceChildren();
    unidadesRelacionadas.forEach(u => {
        const option = document.createElement('option');
        option.setAttribute('value', u.nombre);
        if (u.nombre === unidadPrevia) {
            option.selected = true;
        }
        option.innerText = u.nombre;
        unidades.appendChild(option);
    })
}
function cargarCantidadNecesitada() {
    const cantidad = document.getElementById('cantidad').value;
    const unidad = document.getElementById('unidad').value;
    if (cantidad == null || unidad == null){
        return;
    }
    const inputReal = document.getElementById('cantidad-necesitada');
    console.log(magnitudes.flatMap(m=>m.unidades).find(u=>u.nombre === unidad).funcion(cantidad));
    inputReal.value = magnitudes.flatMap(m=>m.unidades).find(u=>u.nombre === unidad).funcion(cantidad);
}