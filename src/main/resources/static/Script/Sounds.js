const sounds = {
    click: './Asset/ClickSFX.mp3',
    hoverSound: './Asset/HoverSFX.mp3',
};

document.addEventListener('DOMContentLoaded', () => {
    const sfxVolumeSlider = document.getElementById('sfxVolume');
    const sfxMute = document.getElementById('sfxMute');
    let lastSfxVolume = 1;

    const loadSettings = () => {
        if (sfxVolumeSlider) {
            const savedVolume = parseFloat(localStorage.getItem('sfxVolume'));
            if (!isNaN(savedVolume)) {
                sfxVolumeSlider.value = savedVolume;
                lastSfxVolume = savedVolume;
            } else {
                sfxVolumeSlider.value = 1;
            }
        } else {
            lastSfxVolume = parseFloat(localStorage.getItem('sfxVolume')) || 1;
        }

        if (sfxMute) {
            const sfxMuted = localStorage.getItem('sfxMuted') === 'true';
            sfxMute.checked = sfxMuted;
        }
    };

    loadSettings();

    if (sfxVolumeSlider) {
        sfxVolumeSlider.addEventListener('input', (e) => {
            const value = parseFloat(e.target.value);
            if (!isNaN(value)) {
                lastSfxVolume = value;
                localStorage.setItem('sfxVolume', value);
                localStorage.setItem('sfxMuted', false);
                if (sfxMute) {
                    sfxMute.checked = false;
                }
            }
        });
    }

    if (sfxMute) {
        sfxMute.addEventListener('change', (e) => {
            localStorage.setItem('sfxMuted', e.target.checked);
            if (!e.target.checked && lastSfxVolume) {
                localStorage.setItem('sfxVolume', lastSfxVolume);
            }
        });
    }

    window.playSFX = (src) => {
        if (localStorage.getItem('sfxMuted') === 'true') return;
        const sfx = new Audio(src);
        const sfxVol = parseFloat(localStorage.getItem('sfxVolume'));
        sfx.volume = !isNaN(sfxVol) ? sfxVol : lastSfxVolume || 1;
        sfx.play();
    };

    window.playSFXByName = (name) => {
        if (sounds[name]) {
            playSFX(sounds[name]);
        } else {
            console.warn(`Sound "${name}" not found`);
        }
    };

    document.querySelectorAll('button').forEach(button => {
        button.addEventListener('mouseenter', () => {
            playSFX(sounds.hoverSound);
        });
    });
});
