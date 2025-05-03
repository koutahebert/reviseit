import Hero from "@/components/hero"
import BookmarksDashboard from "@/components/bookmarks-dashboard"
import StatsBar from "@/components/stats-bar"

export default function Home() {
  return (
    <main className="min-h-screen">
      <Hero />
      <StatsBar />
      <BookmarksDashboard />
    </main>
  )
}
