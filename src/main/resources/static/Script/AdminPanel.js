// Expose functions to window for onclick events
window.editChallenge = editChallenge;
window.deleteChallenge = deleteChallenge;
window.viewSubmission = viewSubmission;
window.deleteSubmission = deleteSubmission;
window.deleteUser = deleteUser;
window.deleteMatch = deleteMatch;

document.addEventListener('DOMContentLoaded', () => {
    // Navigation
    const navBtns = document.querySelectorAll('.nav-btn');
    const sections = document.querySelectorAll('main section');

    navBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            // Remove active class from all buttons and sections
            navBtns.forEach(b => b.classList.remove('active'));
            sections.forEach(s => s.classList.remove('active-section'));

            // Add active class to clicked button and target section
            btn.classList.add('active');
            const targetId = btn.getAttribute('data-target');
            document.getElementById(targetId).classList.add('active-section');
            
            // Load data for the section
            loadSectionData(targetId);
        });
    });

    // Initial load
    loadSectionData('challenges');

    // Challenge Modal
    const challengeModal = document.getElementById('challengeModal');
    const newChallengeBtn = document.getElementById('newChallengeBtn');
    const closeChallengeModal = challengeModal.querySelector('.close');
    const challengeForm = document.getElementById('challengeForm');
    const addTestCaseBtn = document.getElementById('addTestCaseBtn');
    const testCasesContainer = document.getElementById('testCasesContainer');

    newChallengeBtn.onclick = () => {
        document.getElementById('challengeId').value = '';
        document.getElementById('challengeModalTitle').innerText = 'New Challenge';
        challengeForm.reset();
        testCasesContainer.innerHTML = '';
        challengeModal.style.display = 'block';
    };

    closeChallengeModal.onclick = () => {
        challengeModal.style.display = 'none';
    };

    addTestCaseBtn.onclick = () => {
        addTestCaseField();
    };

    challengeForm.onsubmit = async (e) => {
        e.preventDefault();
        await saveChallenge();
    };

    // Submission Modal
    const submissionModal = document.getElementById('submissionModal');
    const closeSubmissionModal = submissionModal.querySelector('.close');
    
    closeSubmissionModal.onclick = () => {
        submissionModal.style.display = 'none';
    };

    window.onclick = (event) => {
        if (event.target == challengeModal) {
            challengeModal.style.display = "none";
        }
        if (event.target == submissionModal) {
            submissionModal.style.display = "none";
        }
    };
});

function loadSectionData(section) {
    if (section === 'challenges') loadChallenges();
    else if (section === 'submissions') loadSubmissions();
    else if (section === 'users') loadUsers();
    else if (section === 'matches') loadMatches();
}

// --- Challenges ---
async function loadChallenges() {
    const list = document.getElementById('challengesList');
    list.innerHTML = 'Loading...';
    try {
        const response = await fetch('/api/admin/challenges');
        const challenges = await response.json();
        
        let html = `<table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Difficulty</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>`;
        
        challenges.forEach(c => {
            html += `<tr>
                <td>${c.challengeID}</td>
                <td>${c.title}</td>
                <td>${c.difficulty}</td>
                <td>
                    <button onclick="editChallenge(${c.challengeID})">Edit</button>
                    <button onclick="deleteChallenge(${c.challengeID})" class="danger-btn">Delete</button>
                </td>
            </tr>`;
        });
        html += '</tbody></table>';
        list.innerHTML = html;
    } catch (error) {
        console.error('Error loading challenges:', error);
        list.innerHTML = 'Error loading challenges.';
    }
}

function addTestCaseField(input = '', output = '') {
    const container = document.getElementById('testCasesContainer');
    const div = document.createElement('div');
    div.className = 'test-case-item';
    div.innerHTML = `
        <label>Input:</label>
        <textarea class="tc-input" required>${input}</textarea>
        <label>Expected Output:</label>
        <textarea class="tc-output" required>${output}</textarea>
        <button type="button" onclick="this.parentElement.remove()" class="danger-btn">Remove</button>
    `;
    container.appendChild(div);
}

