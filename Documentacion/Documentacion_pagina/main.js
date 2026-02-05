/* 
    LÓGICA INTERACTIVA
    Manejo del menú móvil y otras interacciones de la UI.
*/

document.addEventListener('DOMContentLoaded', () => {
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    const navLinks = document.querySelectorAll('.side-nav a');

    // Función para alternar el menú
    const toggleMenu = () => {
        sidebar.classList.toggle('active');
        overlay.classList.toggle('active');
        // Bloquear scroll del body cuando el menú está abierto
        document.body.style.overflow = sidebar.classList.contains('active') ? 'hidden' : '';
    };

    // Eventos
    if (menuToggle) {
        menuToggle.addEventListener('click', toggleMenu);
    }

    if (overlay) {
        overlay.addEventListener('click', toggleMenu);
    }

    // Cerrar menú al hacer clic en un enlace (importante en móviles)
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (window.innerWidth <= 768) {
                toggleMenu();
            }
        });
    });

    // Cerrar menú si la pantalla se redimensiona por encima del límite
    window.addEventListener('resize', () => {
        if (window.innerWidth > 768 && sidebar.classList.contains('active')) {
            toggleMenu();
        }
    });
});
