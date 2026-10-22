const menuButton = document.querySelector('.menu-toggle');
const menu = document.querySelector('#site-menu');
const themeSelect = document.querySelector('#theme-select');

menuButton.addEventListener('click', () => {
  const opening = menu.hidden;
  menu.hidden = !opening;
  menuButton.setAttribute('aria-expanded', String(opening));
  menuButton.setAttribute('aria-label', opening ? 'Close site menu' : 'Open site menu');
});

menu.addEventListener('click', (event) => {
  if (event.target.closest('a')) {
    menu.hidden = true;
    menuButton.setAttribute('aria-expanded', 'false');
  }
});

let savedTheme = 'blacksite';
try {
  savedTheme = localStorage.getItem('cyberssh-theme') || savedTheme;
} catch {
  // Theme switching still works for the current visit when storage is disabled.
}
document.body.dataset.theme = savedTheme;
themeSelect.value = savedTheme;
themeSelect.addEventListener('change', () => {
  document.body.dataset.theme = themeSelect.value;
  try {
    localStorage.setItem('cyberssh-theme', themeSelect.value);
  } catch {
    // No persistence when browser storage is unavailable.
  }
});

document.querySelector('#year').textContent = new Date().getFullYear();
