document.addEventListener('DOMContentLoaded', () => {
  const contenedor = document.getElementById('lista-clases');

  fetch('http://localhost:8081/api/clases') // asegúrate que el puerto es correcto
    .then(response => response.json())
    .then(clases => {
      if (clases.length === 0) {
        contenedor.innerHTML = "<p>No hay clases disponibles por ahora.</p>";
        return;
      }

      clases.forEach(clase => {
        const card = document.createElement('div');
        card.classList.add('card-clase');

        // asegúrate de que los nombres de los campos coinciden EXACTAMENTE
        card.innerHTML = `
          <h3>${clase.nombre}</h3>
          <p>${clase.descripcion}</p>
          <a href="detalle_clase.html?id=${clase.id}" class="btn-secundario">Ver detalles</a>
        `;

        contenedor.appendChild(card);
      });
    })
    .catch(error => {
      contenedor.innerHTML = "<p>Error al cargar clases. Intenta más tarde.</p>";
      console.error('Error al obtener clases:', error);
    });
});
