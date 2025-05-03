if (localStorage.getItem("adminAutenticado") !== "true") {
    alert("Acceso denegado");
    window.location.href = "login_admin.html";
  }

  fetch("https://proyectoagile.onrender.com/api/clientes")
    .then(res => res.json())
    .then(clientes => {
      const contenedorSolicitudes = document.getElementById("solicitudes");
      const contenedorRegistrados = document.getElementById("registrados");

      if (clientes.length === 0) {
        contenedorSolicitudes.innerHTML = "<p>No hay solicitudes.</p>";
        contenedorRegistrados.innerHTML = "<p>No hay alumnos registrados.</p>";
        return;
      }

      clientes.forEach(cliente => {
        const div = document.createElement("div");
        div.classList.add("card-cliente");
        div.innerHTML = `
          <h4>${cliente.nombreCompleto}</h4>
          <p><strong>Clase:</strong> ${cliente.clase?.nombre || 'N/A'}</p>
          <p><strong>Nivel:</strong> ${cliente.nivel}</p>
          <p><strong>Correo:</strong> ${cliente.correo}</p>
          <p><strong>Teléfono:</strong> ${cliente.telefono}</p>
          <p><strong>Estado de pago:</strong> ${cliente.estadoPago}</p>
        `;

        if (cliente.estadoPago.toLowerCase() === "pendiente") {
          const btn = document.createElement("button");
          btn.textContent = "Aceptar Pago";
          btn.onclick = () => {
            fetch(`https://proyectoagile.onrender.com/api/clientes/${cliente.id}/aceptar`, {
              method: "PUT"
            }).then(() => location.reload());
          };
          div.appendChild(btn);
          contenedorSolicitudes.appendChild(div);
        } else {
          contenedorRegistrados.appendChild(div);
        }
      });
    });
    document.getElementById("btn-cerrar-sesion").addEventListener("click", function () {
      localStorage.removeItem("adminAutenticado");
      window.location.href = "principal.html";
    });
    