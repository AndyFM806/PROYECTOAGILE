document.addEventListener('DOMContentLoaded', function() {
    function mostrarPaso(paso) {
      document.querySelectorAll('.step').forEach(div => div.classList.remove('active'));
  
      if (paso === 'pago') {
        document.getElementById('pago').classList.add('active');
        document.getElementById('step1').classList.remove('active');
        document.getElementById('step2').classList.add('active');
      }
      if (paso === 'confirmacion') {
        document.getElementById('confirmacion').classList.add('active');
        document.getElementById('step2').classList.remove('active');
        document.getElementById('step3').classList.add('active');
      }
    }
  
    // Exponer la función al scope global para que los botones puedan llamarla
    window.mostrarPaso = mostrarPaso;
  });
  