async function loadLicense() {
    try {
        const response = await fetch("/license-text");
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const text = await response.text();
        document.getElementById("licenseText").innerText = text;
    } catch (error) {
        console.error("Failed to load license:", error);
        document.getElementById("licenseText").innerText =
            "Failed to load license text. Please try again.";
    }
   /* const response = await fetch("/license-text");
    const text = await response.text();
    document.getElementById("licenseText").innerText = text;*/
}

// 1. Λειτουργίες License (Άδεια Χρήσης)
function checkLicense() {
    const accepted = localStorage.getItem('licenseAccepted');
    const licenseModal = document.getElementById('licenseModal');
    const appContent = document.getElementById('appContent');

    if (!accepted) {
        licenseModal.style.display = "block";
        appContent.style.display = "none";
    } else {
        licenseModal.style.display = "none";
        appContent.style.display = "block"; // Εμφάνιση περιεχομένου μετά την αποδοχή
    }
}

function acceptLicense() {
    localStorage.setItem('licenseAccepted', 'true');
    document.getElementById('licenseModal').style.display = "none";
    document.getElementById('appContent').style.display = "block"; // Εμφάνιση του κύριου περιεχομένου
}

document.addEventListener("DOMContentLoaded", checkLicense);

