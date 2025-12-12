// API Configuration
//const API_KEY = "f8947b5a30msh284eb84742f0b0cp1ec490jsn3e5c66478f7f";
//const TMDB_BASE_URL = "https://api.themoviedb.org/3";
//const TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200";

// -------------------------------------------------------------
// I. MODAL FUNCTIONS
// -------------------------------------------------------------
function openModal(id) {
    document.getElementById(id).style.display = "block";
}

function closeModal(id) {
    document.getElementById(id).style.display = "none";
}

// Close modal when clicking outside
window.onclick = function(e) {
    document.querySelectorAll(".modal").forEach(modal => {
        if (e.target === modal) modal.style.display = "none";
    });
};

// -------------------------------------------------------------
// II. MOVIE SEARCH & WATCHLIST FUNCTIONS
// -------------------------------------------------------------

function getWatchlist() {
    return JSON.parse(localStorage.getItem("watchlist") || "[]");
}

function saveWatchlist(list) {
    localStorage.setItem("watchlist", JSON.stringify(list));
}

// Search movies function
async function searchMovies() {
    const query = document.getElementById("searchInput").value;
    if (!query) return;

    try {
        const response = await fetch(
            `${TMDB_BASE_URL}/search/movie?query=${query}`,
            {
                headers: {
                    "Authorization": `Bearer ${API_KEY}`,
                    "Content-Type": "application/json;charset=utf-8"
                }
            }
        );

        const data = await response.json();
        const resultsDiv = document.getElementById("results");
        resultsDiv.innerHTML = "";

        if (data.results && data.results.length > 0) {
            data.results.forEach(movie => {
                const posterPath = movie.poster_path
                    ? `${TMDB_IMAGE_BASE_URL}${movie.poster_path}`
                    : 'https://via.placeholder.com/200x300?text=No+Image';

                const card = document.createElement("div");
                card.className = "movie";
                card.innerHTML = `
                    <img src="${posterPath}" alt="${movie.title}">
                    <h3>${movie.title}</h3>
                    <p>${movie.release_date ? movie.release_date.substring(0,4) : 'N/A'}</p>
                    <button class="btn" onclick='addToWatchlist(${JSON.stringify(movie)})'>
                        Add to Watchlist
                    </button>
                `;
                resultsDiv.appendChild(card);
            });
        } else {
            resultsDiv.innerHTML = "<p>No movies found. Try a different search term.</p>";
        }
    } catch (error) {
        console.error("Error fetching movies:", error);
        document.getElementById("results").innerHTML = "<p>Error searching for movies. Please try again.</p>";
    }
}

// Add to watchlist
function addToWatchlist(movie) {
    let list = getWatchlist();
    if (!list.some(m => m.id === movie.id)) {
        list.push(movie);
        saveWatchlist(list);
        alert(`${movie.title} added to watchlist!`);
    } else {
        alert(`${movie.title} is already in your watchlist!`);
    }
    renderWatchlist();
}

// Remove from watchlist
function removeFromWatchlist(id) {
    let list = getWatchlist().filter(m => m.id !== id);
    saveWatchlist(list);
    renderWatchlist();
}

// Update badge count
function updateBadge() {
    document.getElementById("watchlist-badge").innerText = getWatchlist().length;
}

// Render watchlist
function renderWatchlist() {
    const items = getWatchlist();
    const container = document.getElementById("watchlist");
    container.innerHTML = "";

    updateBadge();

    if (items.length === 0) {
        container.innerHTML = "<p>Your watchlist is empty.</p>";
        return;
    }

    items.forEach(movie => {
        const item = document.createElement("div");
        item.className = "watchlist-item";
        item.innerHTML = `
            <span>${movie.title}</span>
            <button class="btn" onclick="removeFromWatchlist(${movie.id})">Remove</button>
        `;
        container.appendChild(item);
    });
}

// Toggle watchlist panel
function toggleWatchlist() {
    document.getElementById("watchlist-container").classList.toggle("hidden");
}

// -------------------------------------------------------------
// III. GENRE PREFERENCE FUNCTIONS
// -------------------------------------------------------------

