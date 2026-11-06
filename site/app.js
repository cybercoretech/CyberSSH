const menuButton = document.querySelector('.menu-toggle');
const menu = document.querySelector('#site-menu');

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

const year = document.querySelector('#year');
if (year) year.textContent = new Date().getFullYear();

document.querySelectorAll('[data-copy-target]').forEach((button) => {
  button.addEventListener('click', async () => {
    const command = document.querySelector(`#${button.dataset.copyTarget}`)?.textContent?.trim();
    if (!command) return;
    try {
      await navigator.clipboard.writeText(command);
    } catch {
      const temporary = document.createElement('textarea');
      temporary.value = command;
      temporary.style.position = 'fixed';
      temporary.style.opacity = '0';
      document.body.append(temporary);
      temporary.select();
      document.execCommand('copy');
      temporary.remove();
    }
    button.textContent = 'COPIED';
    window.setTimeout(() => { button.textContent = 'COPY'; }, 1600);
  });
});
