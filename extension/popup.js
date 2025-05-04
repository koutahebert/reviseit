const SITE_URL = "http://localhost:3000";

document.addEventListener("DOMContentLoaded", () => {
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
  });  