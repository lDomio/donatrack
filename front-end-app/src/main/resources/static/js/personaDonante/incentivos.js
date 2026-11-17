document.addEventListener('DOMContentLoaded', () => {
    inicializarFiltrosMisiones();
});

function inicializarFiltrosMisiones() {
    const pestanas = document.querySelectorAll('.pestanaCategoriaMision');
    const buscador = document.getElementById('buscadorMisiones');
    const tarjetas = document.querySelectorAll('#listaMisiones .tarjetaMision');
    if (tarjetas.length === 0) return;

    let categoriaActiva = 'Todas';

    function aplicarFiltros() {
        const texto = (buscador?.value || '').trim().toLowerCase();

        tarjetas.forEach((tarjeta) => {
            const coincideCategoria = categoriaActiva === 'Todas' || tarjeta.dataset.categoria === categoriaActiva;
            const coincideTexto = texto === '' || tarjeta.textContent.toLowerCase().includes(texto);
            tarjeta.hidden = !(coincideCategoria && coincideTexto);
        });
    }

    pestanas.forEach((pestana) => {
        pestana.addEventListener('click', () => {
            pestanas.forEach((p) => p.classList.remove('activa'));
            pestana.classList.add('activa');
            categoriaActiva = pestana.dataset.categoria;
            aplicarFiltros();
        });
    });

    buscador?.addEventListener('input', aplicarFiltros);
}
