// Placeholder for future background tasks
chrome.runtime.onInstalled.addListener(() => {
    console.log("ReviseIt installed.");
  });

chrome.runtime.onMessage.addListener((msg, sender, sendResponse) => {
  if (msg.type === "SAVE_BOOKMARK") {
    const { url, title, scroll } = msg.payload;

    // Calculate domain from URL
    const domain = new URL(url).hostname;

    fetch("https://api.yoursite.com/api/bookmarks", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + YOUR_AUTH_TOKEN_HERE  // <-- inject this if using OAuth2
      },
      body: JSON.stringify({
        website: url,
        domainName: domain,
        progress: scroll
      })
    })
    .then(res => res.json())
    .then(data => {
      console.log("Bookmark saved:", data);
    })
    .catch(err => {
      console.error("Failed to save bookmark:", err);
    });
  }
});