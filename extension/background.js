chrome.runtime.onInstalled.addListener(() => {
  console.log("ReviseIt installed.");
});

chrome.runtime.onMessage.addListener((msg, sender, sendResponse) => {
  if (msg.type === "SAVE_BOOKMARK") {
    const { url, title, scroll } = msg.payload;
    const domain = new URL(url).hostname;

    chrome.storage.local.get("authToken", (data) => {
      const token = data.authToken;

      if (!token) {
        console.error("❌ No auth token found in chrome.storage.local.");
        return;
      }

      fetch("https://api.yoursite.com/api/bookmarks", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
          website: url,
          domainName: domain,
          progress: scroll
        })
      })
        .then(res => res.json())
        .then(data => {
          console.log("✅ Bookmark saved:", data);
        })
        .catch(err => {
          console.error("❌ Failed to save bookmark:", err);
        });
    });

    return true; // allow async sendResponse
  }
});

chrome.runtime.onMessageExternal.addListener((message, sender, sendResponse) => {
  if (message.type === "SAVE_TOKEN") {
    const token = message.token;

    if (token) {
      chrome.storage.local.set({ authToken: token }, () => {
        console.log("✅ Auth token saved to storage");
        sendResponse({ success: true });
      });

      return true; // Required for async sendResponse
    } else {
      sendResponse({ success: false, error: "No token provided" });
    }
  }
});
