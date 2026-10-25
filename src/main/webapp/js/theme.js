(function () {
    'use strict';
    const root = document.documentElement;
    const toggle = document.getElementById('theme-toggle');
    const menu = document.getElementById('mobile-menu-button');
    const nav = document.getElementById('site-navigation');
    function setTheme(theme) {
        root.setAttribute('data-theme', theme);
        if (toggle) toggle.setAttribute('aria-label', theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
        try { localStorage.setItem('YummyCart-theme', theme); } catch (e) { /* disabled storage */ }
    }
    setTheme(root.getAttribute('data-theme') === 'light' ? 'light' : 'dark');
    if (toggle) toggle.addEventListener('click', () => setTheme(root.getAttribute('data-theme') === 'dark' ? 'light' : 'dark'));
    if (nav && menu) {
        menu.addEventListener('click', function () {
            const open = nav.classList.toggle('is-open');
            menu.setAttribute('aria-expanded', String(open));
            menu.setAttribute('aria-label', open ? 'Close navigation' : 'Open navigation');
        });
        nav.addEventListener('click', function (e) {
            if (e.target.closest('a')) { nav.classList.remove('is-open'); menu.setAttribute('aria-expanded', 'false'); }
        });
    }
    const context = document.querySelector('.site-brand');
    if (!context || !nav) return;
    let prefix = new URL(context.href, window.location.href).pathname;
    const path = window.location.pathname.slice(prefix.length).replace(/^\/+/, '');
    let key = path.startsWith('admin/') ? 'admin' : path.startsWith('restaurants') ? 'restaurants' : path.startsWith('food') ? 'food' : path.startsWith('orders') ? 'orders' : path.startsWith('delivery') ? 'delivery' : path.startsWith('reviews') ? 'reviews' : 'home';
    nav.querySelectorAll('[data-nav]').forEach(a => {
        const active = a.getAttribute('data-nav') === key;
        a.classList.toggle('is-active', active);
        if (active) a.setAttribute('aria-current', 'page');
    });
}());
