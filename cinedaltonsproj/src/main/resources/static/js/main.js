
// -------------------------------------------------------------
// I. MODAL FUNCTIONS (Προστέθηκαν για να λειτουργήσουν τα buttons)
// -------------------------------------------------------------
function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = "block";
    }
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = "none";
    }
}

// Κλείσιμο modal όταν ο χρήστης κάνει κλικ έξω από αυτό
window.onclick = function(e) {
    document.querySelectorAll(".modal").forEach(modal => {
        if (e.target === modal) {
            modal.style.display = "none";
        }
    });
};

// -------------------------------------------------------------
// II. INITIALIZATION
// -------------------------------------------------------------
document.addEventListener("DOMContentLoaded", () => {
    // Η checkLicense() βρίσκεται στο license.js και πρέπει να εκτελεστεί
    if (typeof checkLicense === 'function') {
        checkLicense();
    }
    // Εάν χρησιμοποιείται το auth.js, καλέστε την αρχική ενημέρωση UI
    if (typeof updateUIForAuthState === 'function') {
        updateUIForAuthState(null);
    }
});