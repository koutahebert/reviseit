"use client"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Loader2, RefreshCw } from "lucide-react"

interface DomainFilterProps {
  onSelectDomain: (domain: string) => void
  onReset: () => void
}

export default function DomainFilter({ onSelectDomain, onReset }: DomainFilterProps) {
  const [domains, setDomains] = useState<string[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [activeDomain, setActiveDomain] = useState<string | null>(null)

  useEffect(() => {
    fetchDomains()
  }, [])

  const fetchDomains = async () => {
    try {
      setLoading(true)

      // Try to fetch from local backend server
      try {
        const response = await fetch("http://localhost:8080/api/bookmarks/domains")

        // Check if response is JSON by looking at content-type header
        const contentType = response.headers.get("content-type")
        if (contentType && contentType.includes("application/json") && response.ok) {
          const data = await response.json()
          setDomains(data)
          return
        }
      } catch (apiError) {
        console.warn("Local API not available, using mock data:", apiError)
      }

      // Fallback to mock data if API fails or returns non-JSON
      console.info("Using mock domain data for development")
      // Mock data based on the API documentation
      const mockDomains = ["spring.io", "reactjs.org", "baeldung.com"]
      setDomains(mockDomains)
    } catch (err) {
      console.error("Error in domain handling:", err)
      setError("Failed to load domains. Using default values.")
    } finally {
      setLoading(false)
    }
  }

  const handleDomainClick = (domain: string) => {
    setActiveDomain(domain)
    onSelectDomain(domain)
  }

  const handleReset = () => {
    setActiveDomain(null)
    onReset()
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-4">
        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
        <span>Loading domains...</span>
      </div>
    )
  }

  if (error) {
    return <p className="py-2 text-sm text-red-500">{error}</p>
  }

  if (domains.length === 0) {
    return <p className="py-2 text-sm text-muted-foreground">No domains available</p>
  }

  return (
    <div className="mb-6" id="domains">
      <div className="mb-2 flex items-center justify-between">
        <h3 className="text-sm font-medium">Filter by Domain:</h3>
        <Button variant="ghost" size="sm" onClick={handleReset} disabled={!activeDomain}>
          <RefreshCw className="mr-1 h-3 w-3" />
          Reset
        </Button>
      </div>
      <div className="flex flex-wrap gap-2">
        {domains.map((domain) => (
          <Button
            key={domain}
            variant={activeDomain === domain ? "default" : "outline"}
            size="sm"
            className="rounded-full"
            onClick={() => handleDomainClick(domain)}
          >
            {domain}
          </Button>
        ))}
      </div>
    </div>
  )
}