function createGenreCheckboxes() {
    const container = document.getElementById('genreContainer');
    if (!container) return;

    container.innerHTML = '';
    // movieGenres is available from genres.js
    movieGenres.forEach(genre => {
        const checkboxItem = document.createElement('div');
        checkboxItem.className = 'checkbox-item';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.id = 'genre-' + genre.name.toLowerCase().replace(/\s+/g, '-');
        checkbox.name = 'genres';
        checkbox.value = genre.name;

        const label = document.createElement('label');
        label.htmlFor = checkbox.id;
        label.style.marginLeft = '10px';

        const icon = document.createElement('span');
        icon.className = 'genre-icon';
        icon.textContent = genre.icon;

        label.appendChild(icon);
        label.appendChild(document.createTextNode(genre.name));

        checkboxItem.appendChild(checkbox);
        checkboxItem.appendChild(label);
        container.appendChild(checkboxItem);
    });
}

function updateSelectedCount() {
    const selectedCountSpan = document.getElementById('selectedCount');
    if (!selectedCountSpan) return;
    const selectedCount = document.querySelectorAll('#genreContainer input:checked').length;
    selectedCountSpan.textContent = selectedCount;
}

function resetForm() {
    document.querySelectorAll('#genreContainer input[type="checkbox"]').forEach(checkbox => {
        checkbox.checked = false;
    });
    document.getElementById('prefUsername').value = '';
    updateSelectedCount();
}

function showPreferencesDisplay() {
    const preferences = localStorage.getItem('userGenrePreferences');
    const display = document.getElementById('preferencesDisplay');

    if (preferences && display) {
        const data = JSON.parse(preferences);
        display.innerHTML = `
            <p><strong>Name:</strong> ${data.username}</p>
            <p><strong>Favorite Genres:</strong> ${data.selectedGenres.join(', ') || 'None selected'}</p>
            <p><strong>Last Updated:</strong> ${new Date(data.timestamp).toLocaleString()}</p>
        `;
    } else if (display) {
        display.innerHTML = '<p>No genre preferences saved yet. Please complete the survey.</p>';
    }
}

function handleGenreFormSubmit(event) {
    event.preventDefault();

    const usernameInput = document.getElementById('prefUsername');
    if (!usernameInput || !usernameInput.value) {
        alert('Please enter your name');
        return;
    }

    const selectedGenres = Array.from(document.querySelectorAll('#genreContainer input:checked'))
        .map(checkbox => checkbox.value);

    const userPreferences = {
        username: usernameInput.value,
        selectedGenres,
        timestamp: new Date().toISOString()
    };

    localStorage.setItem('userGenrePreferences', JSON.stringify(userPreferences));

    const successMessage = document.getElementById('successMessage');
    if (successMessage) {
        successMessage.style.display = 'block';
        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 3000);
    }

    showPreferencesDisplay();
    alert('Preferences saved successfully!');
}

function showPreferencesModal() {
    initializeGenreForm();
    showPreferencesDisplay();
    openModal('genrePreferencesModal');
}

function initializeGenreForm() {
    createGenreCheckboxes();

    const preferences = localStorage.getItem('userGenrePreferences');

    if (preferences) {
        const data = JSON.parse(preferences);

        // 1. Pre-fill name field
        const usernameInput = document.getElementById('prefUsername');
        if (usernameInput) {
            usernameInput.value = data.username || '';
        }

        // 2. Check the saved checkboxes
        document.querySelectorAll('#genreContainer input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = data.selectedGenres.includes(checkbox.value);
        });
    } else {
        // Clear form if no preferences are saved
        resetForm();
    }

    updateSelectedCount();

    // Attach event listeners after (re)creating elements
    document.querySelectorAll('#genreContainer input[type="checkbox"]').forEach(checkbox => {
        checkbox.removeEventListener('change', updateSelectedCount); // Prevent double-binding
        checkbox.addEventListener('change', updateSelectedCount);
    });

    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.removeEventListener('click', resetForm);
        resetBtn.addEventListener('click', resetForm);
    }

    const form = document.getElementById('movieGenreForm');
    if (form) {
        form.removeEventListener('submit', handleGenreFormSubmit);
        form.addEventListener('submit', handleGenreFormSubmit);
    }
}

// -------------------------------------------------------------
// IV. AUTH/VALIDATION FUNCTIONS
// -------------------------------------------------------------

