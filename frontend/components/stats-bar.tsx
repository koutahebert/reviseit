"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Bookmark, BookOpen, FileQuestion } from "lucide-react"
import { Skeleton } from "@/components/ui/skeleton"

export default function StatsBar() {
  const [stats, setStats] = useState({
    bookmarks: 0,
    flashcards: 0,
    quizzes: 0,
  })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true)

        let bookmarksCount = 0

        // Try to fetch bookmarks from local backend server
        try {
          const bookmarksResponse = await fetch("http://localhost:8080/api/bookmarks")

          // Check if response is JSON
          const contentType = bookmarksResponse.headers.get("content-type")
          if (contentType && contentType.includes("application/json") && bookmarksResponse.ok) {
            const bookmarksData = await bookmarksResponse.json()
            bookmarksCount = bookmarksData.length
          } else {
            // Use mock data if API fails
            bookmarksCount = 3 // Mock count
          }
        } catch (apiError) {
          console.warn("Local API not available, using mock data:", apiError)
          bookmarksCount = 3 // Mock count if API fails
        }

        // For flashcards and quizzes, we'd need to aggregate from all sets
        // This is a simplified version - in a real app, you might have dedicated endpoints

        // Simulate flashcard count (in a real app, you'd fetch all sets and count cards)
        const flashcardsCount = 24 // Placeholder

        // Simulate quiz questions count (in a real app, you'd fetch all sets and count questions)
        const quizzesCount = 15 // Placeholder

        setStats({
          bookmarks: bookmarksCount,
          flashcards: flashcardsCount,
          quizzes: quizzesCount,
        })
      } catch (err) {
        console.error("Error in stats handling:", err)
        setError("Failed to load statistics. Using default values.")

        // Set default values even if there's an error
        setStats({
          bookmarks: 3,
          flashcards: 24,
          quizzes: 15,
        })
      } finally {
        setLoading(false)
      }
    }

    fetchStats()
  }, [])

  return (
    <section className="container py-8">
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <StatCard icon={<Bookmark className="h-6 w-6" />} title="Bookmarks" value={stats.bookmarks} loading={loading} />
        <StatCard
          icon={<BookOpen className="h-6 w-6" />}
          title="Flashcards"
          value={stats.flashcards}
          loading={loading}
        />
        <StatCard
          icon={<FileQuestion className="h-6 w-6" />}
          title="Quiz Questions"
          value={stats.quizzes}
          loading={loading}
        />
      </div>
      {error && <p className="mt-4 text-center text-sm text-red-500">{error}</p>}
    </section>
  )
}

interface StatCardProps {
  icon: React.ReactNode
  title: string
  value: number
  loading: boolean
}

function StatCard({ icon, title, value, loading }: StatCardProps) {
  return (
    <Card>
      <CardContent className="flex items-center justify-between p-6">
        <div className="flex items-center space-x-4">
          <div className="rounded-full bg-blue-100 p-2 dark:bg-blue-900">{icon}</div>
          <div>
            <p className="text-sm font-medium text-muted-foreground">{title}</p>
            {loading ? <Skeleton className="h-8 w-16" /> : <h3 className="text-2xl font-bold">{value}</h3>}
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
