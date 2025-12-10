/*// Χρησιμοποιεί τις μεταβλητές: supabase, currentAuthUser (από supabase.js)

async function updateUIForAuthState(session) {
    if (!supabase) {
        document.getElementById('auth-buttons').innerHTML = `
            <p style="color:red; font-size: 14px;">Auth Error</p>
            <button class="btn" onclick="openModal('signupModal')">Sign Up</button>
            <button class="btn" onclick="openModal('signinModal')">Sign In</button>
        `;
        return;
    }

    // Εάν δεν υπάρχει session, το τραβάμε μέσω getUser().
    const user = session?.user || (await window.supabase.auth.getUser())?.data?.user;
    currentAuthUser = user;
    const watchlistToggle = document.getElementById('watchlist-toggle');
    const authButtons = document.getElementById('auth-buttons');

    if (!authButtons || !watchlistToggle) return;

    if (currentAuthUser) {
        const username = currentAuthUser.user_metadata?.username || currentAuthUser.email;

        watchlistToggle.style.display = 'block';
        authButtons.innerHTML = `
            <div class="user-info">
                <span class="welcome-message">Welcome, ${username}</span>
                <button class="btn" onclick="showPreferencesModal()">Genre Preferences</button>
                <button class="btn" onclick="logout()">Logout</button>
            </div>
        `;
        renderWatchlist();
        closeModal('signinModal');
        closeModal('signupModal');
    } else {
        watchlistToggle.style.display = 'none';
        document.getElementById('watchlist-container').classList.add('hidden');
        authButtons.innerHTML = `
            <button class="btn" onclick="showPreferencesModal()">Genre Preferences</button>
            <button class="btn" onclick="openModal('signupModal')">Sign Up</button>
            <button class="btn" onclick="openModal('signinModal')">Sign In</button>
        `;
        document.getElementById("watchlist-badge").innerText = '0';
    }
}

async function handleAuthChange(event, session) {
    await updateUIForAuthState(session);
}

async function signUp(username, first_name, last_name, email, password) {
    document.getElementById('signup-error').innerText = '';
    if (!supabase || !supabase.auth) return false;
    try {
        const { data: authData, error: authError } = await window.supabase.auth.signUp({
            email: email,
            password: password,
            options: { data: { username, first_name, last_name } }
        });

        if (authError) throw authError;
        const user = authData.user;
        if (!user) {
            document.getElementById('signup-error').innerText = 'Registration successful! Please check your email for a confirmation link.';
            return true;
        }

        // ΕΙΣΑΓΩΓΗ ΣΤΟΝ ΠΙΝΑΚΑ 'users'
        await supabase.from('users').insert([{ id: user.id, username, email, password, first_name, last_name }]);
        alert('Account created successfully! You are now logged in.');
        return true;
    } catch (error) {
        console.error('Sign Up Error:', error);
        document.getElementById('signup-error').innerText = error.message || 'An unexpected error occurred during sign up.';
        return false;
    }
}

async function signIn(email, password) {
    document.getElementById('signin-error').innerText = '';
    if (!supabase || !supabase.auth) return false;
    try {
        const { error } = await window.supabase.auth.signInWithPassword({ email, password });
        if (error) throw error;
        alert('Signed in successfully!');
        return true;
    } catch (error) {
        console.error('Sign In Error:', error);
        document.getElementById('signin-error').innerText = error.message || 'Invalid login credentials.';
        return false;
    }
}

async function logout() {
    if (!supabase || !supabase.auth) return;
    const { error } = await window.supabase.auth.signOut();
    if (error) {
        console.error('Logout Error:', error);
        alert('Logout failed: ' + error.message);
    } else {
        alert('Logged out successfully!');
    }
}

async function handlePasswordReset() {
    const email = prompt("Enter your email address to reset your password:");
    if (email && supabase?.auth) {
        const { error } = await window.supabase.auth.resetPasswordForEmail(email, { redirectTo: window.location.origin });
        if (error) {
            alert('Error sending password reset email: ' + error.message);
        } else {
            alert('Password reset link sent to your email!');
        }
    }
}*/async function signUp() {
    const email = prompt("Email");
    const password = prompt("Password");

    const { error } = await supabase.auth.signUp({ email, password });
    alert(error ? error.message : "✅ Account created");
}

async function signIn() {
    const email = prompt("Email");
    const password = prompt("Password");

    const { error } = await supabase.auth.signInWithPassword({
        email,
        password
    });

    alert(error ? error.message : "✅ Logged in");
}
document.addEventListener("DOMContentLoaded", () => {
    const signupForm = document.getElementById("signupForm");

    if (signupForm) {
        signupForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const userData = {
                username: document.getElementById("su_username").value,
                first_name: document.getElementById("su_firstName").value,
                last_name: document.getElementById("su_lastName").value,
                email: document.getElementById("su_email").value,
                password: document.getElementById("su_password").value
            };

            try {
                const { data, error } = await window.supabase
                    .from("users")
                    .insert([userData])
                    .select()
                    .single();

                if (error) {
                    console.error(error);
                    alert("Signup failed");
                    return;
                }

                console.log("User created:", data);

                // Store logged user (demo login)
                setUser({
                    id: data.id,
                    username: data.username,
                    email: data.email
                });

                closeModal("signupModal");
                alert("Account created successfully ✅");

            } catch (err) {
                console.error(err);
                alert("Unexpected error");
            }
        });
    }
});
