document.addEventListener('DOMContentLoaded', () => {
    const SYSTEM_VERSION = 'v2.4.0';

    // --- Control de Versión Centralizado ---
    const setupVersion = () => {
        const versionTags = document.querySelectorAll('.version-tag');
        versionTags.forEach(tag => {
            tag.textContent = SYSTEM_VERSION;
        });
    };
    // --- Lógica del modo oscuro ---
    const setupDarkMode = () => {
        const themeToggle = document.getElementById('themeToggle');
        if (!themeToggle) return;

        const currentTheme = localStorage.getItem('theme') || 'light';
        if (currentTheme === 'dark') document.documentElement.setAttribute('data-theme', 'dark');

        themeToggle.addEventListener('click', () => {
            let theme = document.documentElement.getAttribute('data-theme');
            const newTheme = theme === 'dark' ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
        });
    };

    // --- Lógica del Menú Móvil ---
    const setupMobileMenu = () => {
        const menuToggle = document.getElementById('menuToggle');
        const sidebar = document.querySelector('.sidebar');
        const overlay = document.querySelector('.sidebar-overlay');
        const navLinks = document.querySelectorAll('.side-nav a');

        if (!menuToggle || !sidebar || !overlay) return;

        const toggleMenu = () => {
            sidebar.classList.toggle('active');
            overlay.classList.toggle('active');
            document.body.style.overflow = sidebar.classList.contains('active') ? 'hidden' : '';
        };

        menuToggle.addEventListener('click', toggleMenu);
        overlay.addEventListener('click', toggleMenu);

        navLinks.forEach(link => {
            link.addEventListener('click', () => {
                if (window.innerWidth <= 768) toggleMenu();
            });
        });

        window.addEventListener('resize', () => {
            if (window.innerWidth > 768 && sidebar.classList.contains('active')) toggleMenu();
        });
    };
    // FUNCIONALIDAD DE BARRA DE BUSQUEDA 
    const buscador = document.getElementById('buscador');

    buscador.addEventListener('input', function () {
    const query = this.value.toLowerCase().trim();

    const links = document.querySelectorAll('.side-nav li');

    let found = false;

    links.forEach(li => {
        const text = li.textContent.toLowerCase();

        if (query === '') {
        li.style.display = '';
        } else if (text.includes(query)) {
        li.style.display = '';
        found = true;
        } else {
        li.style.display = 'none';
        }
    });

    const grupos = document.querySelectorAll('.nav-group');
    grupos.forEach(grupo => {
        const visibles = grupo.querySelectorAll('li:not([style*="none"])');
        grupo.style.display = (query !== '' && visibles.length === 0) ? 'none' : '';
    });

    let noResults = document.getElementById('noResults');
    if (!noResults) {
        noResults = document.createElement('p');
        noResults.id = 'noResults';
        noResults.textContent = 'No se encontraron resultados.';
        noResults.style.cssText = 'color: red; font-size: 0.85rem; padding: 8px 12px;';
        document.querySelector('.barra-busqueda').insertAdjacentElement('afterend', noResults);
    }
    noResults.style.display = (!found && query !== '') ? 'block' : 'none';
    });

    // --- ZOOM IMÁGENES (Resiliente a elementos faltantes) ---
    const setupImageZoom = () => {
        let modal = document.getElementById("imageModal");
        if (!modal) {
            modal = document.createElement('div');
            modal.id = 'imageModal';
            modal.className = 'image-modal';
            modal.innerHTML = `
                <span class="close-modal">&times;</span>
                <img id="modalImage" src="" alt="">
                <p id="modalCaption"></p>
            `;
            document.body.appendChild(modal);
        }

        const modalImg = document.getElementById("modalImage");
        const captionText = document.getElementById("modalCaption");
        const closeModal = modal.querySelector(".close-modal");

        if (!modalImg || !closeModal) return;

        document.querySelectorAll(".zoom-img").forEach(img => {
            img.addEventListener("click", () => {
                modal.style.display = "flex";
                modalImg.src = img.src;
                captionText.textContent = img.alt;
            });
        });

        closeModal.addEventListener("click", () => modal.style.display = "none");
        modal.addEventListener("click", (e) => {
            if (e.target === modal) modal.style.display = "none";
        });
    };

    // --- MEJORAS DINÁMICAS (TOC, Breadcrumbs, Progress Bar) ---

    const setupProgressBar = () => {
        if (document.querySelector('.reading-progress-container')) return;

        const container = document.createElement('div');
        container.className = 'reading-progress-container';
        const bar = document.createElement('div');
        bar.className = 'reading-progress-bar';
        container.appendChild(bar);
        document.body.prepend(container);

        container.addEventListener('click', () => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });

        window.addEventListener('scroll', () => {
            const winScroll = window.scrollY || document.documentElement.scrollTop;
            const height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
            const scrolled = Math.round((winScroll / height) * 100);
            bar.style.width = scrolled + "%";
            container.setAttribute('data-pct', scrolled);
            if (scrolled > 2) container.classList.add('visible');
            else container.classList.remove('visible');
        });
    };

    const setupDynamicLayout = () => {
        const content = document.querySelector('.content');
        if (!content) return;

        // 1. Inyectar Breadcrumbs
        if (!content.querySelector('.breadcrumbs')) {
            const breadcrumbsNav = document.createElement('nav');
            breadcrumbsNav.className = 'breadcrumbs';
            content.prepend(breadcrumbsNav);
            generateBreadcrumbs(breadcrumbsNav);
        }

        // 2. Inyectar Estructura para TOC
        if (content.querySelector('.content-wrapper')) return;

        const childrenArray = Array.from(content.children);
        const originalContent = childrenArray.filter(child =>
            !child.classList.contains('breadcrumbs') &&
            !child.classList.contains('sidebar-overlay') &&
            !child.classList.contains('image-modal') &&
            !child.classList.contains('reading-progress-container')
        );

        if (originalContent.length === 0) return;

        const wrapper = document.createElement('div');
        wrapper.className = 'content-wrapper';
        const article = document.createElement('article');
        article.className = 'main-article';
        const tocSidebar = document.createElement('aside');
        tocSidebar.className = 'toc-sidebar';
        tocSidebar.innerHTML = `
            <div class="toc-sticky">
                <h4>Navegación</h4>
                <nav id="toc"></nav>
            </div>
        `;

        originalContent.forEach(child => article.appendChild(child));
        wrapper.appendChild(article);
        wrapper.appendChild(tocSidebar);
        content.appendChild(wrapper);

        generateTOC(article, tocSidebar.querySelector('#toc'));
        initScrollSpy();
    };

    const generateBreadcrumbs = (container) => {
        const path = window.location.pathname;
        const fileName = path.split(/[/\\]/).pop() || 'index.html';
        const isIndex = fileName.toLowerCase() === 'index.html' || fileName === '';

        const items = [];
        items.push({
            label: 'Inicio',
            url: isIndex ? '#' : (path.includes('/pages/') ? '../index.html' : 'index.html')
        });

        const activeLink = document.querySelector('.side-nav a.active') ||
            Array.from(document.querySelectorAll('.side-nav a'))
                .find(a => a.getAttribute('href').includes(fileName));

        if (activeLink && !isIndex) {
            const group = activeLink.closest('.nav-group');
            if (group) {
                const groupTitle = group.querySelector('h3').textContent.trim();
                items.push({ label: groupTitle, url: '#' });
            }
            items.push({ label: activeLink.textContent.trim(), current: true });
        } else if (isIndex) {
            items[0].current = true;
        }

        container.innerHTML = items.map(item => {
            if (item.current) return `<span class="current">${item.label}</span>`;
            return `<a href="${item.url}">${item.label}</a><span class="separator">/</span>`;
        }).join('');
    };

    const generateTOC = (source, target) => {
        const headers = Array.from(source.querySelectorAll('h1, h2, h3'));
        if (headers.length === 0) {
            target.innerHTML = '<p style="font-size: 0.8rem; color: var(--color-text-muted);">Sin subsecciones</p>';
            return;
        }

        const tocList = document.createElement('ul');
        headers.forEach((header, index) => {
            if (!header.id) header.id = 'section-' + index;
            const li = document.createElement('li');
            const a = document.createElement('a');
            a.href = `#${header.id}`;
            a.textContent = header.textContent.trim();
            a.className = `toc-item toc-${header.tagName.toLowerCase()}`;
            a.addEventListener('click', (e) => {
                e.preventDefault();
                const targetEl = document.getElementById(header.id);
                if (targetEl) {
                    const targetPosition = targetEl.getBoundingClientRect().top + window.scrollY;
                    window.scrollTo({ top: targetPosition - 100, behavior: "smooth" });
                    history.pushState(null, null, `#${header.id}`);
                    
                    // Lógica para resaltar el elemento destino
                    const highlightTarget = targetEl.closest('.team-card, .glosario-card, .diagrama_er_container, .diagrama_mer_container, table') || targetEl;
                    
                    // Remover la clase si ya existe para reiniciar la animación
                    highlightTarget.classList.remove('flash-highlight');
                    
                    // Forzar reflow para reiniciar la animación CSS
                    void highlightTarget.offsetWidth;
                    
                    highlightTarget.classList.add('flash-highlight');
                }
            });
            li.appendChild(a);
            tocList.appendChild(li);
        });
        target.appendChild(tocList);
    };

    const initScrollSpy = () => {
        const headers = document.querySelectorAll('h1, h2, h3');
        if (headers.length === 0) return;
        const options = { rootMargin: '-10% 0px -80% 0px', threshold: 0 };
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                const id = entry.target.getAttribute('id');
                const tocLink = document.querySelector(`.toc-sidebar nav a[href="#${id}"]`);
                if (entry.isIntersecting && tocLink) {
                    document.querySelectorAll('.toc-sidebar nav a').forEach(a => a.classList.remove('active'));
                    tocLink.classList.add('active');
                }
            });
        }, options);
        headers.forEach(h => observer.observe(h));
    };

    // --- Contadores animados ---
    const setupCounters = () => {
        const counters = document.querySelectorAll('.stat-num[data-target]');
        if (counters.length === 0) return;

        const animate = (el) => {
            const target = parseInt(el.getAttribute('data-target'), 10);
            const duration = 1200;
            const step = Math.ceil(duration / target);
            let current = 0;
            const timer = setInterval(() => {
                current++;
                el.textContent = current;
                if (current >= target) {
                    el.textContent = target;
                    clearInterval(timer);
                }
            }, step);
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.target.textContent === '0') {
                    animate(entry.target);
                }
            });
        }, { threshold: 0.5 });

        counters.forEach(c => observer.observe(c));
    };

    // --- Scroll reveal ---
    const setupScrollReveal = () => {
        const elements = document.querySelectorAll('.reveal');
        if (elements.length === 0) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry, i) => {
                if (entry.isIntersecting) {
                    setTimeout(() => entry.target.classList.add('visible'), i * 80);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1 });

        elements.forEach(el => observer.observe(el));
    };

    // --- INICIALIZACIÓN SEGURA ---
    setupVersion();
    setupDarkMode();
    setupMobileMenu();
    setupImageZoom();
    setupProgressBar();
    setupDynamicLayout();
    setupCounters();
    setupScrollReveal();
});
