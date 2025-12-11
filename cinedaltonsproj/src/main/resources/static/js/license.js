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

function checkLicense() {
    const accepted = localStorage.getItem("licenseAccepted");
    const modal = document.getElementById("licenseModal");
    const app = document.getElementById("appContent");

    if (!accepted) {
        // ΔΕΝ έχει δεχτεί → δείξε modal
        modal.classList.remove("hidden");
        app.classList.add("hidden-app");
        loadLicense();
    } else {
        // Έχει δεχτεί → δείξε εφαρμογή
        modal.classList.add("hidden");
        app.classList.remove("hidden-app");
    }
}

function acceptLicense() {
    localStorage.setItem("licenseAccepted", "true");

    document.getElementById("licenseModal").classList.add("hidden");
    document.getElementById("appContent").classList.remove("hidden-app");
}

document.addEventListener("DOMContentLoaded", checkLicense);

