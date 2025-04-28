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
  
    // Mostrar la información
    document.getElementById('nombre-clase').innerText = claseSeleccionada.nombre;
    document.getElementById('descripcion-clase').innerText = claseSeleccionada.descripcion;
  
    // Opcional: podrías mostrar más detalles si en tu clases.js tienes niveles, horarios, etc.
  
    // Cambiar el enlace del botón de inscripción para que pase el id
    document.getElementById('btn-inscribirse').href = `registro.html?id=${claseSeleccionada.id}`;
  });
  