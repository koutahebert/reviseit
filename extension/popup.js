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
        const [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

        const [{ result }] = await chrome.scripting.executeScript({
          target: { tabId: tab.id },
          func: () => {
            const scrollY = window.scrollY;
            const viewportHeight = window.innerHeight;
            const scrollBottom = scrollY + viewportHeight;
            const totalHeight = document.body.scrollHeight - viewportHeight;
            const progress = totalHeight > 0 ? scrollY / totalHeight : 0;

            // Traverse DOM to collect text up to scrollBottom
            const walker = document.createTreeWalker(
              document.body,
              NodeFilter.SHOW_TEXT,
              {
                acceptNode: (node) => {
                  if (!node.nodeValue.trim()) return NodeFilter.FILTER_REJECT;

                  const parent = node.parentElement;
                  if (!parent) return NodeFilter.FILTER_REJECT;

                  // Only accept text inside headings or paragraphs
                  const allowedTags = ['P', 'H1', 'H2', 'H3', 'H4', 'H5', 'H6'];
                  if (!allowedTags.includes(parent.tagName)) return NodeFilter.FILTER_REJECT;

                  // Reject if parent or any ancestor is fixed/sticky or hidden
                  let el = parent;
                  while (el) {
                    const style = window.getComputedStyle(el);
                    if (
                      style.position === 'fixed' ||
                      style.position === 'sticky' ||
                      style.display === 'none' ||
                      style.visibility === 'hidden'
                    ) {
                      return NodeFilter.FILTER_REJECT;
                    }
                    el = el.parentElement;
                  }

                  // Only accept nodes above scroll bottom
                  const rect = parent.getBoundingClientRect();
                  const absoluteTop = rect.top + window.scrollY;

                  return absoluteTop < scrollBottom
                    ? NodeFilter.FILTER_ACCEPT
                    : NodeFilter.FILTER_REJECT;
                }
              }
            );

            let visibleText = '';
            while (walker.nextNode()) {
              visibleText += walker.currentNode.nodeValue + ' ';
            }

            return {
              url: window.location.href,
              title: document.title,
              scroll: progress,
              content: visibleText.trim()
            };
          }
        });

        console.log("Scroll progress captured:", result.scroll);
        console.log("Content:\n", result.content);

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