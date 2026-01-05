async function getGuardedUser() {
    const cached = window.getCachedUser?.();
    if (cached) {
        return cached;
    }
    if (window.ensureAuthenticatedUser) {
        return await window.ensureAuthenticatedUser();
    }
    return null;
}
 
async function getUserStatistics() {
    try {
        const response = await fetch('/api/user/statics', {
            method: 'GET',
            headers: {
                credentials: 'include' 
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching user statistics:', error);
        throw error;
    }
}

async function getUserMatches() {
    try {
        const response = await fetch('/api/match/matchesByUser', {
            method: 'GET',
            headers: {
                credentials: 'include' 
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching user statistics:', error);
        throw error;
    }
}

async function displayRecentMatches() {
    const user = await getGuardedUser()
    if (!user) {
        console.warn('No authenticated user to evaluate match history.');
        return;
    }
    console.log("user from display function" , user)
    try {
        const response = await fetch("/api/match/matchesByUser", {
            method: 'GET',
            headers: {
                credentials: 'include'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const allMatches = await response.json();
        const recentMatches = allMatches.slice(0, 3); // Get first 3 matches
        
        const gamesList = document.querySelector('.games-list');
        gamesList.innerHTML = ''; // Clear existing entries
        
        if (recentMatches.length === 0) {
            gamesList.innerHTML = '<div class="no-matches">No recent matches found</div>';
            return;
        }

        recentMatches.forEach(match => {
            const gameEntry = document.createElement('div');
            gameEntry.className = 'game-entry';
            console.log(match)
            // Determine result text
            let resultText;
            if (match.status === 'FINISHED') {
                console.log(match.winnerId + " = " + user.userID)
                resultText = match.winnerId === user.userID ? '🏆 Victory' : '💔 Defeat';
            } else {
                resultText = '⏳ Ongoing';
            }
            
            gameEntry.innerHTML = `
                <div class="game-mode">${match.difficulty || 'Unknown'}</div>
                <div class="game-date">${match.programmingLanguage}</div>
                <div class="game-result">${resultText}</div>
            `;
            
            gamesList.appendChild(gameEntry);
        });
    } catch (error) {
        console.error('Error loading recent matches:', error);
        document.querySelector('.games-list').innerHTML = 
            '<div class="error-message">Failed to load match history</div>';
    }
}

async function displayDetails() {
    const statics = await getUserStatistics();
    const matches = await getUserMatches();
    const user = await getGuardedUser()
    if (!user) {
        console.warn('No authenticated user for profile display.');
        return;
    }

    document.getElementById("winLoseRatio").innerHTML = statics.winLoseRatio
    document.getElementById("totalMatches").innerHTML = statics.totalMatches
    document.getElementById("usernameP").innerHTML = user.username
    document.getElementById("userId").innerHTML = "Id #"+user.userID
    document.getElementById("userScoreSpan").innerHTML = user.score
    displayRecentMatches()
    
}
document.addEventListener('DOMContentLoaded', () => {
    displayDetails()
});


