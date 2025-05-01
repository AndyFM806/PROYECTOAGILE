document.addEventListener('DOMContentLoaded', () => {
  const params = new URLSearchParams(window.location.search);
  const claseId = params.get('id');
  const nivel = params.get('nivel');
  const precio = params.get('precio');

  document.getElementById('clase-seleccionada').value = `Nivel: ${nivel} | Precio: S/${precio}`;

  document.getElementById('form-registro').addEventListener('submit', function (e) {
    e.preventDefault();

    const cliente = {
      nombreCompleto: document.querySelector('[name="nombre"]').value,
      telefono: document.querySelector('[name="telefono"]').value,
      correo: document.querySelector('[name="correo"]').value,
      direccion: document.querySelector('[name="direccion"]').value,
      nivel: nivel,
      estadoPago: "pendiente",
      clase: {
        id_clase: parseInt(claseId)
      }
    };

    fetch('http://localhost:8080/api/clientes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cliente)
    })
    .then(response => {
      if (!response.ok) throw new Error("Error al registrar");
      return response.json();
    })
    .then(data => {
      mostrarPaso('confirmacion');
    })
    .catch(error => {
      console.error('Error:', error);
      alert('Hubo un error al registrar. Revisa consola.');
    });
  });
});

function mostrarPaso(idPaso) {
  document.querySelectorAll('.step').forEach(p => p.classList.remove('active'));
  document.getElementById('step3').classList.add('active');
  document.getElementById(idPaso).classList.add('active');
}
