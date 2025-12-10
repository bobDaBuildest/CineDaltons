async function searchMovies() {
    const query = document.getElementById("searchInput").value;
    if (!query) return;

    const res = await fetch(
        `https://api.themoviedb.org/3/search/movie?api_key=${TMDB_API_KEY}&query=${query}`
    );

    const data = await res.json();
    renderMovies(data.results);
}

function renderMovies(movies) {
    const results = document.getElementById("results");
    results.innerHTML = "";

    movies.forEach(m => {
        const div = document.createElement("div");
        div.className = "movie";

        div.innerHTML = `
            <img src="https://image.tmdb.org/t/p/w500${m.poster_path}">
            <h3>${m.title}</h3>
            <p>${m.release_date?.slice(0,4) || ""}</p>
            <button class="btn" onclick="addToWatchlist('${m.title}')">
                Add to Watchlist
            </button>
        `;

        results.appendChild(div);
    });
}
