// js/preferences.js

// Χρησιμοποιεί τις μεταβλητές: currentAuthUser, supabase (από supabase.js/auth.js)

const movieGenres = [
    { name: "Action", icon: "A" }, { name: "Adventure", icon: "AV" }, { name: "Animation", icon: "AN" },
    { name: "Comedy", icon: "C" }, { name: "Crime", icon: "CR" }, { name: "Documentary", icon: "D" },
    { name: "Drama", icon: "DR" }, { name: "Fantasy", icon: "F" }, { name: "Horror", icon: "H" },
    { name: "Mystery", icon: "M" }, { name: "Romance", icon: "R" }, { name: "Science Fiction", icon: "SF" },
    { name: "Thriller", icon: "T" }, { name: "Western", icon: "W" }
];

async function getPreferences() {
    if (!currentAuthUser || !window.supabase) return { data: [], error: null };
    const { data, error } = await window.supabase.from('user_preferences').select('genre').eq('user_id', currentAuthUser.id);
    if (error) { console.error('Error fetching preferences:', error); return { data: [], error: null }; }
    return { data, error };
}

async function savePreferences(selectedGenres) {
    if (!currentAuthUser || !window.supabase) return false;
    try {
        const { error: deleteError } = await window.supabase.from('user_preferences').delete().eq('user_id', currentAuthUser.id);
        if (deleteError) throw deleteError;

        const preferencesToInsert = selectedGenres.map(genre => ({ user_id: currentAuthUser.id, genre: genre }));
        const { error: insertError } = await window.supabase.from('user_preferences').insert(preferencesToInsert);
        if (insertError) throw insertError;

        return true;
    } catch (error) {
        console.error('Error saving preferences:', error);
        return false;
    }
}

function createGenreCheckboxes() {
    const container = document.getElementById('genreContainer');
    if (!container) return;
    container.innerHTML = '';
    // ... (logic to create checkboxes based on movieGenres)
    movieGenres.forEach(genre => {
        const checkboxItem = document.createElement('div');
        checkboxItem.className = 'checkbox-item';
        checkboxItem.style.cssText = 'display: inline-block; margin: 5px;';
        //  (checkbox and label creation)
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.id = 'genre-' + genre.name.toLowerCase().replace(/\s+/g, '-');
        checkbox.name = 'genres';
        checkbox.value = genre.name;

        const label = document.createElement('label');
        label.htmlFor = checkbox.id;
        label.style.marginLeft = '5px';
        label.style.color = 'black';

        const icon = document.createElement('span');
        icon.className = 'genre-icon';
        icon.textContent = genre.icon;
        icon.style.cssText = 'background-color: #fdd835; padding: 2px 5px; border-radius: 3px; font-weight: bold; color: black;';

        label.appendChild(icon);
        label.appendChild(document.createTextNode(' ' + genre.name));

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
    updateSelectedCount();
}

async function handleGenreFormSubmit(event) {
    if (event && event.preventDefault) { event.preventDefault(); }
    if (!currentAuthUser) { document.getElementById('pref-auth-warning').style.display = 'block'; return; }

    const selectedGenres = Array.from(document.querySelectorAll('#genreContainer input:checked')).map(checkbox => checkbox.value);
    document.getElementById('savePrefBtn').disabled = true;

    const success = await savePreferences(selectedGenres);
    document.getElementById('savePrefBtn').disabled = false;

    const successMessage = document.getElementById('successMessage');
    if (successMessage) {
        successMessage.textContent = success ? 'Your genre preferences have been saved successfully!' : 'Failed to save preferences. Check console for error.';
        successMessage.style.color = success ? 'green' : 'red';
        successMessage.style.display = 'block';
        setTimeout(() => { successMessage.style.display = 'none'; }, 3000);
    }

    showPreferencesDisplay();
}

async function initializeGenreForm() {
    createGenreCheckboxes();

    // ... (logic to load saved preferences and set up event listeners)
    const prefUsername = document.getElementById('prefUsername');
    const savePrefBtn = document.getElementById('savePrefBtn');
    const prefAuthWarning = document.getElementById('pref-auth-warning');

    if (currentAuthUser) {
        const username = currentAuthUser.user_metadata?.username || currentAuthUser.email;
        prefUsername.value = username;
        savePrefBtn.disabled = false;
        prefAuthWarning.style.display = 'none';

        const { data } = await getPreferences();
        if (data) {
            const savedGenres = data.map(item => item.genre);
            document.querySelectorAll('#genreContainer input[type="checkbox"]').forEach(checkbox => {
                checkbox.checked = savedGenres.includes(checkbox.value);
            });
        }
    } else {
        prefUsername.value = '';
        savePrefBtn.disabled = true;
        prefAuthWarning.style.display = 'block';
        resetForm();
    }

    updateSelectedCount();

    // Setup event listeners
    document.querySelectorAll('#genreContainer input[type="checkbox"]').forEach(checkbox => {
        checkbox.removeEventListener('change', updateSelectedCount);
        checkbox.addEventListener('change', updateSelectedCount);
    });

    document.getElementById('resetBtn').removeEventListener('click', resetForm);
    document.getElementById('resetBtn').addEventListener('click', resetForm);

    const form = document.getElementById('movieGenreForm');
    form.removeEventListener('submit', handleGenreFormSubmit);
    form.addEventListener('submit', handleGenreFormSubmit);
}

function showPreferencesModal() {
    initializeGenreForm();
    showPreferencesDisplay();
    openModal('genrePreferencesModal');
}

async function showPreferencesDisplay() {
    // Re-implemented fully to use getPreferences and update DOM
    const display = document.getElementById('preferencesDisplay');
    if (!currentAuthUser) { display.innerHTML = '<p>Please sign in to view your saved preferences.</p>'; return; }
    const { data, error } = await getPreferences();
    if (error || !data) { display.innerHTML = '<p>Error loading preferences.</p>'; return; }
    if (data.length === 0) { display.innerHTML = '<p>No genre preferences saved yet. Please select and save.</p>'; return; }

    const genreList = data.map(item => item.genre).join(', ');
    display.innerHTML = `<p><strong>Favorite Genres:</strong> ${genreList || 'None selected'}</p>`;
}