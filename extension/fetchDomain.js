// Fetch and display the current tab's domain
chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
    const url = new URL(tabs[0].url);
    document.getElementById('domain').textContent = url.hostname;
});
