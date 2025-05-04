const SITE_URL = "http://localhost:3000";

document.addEventListener("DOMContentLoaded", () => {
  const bookmarkButton = document.getElementById("bookmarkBtn");

  if (bookmarkButton) {
    bookmarkButton.addEventListener("click", async () => {
      try {
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

        const [{ result }] = await chrome.scripting.executeScript({
          target: { tabId: tab.id },
          func: () => {
            const scrollY = window.scrollY;
            const totalHeight = document.body.scrollHeight - window.innerHeight;
            const progress = totalHeight > 0 ? scrollY / totalHeight : 0;

            return {
              url: window.location.href,
              title: document.title,
              scroll: progress  
            };
          }
        });

        console.log("Scroll progress captured:", result.scroll);

        chrome.runtime.sendMessage({
          type: "SAVE_BOOKMARK",
          payload: result
        });

      } catch (err) {
        console.error("Failed to inject scroll script:", err);
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
