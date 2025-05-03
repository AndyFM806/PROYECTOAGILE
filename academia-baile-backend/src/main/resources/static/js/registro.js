let inscripcionId = null;

function mostrarPaso(idPaso) {
  document.querySelectorAll('.step').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.progress-bar div').forEach(p => p.classList.remove('active'));
  document.getElementById(idPaso).classList.add('active');

  if (idPaso === 'datos-personales') document.getElementById('step1').classList.add('active');
  else if (idPaso === 'pago') document.getElementById('step2').classList.add('active');
  else if (idPaso === 'confirmacion') document.getElementById('step3').classList.add('active');
}

document.addEventListener('DOMContentLoaded', () => {
  const params = new URLSearchParams(window.location.search);
  const claseNivelId = params.get('id');
  const nivel = params.get('nivel');
  const precio = params.get('precio');

  document.getElementById('clase-seleccionada').value = `Nivel: ${nivel || 'N/A'} | Precio: S/${precio || 'N/A'}`;

  window.registrarPaso1 = () => {
    const nombres = document.getElementById("nombres").value.trim();
    const apellidos = document.getElementById("apellidos").value.trim();
    const correo = document.getElementById("correo").value.trim();
    const direccion = document.getElementById("direccion").value.trim();
    const dni = document.getElementById("dni").value.trim();

    const inscripcionDTO = {
      nombres,
      apellidos,
      correo,
      direccion,
      dni,
      claseNivelId: parseInt(claseNivelId),
      estado: "pendiente"
    };

    console.log("DTO enviado:", inscripcionDTO);

    fetch('https://proyectoagile.onrender.com/api/inscripciones', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(inscripcionDTO)
    })
    .then(res => {
      if (!res.ok) throw new Error("No se pudo registrar la inscripción.");
      return res.json();
    })
    .then(data => {
      inscripcionId = data;
      mostrarPaso('pago');
    })
    .catch(err => {
      alert("Error al registrar inscripción.");
      console.error(err);
    });
  };

  window.subirComprobante = () => {
    const archivo = document.getElementById('comprobante').files[0];
    if (!archivo || !inscripcionId) {
      alert("Debes registrar tus datos y subir el comprobante JPG.");
      return;
    }

    const formData = new FormData();
    formData.append("file", archivo);

    fetch(`https://proyectoagile.onrender.com/api/inscripciones/comprobante/${inscripcionId}`, {
      method: 'POST',
      body: formData
    })
    .then(res => {
      if (!res.ok) throw new Error("No se pudo subir el comprobante.");
      mostrarPaso('confirmacion');
    })
    .catch(err => {
      alert("Error al subir el comprobante.");
      console.error(err);
    });
  };
});
