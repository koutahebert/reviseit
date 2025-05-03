"use client"

import Link from "next/link"
import { Github, Twitter, Linkedin, ArrowUp } from "lucide-react"
import { Button } from "@/components/ui/button"

export default function Footer() {
  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: "smooth" })
  }

  return (
    <footer className="border-t bg-background">
      <div className="container flex flex-col items-center justify-between gap-4 py-10 md:h-24 md:flex-row md:py-0">
        <div className="flex flex-col items-center gap-4 px-8 md:flex-row md:gap-2 md:px-0">
          <p className="text-center text-sm leading-loose text-muted-foreground md:text-left">
            © 2025 ReviseIt. All rights reserved.
          </p>
        </div>
        <div className="flex items-center gap-4">
          <Link href="https://github.com" target="_blank" rel="noreferrer">
            <Github className="h-5 w-5 text-muted-foreground transition-colors hover:text-foreground" />
            <span className="sr-only">GitHub</span>
          </Link>
          <Link href="https://twitter.com" target="_blank" rel="noreferrer">
            <Twitter className="h-5 w-5 text-muted-foreground transition-colors hover:text-foreground" />
            <span className="sr-only">Twitter</span>
          </Link>
          <Link href="https://linkedin.com" target="_blank" rel="noreferrer">
            <Linkedin className="h-5 w-5 text-muted-foreground transition-colors hover:text-foreground" />
            <span className="sr-only">LinkedIn</span>
          </Link>
          <Button variant="ghost" size="icon" aria-label="Back to top" onClick={scrollToTop} className="ml-2">
            <ArrowUp className="h-5 w-5" />
            <span className="sr-only">Back to top</span>
          </Button>
        </div>
      </div>
    </footer>
  )
}
