"use client"

import { useEffect, useState } from "react"
import type { BookmarkDTO, BookmarkDetailDTO } from "@/types/bookmark"
import BookmarkCard from "@/components/bookmark-card"
import DomainFilter from "@/components/domain-filter"
import BookmarkDetailPanel from "@/components/bookmark-detail-panel"
import { Loader2 } from "lucide-react"

export default function BookmarksDashboard() {
  const [bookmarks, setBookmarks] = useState<BookmarkDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedBookmark, setSelectedBookmark] = useState<BookmarkDTO | null>(null)
  const [bookmarkDetail, setBookmarkDetail] = useState<BookmarkDetailDTO | null>(null)
  const [detailLoading, setDetailLoading] = useState(false)

  useEffect(() => {
    fetchBookmarks()
  }, [])

  const fetchBookmarks = async () => {
    try {
      setLoading(true)

      // Try to fetch from local backend server
      try {
        const response = await fetch("http://localhost:8080/api/bookmarks", {
          credentials: "include", // Include cookies with the request
        })

        if (response.status === 401) {
          // Redirect to login if unauthorized
          console.log("Unauthorized, redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return // Stop further execution
        }

        // Check if response is JSON
        const contentType = response.headers.get("content-type")
        if (contentType && contentType.includes("application/json") && response.ok) {
          const data = await response.json()
          setBookmarks(data)
          setError(null) // Clear previous errors on success
          return
        } else if (!response.ok) {
          // Handle other non-OK responses (e.g., 500)
          throw new Error(`HTTP error! status: ${response.status}`)
        }
      } catch (apiError) {
        // Handle network errors or errors thrown from response check
        if (apiError instanceof Error && apiError.message.includes("401")) {
          // This case might not be strictly necessary if 401 is handled above, but good for robustness
          console.log("Unauthorized (caught), redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        } else {
          console.warn("Local API not available or other error, using mock data:", apiError)
          // Fallback to mock data only if it's not an auth issue
          console.info("Using mock bookmark data for development")
          // Mock data based on the API documentation
          const mockBookmarks = [
            {
              id: 1,
              title: "Spring Boot Security",
              url: "https://spring.io/guides/gs/securing-web/",
              domain: "spring.io",
              notes: "Review OAuth2 configuration.",
              createdAt: "2025-05-03T10:15:30Z",
            },
            {
              id: 2,
              title: "React Hooks Tutorial",
              url: "https://reactjs.org/docs/hooks-intro.html",
              domain: "reactjs.org",
              notes: null,
              createdAt: "2025-05-02T14:20:00Z",
            },
            {
              id: 3,
              title: "Understanding JPA",
              url: "https://www.baeldung.com/jpa-intro",
              domain: "baeldung.com",
              notes: "Focus on entity relationships.",
              createdAt: "2025-05-03T11:05:00Z",
            },
          ]
          setBookmarks(mockBookmarks)
        }
      }
    } catch (err) {
      // Catch errors from setLoading etc. (less likely)
      console.error("Error in bookmarks handling:", err)
      setError("Failed to load bookmarks. Using default values.")
    } finally {
      setLoading(false)
    }
  }

  const handleDomainFilter = async (domain: string) => {
    try {
      setLoading(true)

      // Try to fetch from local backend server
      try {
        const response = await fetch(`http://localhost:8080/api/bookmarks/domain/${domain}`, {
          credentials: "include", // Include cookies
        })

        if (response.status === 401) {
          console.log("DomainFilter: Unauthorized, redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        }

        // Check if response is JSON
        const contentType = response.headers.get("content-type")
        if (contentType && contentType.includes("application/json") && response.ok) {
          const data = await response.json()
          setBookmarks(data)
          setError(null)
          return
        } else if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
      } catch (apiError) {
        if (apiError instanceof Error && apiError.message.includes("401")) {
          console.log("DomainFilter: Unauthorized (caught), redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        } else {
          console.warn("DomainFilter: Local API not available or error, using mock data:", apiError)
          // Fallback to mock data if API fails or returns non-JSON
          console.info(`Using mock bookmark data for domain: ${domain}`)
          // Filter the mock bookmarks by domain
          const mockBookmarks = [
            {
              id: 1,
              title: "Spring Boot Security",
              url: "https://spring.io/guides/gs/securing-web/",
              domain: "spring.io",
              notes: "Review OAuth2 configuration.",
              createdAt: "2025-05-03T10:15:30Z",
            },
            {
              id: 2,
              title: "React Hooks Tutorial",
              url: "https://reactjs.org/docs/hooks-intro.html",
              domain: "reactjs.org",
              notes: null,
              createdAt: "2025-05-02T14:20:00Z",
            },
            {
              id: 3,
              title: "Understanding JPA",
              url: "https://www.baeldung.com/jpa-intro",
              domain: "baeldung.com",
              notes: "Focus on entity relationships.",
              createdAt: "2025-05-03T11:05:00Z",
            },
          ]

          const filteredBookmarks = mockBookmarks.filter((bookmark) => bookmark.domain === domain)
          setBookmarks(filteredBookmarks)
        }
      }
    } catch (err) {
      console.error("Error in domain filtering:", err)
      setError("Failed to filter bookmarks. Using default values.")
    } finally {
      setLoading(false)
    }
  }

  const handleResetFilter = () => {
    fetchBookmarks()
  }

  const handleBookmarkClick = async (bookmark: BookmarkDTO) => {
    try {
      setSelectedBookmark(bookmark)
      setDetailLoading(true)

      // Try to fetch from local backend server
      try {
        const response = await fetch(
          `http://localhost:8080/api/bookmarks/website?url=${encodeURIComponent(bookmark.url)}`,
          {
            credentials: "include", // Include cookies
          },
        )

        if (response.status === 401) {
          console.log("BookmarkClick: Unauthorized, redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        }

        // Check if response is JSON
        const contentType = response.headers.get("content-type")
        if (contentType && contentType.includes("application/json") && response.ok) {
          const data = await response.json()
          setBookmarkDetail(data)
          setError(null)
          return
        } else if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
      } catch (apiError) {
        if (apiError instanceof Error && apiError.message.includes("401")) {
          console.log("BookmarkClick: Unauthorized (caught), redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        } else {
          console.warn("BookmarkClick: Local API not available or error, using mock data:", apiError)
          // Fallback to mock data if API fails or returns non-JSON
          console.info("Using mock bookmark detail data for development")
          // Mock data based on the API documentation
          const mockDetail = {
            bookmark: bookmark,
            flashCardSets: [
              { id: 10, name: "Security Concepts" },
              { id: 15, name: "OAuth2 Flow" },
            ],
            quizSets: [{ id: 5, name: "Web Security Quiz" }],
          }

          setBookmarkDetail(mockDetail)
        }
      }
    } catch (err) {
      console.error("Error in bookmark detail handling:", err)
      setError("Failed to load bookmark details. Using default values.")
    } finally {
      setDetailLoading(false)
    }
  }

  const handleDeleteBookmark = async (id: number) => {
    try {
      // Try to call the local backend server
      try {
        const response = await fetch(`http://localhost:8080/api/bookmarks/${id}`, {
          method: "DELETE",
          credentials: "include", // Include cookies
        })

        if (response.status === 401) {
          console.log("DeleteBookmark: Unauthorized, redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        }

        if (response.ok) {
          // If the deleted bookmark is currently selected, clear the selection
          if (selectedBookmark?.id === id) {
            setSelectedBookmark(null)
            setBookmarkDetail(null)
          }

          // Refresh the bookmarks list
          fetchBookmarks() // Assumes fetchBookmarks also handles 401
          setError(null)
          return
        } else if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
      } catch (apiError) {
        if (apiError instanceof Error && apiError.message.includes("401")) {
          console.log("DeleteBookmark: Unauthorized (caught), redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        } else {
          console.warn("DeleteBookmark: Local API not available or error, using mock behavior:", apiError)
          // Fallback behavior if API fails
          console.info("Using mock delete behavior for development")

          // If the deleted bookmark is currently selected, clear the selection
          if (selectedBookmark?.id === id) {
            setSelectedBookmark(null)
            setBookmarkDetail(null)
          }

          // Update the local state to remove the bookmark
          setBookmarks((prevBookmarks) => prevBookmarks.filter((bookmark) => bookmark.id !== id))
        }
      }
    } catch (err) {
      console.error("Error in delete handling:", err)
      setError("Failed to delete bookmark. Please try again.")
    }
  }

  const closeDetailPanel = () => {
    setSelectedBookmark(null)
    setBookmarkDetail(null)
  }

  return (
    <section className="container py-8" id="bookmarks">
      <h2 className="mb-6 text-2xl font-bold">Your Bookmarks</h2>

      <DomainFilter onSelectDomain={handleDomainFilter} onReset={handleResetFilter} />

      {error && <p className="my-4 text-red-500">{error}</p>}

      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-blue-500" />
        </div>
      ) : bookmarks.length === 0 ? (
        <p className="my-8 text-center text-muted-foreground">No bookmarks found. Start adding some!</p>
      ) : (
        <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {bookmarks.map((bookmark) => (
            <BookmarkCard
              key={bookmark.id}
              bookmark={bookmark}
              onClick={() => handleBookmarkClick(bookmark)}
              onDelete={() => handleDeleteBookmark(bookmark.id)}
              isSelected={selectedBookmark?.id === bookmark.id}
            />
          ))}
        </div>
      )}

      {selectedBookmark && (
        <BookmarkDetailPanel
          bookmark={selectedBookmark}
          detail={bookmarkDetail}
          loading={detailLoading}
          onClose={closeDetailPanel}
        />
      )}
    </section>
  )
}