async function saveChallenge() {
    const id = document.getElementById('challengeId').value;
    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    const difficulty = document.getElementById('difficulty').value;
    const sample = document.getElementById('sample').value;

    const testCases = [];
    document.querySelectorAll('.test-case-item').forEach(item => {
        testCases.push({
            input: item.querySelector('.tc-input').value,
            expectedOutput: item.querySelector('.tc-output').value
        });
    });

    const data = {
        challenge: {
            title, description, difficulty, sample
        },
        testCases
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/admin/challenges/${id}` : '/api/admin/challenges';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            document.getElementById('challengeModal').style.display = 'none';
            loadChallenges();
        } else {
            alert('Failed to save challenge.');
        }
    } catch (error) {
        console.error('Error saving challenge:', error);
    }
}

async function editChallenge(id) {
    try {
        const response = await fetch(`/api/admin/challenges/${id}`);
        const data = await response.json();
        
        document.getElementById('challengeId').value = data.challenge.challengeID;
        document.getElementById('title').value = data.challenge.title;
        document.getElementById('description').value = data.challenge.description;
        document.getElementById('difficulty').value = data.challenge.difficulty;
        document.getElementById('sample').value = data.challenge.sample;
        
        const container = document.getElementById('testCasesContainer');
        container.innerHTML = '';
        if (data.testCases) {
            data.testCases.forEach(tc => addTestCaseField(tc.input, tc.expectedOutput));
        }
        
        document.getElementById('challengeModalTitle').innerText = 'Edit Challenge';
        document.getElementById('challengeModal').style.display = 'block';
    } catch (error) {
        console.error('Error loading challenge details:', error);
    }
}

async function deleteChallenge(id) {
    if (!confirm('Are you sure?')) return;
    try {
        await fetch(`/api/admin/challenges/${id}`, { method: 'DELETE' });
        loadChallenges();
    } catch (error) {
        console.error('Error deleting challenge:', error);
    }
}


// --- Submissions ---
async function loadSubmissions() {
    const list = document.getElementById('submissionsList');
    list.innerHTML = 'Loading...';
    try {
        const response = await fetch('/api/admin/submissions');
        const submissions = await response.json();
        
        let html = `<table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Challenge</th>
                    <th>Language</th>
                    <th>Result</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>`;
        
        submissions.forEach(s => {
            html += `<tr>
                <td>${s.submissionID}</td>
                <td>${s.challengeTitle}</td>
                <td>${s.programmingLanguage}</td>
                <td>${s.result}</td>
                <td>
                    <button onclick="viewSubmission(${s.submissionID})">View Code</button>
                    <button onclick="deleteSubmission(${s.submissionID})" class="danger-btn">Delete</button>
                </td>
            </tr>`;
        });
        html += '</tbody></table>';
        list.innerHTML = html;
    } catch (error) {
        console.error('Error loading submissions:', error);
        list.innerHTML = 'Error loading submissions.';
    }
}

let editor = null;

async function viewSubmission(id) {
    try {
        const response = await fetch(`/api/admin/submissions/${id}`);
        const submission = await response.json();
        
        const modal = document.getElementById('submissionModal');
        modal.style.display = 'block';
        
        // Initialize Monaco Editor if not already
        if (!editor) {
            require.config({ paths: { 'vs': '/monaco/vs' }});
            require(['vs/editor/editor.main'], function() {
                editor = monaco.editor.create(document.getElementById('submissionCodeEditor'), {
                    value: submission.code,
                    language: getMonacoLanguage(submission.programmingLanguage),
                    readOnly: true,
                    theme: 'vs-dark'
                });
            });
        } else {
            editor.setValue(submission.code);
            monaco.editor.setModelLanguage(editor.getModel(), getMonacoLanguage(submission.programmingLanguage));
        }
        
        document.getElementById('submissionInfo').innerHTML = `
            <p><strong>Result:</strong> ${submission.result}</p>
            <p><strong>Status:</strong> ${submission.status}</p>
            <p><strong>Compile Output:</strong> <pre>${submission.compileOutput || ''}</pre></p>
        `;
        
        document.getElementById('deleteSubmissionBtn').onclick = () => deleteSubmission(id);
        
    } catch (error) {
        console.error('Error loading submission details:', error);
    }
}

function getMonacoLanguage(lang) {
    if (!lang) return 'plaintext';
    lang = lang.toLowerCase();
    if (lang === 'python') return 'python';
    if (lang === 'java') return 'java';
    if (lang === 'cpp' || lang === 'c++') return 'cpp';
    if (lang === 'javascript') return 'javascript';
    return 'plaintext';
}

async function deleteSubmission(id) {
    if (!confirm('Are you sure?')) return;
    try {
        await fetch(`/api/admin/submissions/${id}`, { method: 'DELETE' });
        document.getElementById('submissionModal').style.display = 'none';
        loadSubmissions();
    } catch (error) {
        console.error('Error deleting submission:', error);
    }
}

// --- Users ---
async function loadUsers() {
    const list = document.getElementById('usersList');
    list.innerHTML = 'Loading...';
    try {
        const response = await fetch('/api/admin/users');
        const users = await response.json();
        
        let html = `<table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Score</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>`;
        
        users.forEach(u => {
            html += `<tr>
                <td>${u.userID}</td>
                <td>${u.username}</td>
                <td>${u.email}</td>
                <td>${u.role}</td>
                <td>${u.score}</td>
                <td>
                    <button onclick="deleteUser(${u.userID})" class="danger-btn">Delete</button>
                </td>
            </tr>`;
        });
        html += '</tbody></table>';
        list.innerHTML = html;
    } catch (error) {
        console.error('Error loading users:', error);
        list.innerHTML = 'Error loading users.';
    }
}

async function deleteUser(id) {
    if (!confirm('Are you sure?')) return;
    try {
        await fetch(`/api/admin/users/${id}`, { method: 'DELETE' });
        loadUsers();
    } catch (error) {
        console.error('Error deleting user:', error);
    }
}


// --- Matches ---
async function loadMatches() {
    const list = document.getElementById('matchesList');
    list.innerHTML = 'Loading...';
    try {
        const response = await fetch('/api/admin/matches');
        const matches = await response.json();
        
        let html = `<table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Challenge ID</th>
                    <th>Difficulty</th>
                    <th>Language</th>
                    <th>Status</th>
                    <th>Winner ID</th>
                    <th>Players</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>`;
        
        matches.forEach(m => {
            html += `<tr>
                <td>${m.matchID}</td>
                <td>${m.currentChallengeId || '-'}</td>
                <td>${m.difficulty}</td>
                <td>${m.programmingLanguage}</td>
                <td>${m.status}</td>
                <td>${m.winnerId || '-'}</td>
                <td>${m.players || '-'}</td>
                <td>
                    <button onclick="deleteMatch(${m.matchID})" class="danger-btn">Delete</button>
                </td>
            </tr>`;
        });
        html += '</tbody></table>';
        list.innerHTML = html;
    } catch (error) {
        console.error('Error loading matches:', error);
        list.innerHTML = 'Error loading matches.';
    }
}

async function deleteMatch(id) {
    if (!confirm('Are you sure?')) return;
    try {
        await fetch(`/api/admin/matches/${id}`, { method: 'DELETE' });
        loadMatches();
    } catch (error) {
        console.error('Error deleting match:', error);
    }
}

