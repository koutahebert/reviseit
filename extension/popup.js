const SITE_URL = "http://localhost:3000";

document.addEventListener("DOMContentLoaded", () => {
    // Check for token in URL on popup load and store it
    const urlParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = urlParams.get('token');
    if (tokenFromUrl) {
      chrome.storage.local.set({ authToken: tokenFromUrl }, () => {
        alert('Login successful!');
        // Optionally, remove token from URL for cleanliness
        window.history.replaceState({}, document.title, window.location.pathname);
      });
    }

    const bookmarkButton = document.getElementById("bookmarkBtn");
  
    if (bookmarkButton) {
      bookmarkButton.addEventListener("click", async () => {
        try {
          // Get the current active tab
          const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
  
          // Inject a script into the tab to collect scroll info and page data
          chrome.scripting.executeScript({
            target: { tabId: tab.id },
            func: () => {
              const scrollY = window.scrollY;
              const totalHeight = document.body.scrollHeight - window.innerHeight;
              const progress = totalHeight > 0 ? scrollY / totalHeight : 0;
  
              const data = {
                url: window.location.href,
                title: document.title,
                progress
              };
  
              chrome.runtime.sendMessage({ type: "SAVE_BOOKMARK", payload: data });
            }
          });
        } catch (err) {
          console.error("Failed to get tab info or inject script:", err);
        }
      });
    }
  
    const goButton = document.getElementById("goBtn");

    if (goButton) {
      goButton.addEventListener("click", async () => {
        try {
          window.open(SITE_URL, "_blank").focus();
        } catch (err) {
          console.error("Failed to jump to website: ", err);
        }
      });
    }

    const loginBtn = document.getElementById('login-btn');
    if (loginBtn) {
      loginBtn.addEventListener('click', () => {
        chrome.identity.launchWebAuthFlow({
          url: 'http://localhost:8080/oauth2/authorization/google',
          interactive: true
        }, function(redirectUrl) {
          console.log('launchWebAuthFlow redirectUrl:', redirectUrl);
          if (redirectUrl) {
            const url = new URL(redirectUrl);
            const token = url.searchParams.get('token');
            if (token) {
              chrome.storage.local.set({ authToken: token }, () => {
                if (chrome.runtime.lastError) {
                  console.error('Storage error:', chrome.runtime.lastError);
                } else {
                  alert('Login successful!');
                  chrome.storage.local.get('authToken', (result) => {
                    console.log('Stored token:', result.authToken);
                  });
                }
              });
            } else {
              alert('Login failed: No token found.');
            }
          } else {
            alert('Login failed or cancelled.');
          }
        });
      });
    }
});