// --- Password Strength & Match Logic ---
function setupSignupValidation() {
    const suPass = document.getElementById("su_password");
    const suConfirm = document.getElementById("su_confirmPassword");
    const strengthText = document.getElementById("strengthText");
    const strengthBar = document.getElementById("strengthBar");
    const signupForm = document.getElementById("signupForm");

    // CRITICAL: Check if elements exist before attaching listeners
    if (!suPass || !signupForm) return;

    // Strength meter live updates
    suPass.addEventListener("input", () => {
        const val = suPass.value;
        let strength = 0;

        if (val.length >= 8) strength++;
        if (/[A-Z]/.test(val)) strength++;
        if (/[0-9]/.test(val)) strength++;
        if (/[^A-Za-z0-9]/.test(val)) strength++; // Special character check

        strengthBar.className = "";
        strengthBar.style.width = '0%'; // Reset width before applying new class

        if (strength <= 1) {
            strengthText.textContent = "Weak";
            strengthBar.classList.add("strength-weak");
        } else if (strength <= 3) {
            strengthText.textContent = "Medium";
            strengthBar.classList.add("strength-medium");
        } else {
            strengthText.textContent = "Strong";
            strengthBar.classList.add("strength-strong");
        }
    });

    // Password Match Check on submit
    signupForm.addEventListener("submit", e => {
        // Check for password match
        if (suPass.value !== suConfirm.value) {
            e.preventDefault();
            alert("Passwords do not match!");
            return;
        }

        // --- Simulated User Registration Logic ---
        e.preventDefault();

        const userData = {
            username: document.getElementById("su_username").value,
            first_name: document.getElementById("su_first").value,
            last_name: document.getElementById("su_last").value,
            email: document.getElementById("su_email").value,
            password: suPass.value
        };

        console.log("Simulating registration with data:", userData);
        alert("Account created successfully (Simulated)! You can now sign in.");
        closeModal('signupModal');
    });
}

// -------------------------------------------------------------
// V. API INFO DISPLAY FUNCTION
// -------------------------------------------------------------
function initializeAPIInfo() {
    const apiInfoDiv = document.getElementById("apiInfo");
    if (apiInfoDiv) {
        apiInfoDiv.innerHTML = `
            <p><strong>TMDb API Key Status:</strong></p>
            <p>Current Key: <code>${API_KEY}</code></p>
            <p style="color: #FF0000; font-weight: bold;">
                Remember to replace "YOUR_API_KEY_HERE" in <code>script.js</code> with your actual TMDb token to enable search.
            </p>
        `;
    }
}

// -------------------------------------------------------------
function acceptLicense() {
    console.log("License accepted! Function fired.");
    // 1. Save acceptance status to local storage
    localStorage.setItem('licenseAccepted', 'true');

    // 2. Hide the modal
    document.getElementById('licenseModal').style.display = 'none';

    // 3. Enable the main application content
    enableAppContent();
}

function enableAppContent() {
    const appContent = document.getElementById('appContent');
    if (appContent) {
        // Remove the blocking/hiding class and add the active class
        appContent.classList.remove('hidden-app');
            // Remove the blur effect or overlay if you have one
            document.body.style.overflow = 'auto'; // Restore scrolling
        }
    }

    function checkLicenseAndInit() {
        const licenseAccepted = localStorage.getItem('licenseAccepted');
        const licenseModal = document.getElementById('licenseModal');

        if (licenseAccepted === 'true') {
            // If accepted, hide the modal and enable the app immediately
            if (licenseModal) licenseModal.style.display = 'none';
            enableAppContent();
        } else {
            // If not accepted, show the modal and keep the app hidden
            if (licenseModal) licenseModal.style.display = 'block';

            // Ensure the app content remains hidden until accepted
            const appContent = document.getElementById('appContent');
            if (appContent) appContent.classList.add('hidden-app');
        }

        // Initialize all other components regardless of license status
        renderWatchlist();
        setupSignupValidation();
        initializeAPIInfo();

        // Initialize search on Enter key press
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    searchMovies();
                }
            });
        }

        const signInForm = document.getElementById('signInForm');
        if (signInForm) {
            signInForm.addEventListener('submit', function(e) {
                e.preventDefault();
                alert('Signed in successfully (Simulated)!');
                closeModal('signinModal');
            });
        }
    }

    // -------------------------------------------------------------
    // V. INITIALIZATION
    // -------------------------------------------------------------

    document.addEventListener('DOMContentLoaded', function() {
        checkLicenseAndInit();
    });