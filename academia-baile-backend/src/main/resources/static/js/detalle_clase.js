document.addEventListener('DOMContentLoaded', function () {
  const params = new URLSearchParams(window.location.search);
  const claseId = params.get('id');

  if (!claseId) {
    console.error("ID de clase no definido en la URL");
    return;
  }

  // Obtener info general de la clase
  fetch(`http://localhost:8080/api/clases`)
    .then(response => response.json())
    .then(clases => {
      const claseSeleccionada = clases.find(clase => clase.id === parseInt(claseId));
      if (!claseSeleccionada) {
        console.error("Clase no encontrada");
        return;
      }

      document.getElementById('nombre-clase').innerText = claseSeleccionada.nombre;
      document.getElementById('descripcion-clase').innerText = claseSeleccionada.descripcion;
    })
    .catch(error => console.error("Error cargando la clase:", error));

  // Obtener niveles desde clase_nivel (ya relacionado con horario y nivel)
  fetch(`http://localhost:8080/api/clases/${claseId}/niveles`)
    .then(response => response.json())
    .then(niveles => {
      const contenedor = document.getElementById('niveles-clase');
      contenedor.innerHTML = "";

      niveles.forEach(nivel => {
        const card = document.createElement('div');
        card.classList.add('card-clase');

        card.innerHTML = `
          <h3>${nivel.nivel.nombre}</h3>
          <p><strong>Horario:</strong> ${nivel.horario.dias} - ${nivel.horario.hora}</p>
          <p><strong>Precio:</strong> S/${nivel.precio}</p>
          <a href="registro.html?id=${claseId}&nivel=${nivel.nivel.nombre}&precio=${nivel.precio}" class="btn-primario">Inscribirme</a>
        `;
        contenedor.appendChild(card);
      });
    })
    .catch(error => console.error("Error cargando los niveles:", error));
});
