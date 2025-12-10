//const SUPABASE_URL = 'https://vnymaccnyaedghawxkoo.supabase.co';
//const SUPABASE_ANON_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkXVCJ9...FA';

//const API_KEY = "fbdfbb87f57e7fd7d5778016a7b26f49";

let currentAuthUser = null;

//  ΧΡΗΣΙΜΟΠΟΙΟΥΜΕ ΤΟ GLOBAL supabase ΤΟΥ CDN
if (typeof window.supabase !== 'undefined') {
    try {
        window.supabaseClient = window.supabase.createClient(
            SUPABASE_URL,
            SUPABASE_ANON_KEY
        );
        console.log(" Supabase client initialized");
    } catch (e) {
        console.error(" Supabase client creation failed:", e);
    }
} else {
    console.error(" Supabase SDK not loaded (check CDN)");
    window.supabaseClient = null;
}
