function checkLicense() {
    const accepted = localStorage.getItem("licenseAccepted");
    toggleApp(!accepted);
}

function acceptLicense() {
    localStorage.setItem("licenseAccepted", "true");
    toggleApp(false);
}

function toggleApp(showLicense) {
    document.getElementById("licenseModal").style.display =
        showLicense ? "block" : "none";

    document.getElementById("appContent").style.display =
        showLicense ? "none" : "block";
}
