document.addEventListener('DOMContentLoaded', () => {
    // --- Lógica del modo oscuro ---

    const themeToggle = document.getElementById('themeToggle');
    const currentTheme = localStorage.getItem('theme') || 'light';

    // Aplicar el tema guardado al cargar
    if (currentTheme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    }

    if (themeToggle) {
        themeToggle.addEventListener('click', () => {
            let theme = document.documentElement.getAttribute('data-theme');
            if (theme === 'dark') {
                document.documentElement.setAttribute('data-theme', 'light');
                localStorage.setItem('theme', 'light');
            } else {
                document.documentElement.setAttribute('data-theme', 'dark');
                localStorage.setItem('theme', 'dark');
            }
        });
    }

    // --- Lógica del Menú Móvil ---
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    const navLinks = document.querySelectorAll('.side-nav a');

    // Función para alternar el menú
    const toggleMenu = () => {
        if (!sidebar || !overlay) return;
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
