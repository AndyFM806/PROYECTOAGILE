document.addEventListener('DOMContentLoaded', function() {
    const contenedor = document.getElementById('lista-clases');
  
    clases.forEach(clase => {
      const card = document.createElement('div');
      card.classList.add('card-clase');
      card.innerHTML = `
        <h3>${clase.nombre}</h3>
        <p>${clase.descripcion}</p>
        <a href="detalle_clase.html?id=${clase.id}" class="btn-secundario">Ver detalles</a>
      `;
      contenedor.appendChild(card);
    });
  });
  