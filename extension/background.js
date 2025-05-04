chrome.runtime.onInstalled.addListener(() => {
  console.log("ReviseIt extension installed.");
});

// Listen for external token messages from the web app or OAuth2 redirect
chrome.runtime.onMessageExternal.addListener((message, sender, sendResponse) => {
  if (message.type === "SAVE_TOKEN") {
    const token = message.token;
    if (token) {
      chrome.storage.local.set({ authToken: token }, () => {
        console.log("Token saved to storage.");
        sendResponse({ success: true });
      });
      return true; // Keep channel open for async response
    } else {
      sendResponse({ success: false, error: "No token provided" });
    }
  }
});

// Listen for internal message to save bookmark
chrome.runtime.onMessage.addListener((msg, sender, sendResponse) => {
  if (msg.type === "SAVE_BOOKMARK") {
    const { url, title, scroll } = msg.payload;
    const domain = new URL(url).hostname;

    // Retrieve token from storage before making API call
    chrome.storage.local.get("authToken", (result) => {
      const token = result.authToken;
      if (!token) {
        console.error("No token found in chrome.storage.local.");
        sendResponse({ success: false, error: "No auth token" });
        return;
      }

      const bookmarkData = {
        website: url,
        domainName: domain,
        progress: scroll
      };
      console.log("Payload being sent to backend:", bookmarkData);

      fetch("https://api.yoursite.com/api/bookmarks", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer " + token
        },
        body: JSON.stringify(bookmarkData)
      })
      .then(res => res.json())
      .then(data => {
        console.log("Bookmark saved:", data);
        sendResponse({ success: true, data });
      })
      .catch(err => {
        console.error("Error saving bookmark:", err);
        sendResponse({ success: false, error: err.toString() });
      });
    });
    return true; // Keep message channel open for async response
  }
});
