document.addEventListener('DOMContentLoaded', function() {
    const params = new URLSearchParams(window.location.search);
    const idClase = params.get('id');
  
    if (!idClase) {
      document.getElementById('nombre-clase').innerText = 'Clase no encontrada.';
      return;
    }
  
    const claseSeleccionada = clases.find(c => c.id == idClase);
  
    if (!claseSeleccionada) {
      document.getElementById('nombre-clase').innerText = 'Clase no encontrada.';
      return;
    }
  
    // Mostrar nombre y descripción
    document.getElementById('nombre-clase').innerText = claseSeleccionada.nombre;
    document.getElementById('descripcion-clase').innerText = claseSeleccionada.descripcion;
  
    // Mostrar niveles
    const contenedorNiveles = document.getElementById('niveles-clase');
    contenedorNiveles.innerHTML = '';
  
    claseSeleccionada.niveles.forEach((nivel, index) => {
      const nivelCard = document.createElement('div');
      nivelCard.classList.add('card-clase');
      nivelCard.innerHTML = `
        <h3>${nivel.nombre}</h3>
        <p><strong>Horario:</strong> ${nivel.horario}</p>
        <p><strong>Precio:</strong> $${nivel.precio}</p>
        <a href="registro.html?id=${claseSeleccionada.id}&nivel=${index}" class="btn-primario">Inscribirse</a>
      `;
      contenedorNiveles.appendChild(nivelCard);
    });
  });
  