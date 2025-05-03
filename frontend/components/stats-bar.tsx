"use client"

import type React from "react"
import { useEffect, useState } from "react"
import Image from "next/image" // Import the Next.js Image component
import { Card, CardContent } from "@/components/ui/card"
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
        setError(null) // Clear previous errors

        // Fetch stats from the backend
        const statsResponse = await fetch("http://localhost:8080/api/bookmarks/stats", {
          credentials: "include", // Include cookies
        })

        if (statsResponse.status === 401) {
          console.log("StatsBar: Unauthorized, redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        }

        if (!statsResponse.ok) {
          throw new Error(`HTTP error! status: ${statsResponse.status}`)
        }

        const statsData = await statsResponse.json()

        // Update state with fetched data
        setStats({
          bookmarks: statsData.totalBookmarks,
          flashcards: statsData.totalFlashcards,
          quizzes: statsData.totalQuizQuestions, // Assuming the DTO field is totalQuizQuestions
        })
      } catch (err) {
        console.error("Error fetching stats:", err)
        setError("Failed to load statistics. Displaying default values.")
        // Set default/fallback values on error
        setStats({
          bookmarks: 0,
          flashcards: 0,
          quizzes: 0,
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
        <StatCard
          icon={<Image src="/bookmark.png" alt="Bookmarks Icon" width={24} height={24} />}
          title="Bookmarks"
          value={stats.bookmarks}
          loading={loading}
        />
        <StatCard
          icon={<Image src="/cards.png" alt="Flashcards Icon" width={24} height={24} />}
          title="Flashcards"
          value={stats.flashcards}
          loading={loading}
        />
        <StatCard
          icon={<Image src="/bulb.png" alt="Quiz Questions Icon" width={24} height={24} />}
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
