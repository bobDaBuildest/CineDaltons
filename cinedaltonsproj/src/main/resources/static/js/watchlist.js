// js/watchlist.js

// Χρησιμοποιεί τις μεταβλητές: API_KEY, currentAuthUser, supabase (από supabase.js/auth.js)

async function getWatchlist() {
    if (!currentAuthUser || !window.supabase) return [];
    const { data, error } = await window.supabase.from('watchlist').select('*').eq('user_id', currentAuthUser.id);
    if (error) { console.error('Error fetching watchlist:', error); return []; }
    return data;
}

async function addToWatchlist(movie) {
    if (!currentAuthUser) { openModal('signinModal'); alert('Please sign in to add movies to your watchlist!'); return; }
    if (!window.supabase) return;
    const list = await getWatchlist();
    if (list.some(m => m.movie_id === movie.id)) { alert(`${movie.title} is already in your watchlist!`); return; }

    const { error } = await window.supabase
        .from('watchlist')
        .insert([{ user_id: currentAuthUser.id, movie_id: movie.id, title: movie.title, poster_path: movie.poster_path, release_date: movie.release_date }]);

    if (error) { console.error('Error adding to watchlist:', error); alert('Failed to add movie to watchlist. Check RLS policies.'); }
    else { alert(`${movie.title} added to watchlist!`); renderWatchlist(); }
}

async function removeFromWatchlist(id) {
    if (!currentAuthUser || !window.supabase) return;
    const { error } = await window.supabase
        .from('watchlist')
        .delete()
        .eq('movie_id', id)
        .eq('user_id', currentAuthUser.id);
    if (error) { console.error('Error removing from watchlist:', error); alert('Failed to remove movie. Check RLS policies.'); }
    else { renderWatchlist(); }
}

async function updateBadge() {
    const list = await getWatchlist();
    document.getElementById("watchlist-badge").innerText = list.length;
}

async function renderWatchlist() {
    const container = document.getElementById("watchlist");
    container.innerHTML = "";

    if (!currentAuthUser) { container.innerHTML = "<p>Please sign in to view your watchlist</p>"; document.getElementById("watchlist-badge").innerText = '0'; return; }

    container.innerHTML = "<p>Loading watchlist...</p>";
    const items = await getWatchlist();
    updateBadge();

    if (items.length === 0) { container.innerHTML = "<p>Your watchlist is empty.</p>"; return; }

    items.forEach(movie => {
        const item = document.createElement("div");
        item.className = "watchlist-item";
        item.style.cssText = "display: flex; justify-content: space-between; align-items: center; padding: 5px 0;";
        item.innerHTML = `
            <span title="${movie.title}">${movie.title.substring(0, 30)}${movie.title.length > 30 ? '...' : ''}</span>
            <button class="btn" style="padding: 5px 10px;" onclick="removeFromWatchlist(${movie.movie_id})">X</button>
        `;
        container.appendChild(item);
    });
}

// Search movies function (TMDb)
async function searchMovies() {
    const query = document.getElementById("searchInput").value;
    const resultsDiv = document.getElementById("results");

    if (!query) { resultsDiv.innerHTML = "<p>Please enter a movie title to search.</p>"; return; }

    resultsDiv.innerHTML = "<h3>Searching...</h3>";

    try {
        const response = await fetch(
            `https://api.themoviedb.org/3/search/movie?query=${encodeURIComponent(query)}&api_key=${API_KEY}`
        );

        const data = await response.json();
        resultsDiv.innerHTML = "";

        if (response.status === 401) {
            resultsDiv.innerHTML = "<p style='color:red; font-weight: bold;'>API Error 401: Invalid TMDb API Key. Please update the API_KEY variable.</p>";
            console.error("TMDb API Error: 401 Unauthorized. Check API_KEY.");
            return;
        }

        if (data.results && data.results.length > 0) {
            data.results.forEach(movie => {
                const posterPath = movie.poster_path
                    ? `https://image.tmdb.org/t/p/w200${movie.poster_path}`
                    : 'https://via.placeholder.com/200x300?text=No+Image';

                const card = document.createElement("div");
                card.className = "movie";
                card.innerHTML = `
                        <img src="${posterPath}" alt="${movie.title}" style="max-width: 100%; height: auto;">
                        <h3 style="font-size: 1.1em; margin: 10px 0;">${movie.title}</h3>
                        <p style="margin: 5px 0;">${movie.release_date ? movie.release_date.substring(0,4) : 'N/A'}</p>
                        <button class="btn" onclick='addToWatchlist(${JSON.stringify({
                    id: movie.id, title: movie.title, poster_path: movie.poster_path, release_date: movie.release_date
                })})'>
                            Add to Watchlist
                        </button>
                    `;
                resultsDiv.appendChild(card);
            });
        } else { resultsDiv.innerHTML = "<p>No movies found. Try a different search term.</p>"; }
    } catch (error) {
        console.error("Error fetching movies:", error);
        document.getElementById("results").innerHTML = "<p>Error searching for movies. Please check your API key or network connection.</p>";
    }
}