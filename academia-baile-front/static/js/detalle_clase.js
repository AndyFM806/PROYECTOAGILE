document.addEventListener('DOMContentLoaded', function () {
  const params = new URLSearchParams(window.location.search);
  const claseId = params.get('id');

  if (!claseId) {
    console.error("ID de clase no definido en la URL");
    return;
  }

  // Obtener info general de la clase
  fetch(`https://proyectoagile.onrender.com/api/clases`)
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

  // Obtener niveles desde clase_nivel (DTO plano)
  fetch(`https://proyectoagile.onrender.com/api/clases/${claseId}/niveles`)
    .then(response => response.json())
    .then(niveles => {
      const contenedor = document.getElementById('niveles-clase');
      contenedor.innerHTML = "";

      niveles.forEach(nivel => {
        const card = document.createElement('div');
        card.classList.add('card-clase');

        card.innerHTML = `
          <h3>${nivel.nivel}</h3>
          <p><strong>Horario:</strong> ${nivel.dias} - ${nivel.hora}</p>
          <p><strong>Precio:</strong> S/${nivel.precio}</p>
          <a href="registro.html?id=${claseId}&nivel=${nivel.nivel}&precio=${nivel.precio}" class="btn-primario">Inscribirme</a>
        `;
        contenedor.appendChild(card);
      });

      console.log("Respuesta del backend:", niveles);
    })
    .catch(error => console.error("Error cargando los niveles:", error));
});
