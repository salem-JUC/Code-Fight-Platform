(() => {
    const STORAGE_KEY = 'authenticatedUser';
    const LOGIN_PAGE = 'Login.html';

    let cachedSpan = null;

    const findUserNameSpan = () => {
        if (cachedSpan) {
            return cachedSpan;
        }
        cachedSpan = window.userNameSpan || document.getElementById('userNameSpan') || null;
        if (cachedSpan) {
            window.userNameSpan = cachedSpan;
        }
        return cachedSpan;
    };

    const setUserNameSpan = (user) => {
        const span = findUserNameSpan();
        if (!span) {
            return;
        }
        span.innerText = user?.username ?? '';
    };

    const getUserFromStorage = () => {
        const raw = sessionStorage.getItem(STORAGE_KEY);
        if (!raw) {
            return null;
        }
        try {
            return JSON.parse(raw);
        } catch (error) {
            console.warn('Failed to parse cached user, clearing storage.', error);
            sessionStorage.removeItem(STORAGE_KEY);
            return null;
        }
    };

    const setUserInStorage = (user) => {
        if (user) {
            sessionStorage.setItem(STORAGE_KEY, JSON.stringify(user));
            window.authenticatedUser = user;
        } else {
            sessionStorage.removeItem(STORAGE_KEY);
            window.authenticatedUser = null;
        }
    };

    const redirectToLogin = () => {
        window.location.href = LOGIN_PAGE;
    };

    const ensureUser = async () => {
        const cached = getUserFromStorage();
        if (cached) {
            setUserNameSpan(cached);
            return cached;
        }

        const response = await fetch('/api/auth/me', {
            credentials: 'include'
        });

        if (response.status !== 200) {
            redirectToLogin();
            throw new Error(`Unauthenticated (status ${response.status})`);
        }

        const user = await response.json();
        setUserInStorage(user);
        setUserNameSpan(user);
        return user;
    };

    const ensureUserOnce = () => {
        if (!window.__authGuardReady) {
            window.__authGuardReady = ensureUser().catch((error) => {
                console.warn('Authentication guard failed', error);
                return null;
            });
        }
        return window.__authGuardReady;
    };

    const clearCachedUser = () => {
        setUserInStorage(null);
        const span = findUserNameSpan();
        if (span) {
            span.innerText = '';
        }
    };

    // Start the guard immediately so that the first fetch is kicked off
    ensureUserOnce();

    window.getCachedUser = getUserFromStorage;
    window.ensureAuthenticatedUser = ensureUserOnce;
    window.authGuard = {
        ensureAuthenticatedUser: ensureUserOnce,
        getCachedUser: getUserFromStorage,
        setCachedUser: (user) => {
            setUserInStorage(user);
            setUserNameSpan(user);
        },
        clearCachedUser,
        storageKey: STORAGE_KEY,
        loginPage: LOGIN_PAGE
    };
})();

