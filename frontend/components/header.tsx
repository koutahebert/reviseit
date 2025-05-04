"use client"

import { useState } from "react"
import Link from "next/link"
import Image from "next/image" // Import the Next.js Image component
import { Menu, X } from "lucide-react"
import { Button } from "@/components/ui/button"
import { ThemeToggle } from "@/components/theme-toggle"

// Define the text logo component inline or separately if preferred
const Logo = () => (
  <div className="flex items-center space-x-2">
    <Image src="/icon.png" alt="ReviseIt Logo" width={30} height={30} />
    <h1 className="text-4xl font-bold tracking-tighter">
      Revise<span className="text-blue-500 dark:text-blue-400">It</span>
    </h1>
  </div>
)

const navLinks = [
  { name: "Home", href: "/" },
  { name: "Bookmarks", href: "#bookmarks" },
  { name: "Domains", href: "#domains" },
]

export default function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-16 items-center justify-between">
        {/* Left */}
        <div className="flex flex-1 items-center">
          <Link href="/" className="flex items-center space-x-2">
            <Logo />
          </Link>
        </div>

        {/* Center */}
        <nav className="hidden md:flex flex-1 justify-center gap-10">
          {navLinks.map((link) => (
            <Link
              key={link.name}
              href={link.href}
              className="text-base font-medium transition-colors hover:text-primary relative py-2 px-1 hover:after:content-[''] hover:after:absolute hover:after:left-0 hover:after:bottom-0 hover:after:w-full hover:after:h-0.5 hover:after:bg-primary"
            >
              {link.name}
            </Link>
          ))}
        </nav>

        {/* Right */}
        <div className="flex flex-1 justify-end items-center gap-2">
          <ThemeToggle className="hidden md:flex" />
          <div className="flex md:hidden">
            <ThemeToggle />
            <Button
              variant="ghost"
              size="icon"
              aria-label="Toggle Menu"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="border-2 border-gray-300 dark:border-gray-700"
            >
              {mobileMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
            </Button>
          </div>
        </div>
      </div>

      {/* Mobile navigation */}
      {mobileMenuOpen && (
        <div className="container md:hidden">
          <div className="flex flex-col space-y-3 pb-3 pt-2">
            {navLinks.map((link) => (
              <Link
                key={link.name}
                href={link.href}
                className="text-sm font-medium transition-colors hover:text-primary relative py-2 px-1 hover:after:content-[''] hover:after:absolute hover:after:left-0 hover:after:bottom-0 hover:after:w-full hover:after:h-0.5 hover:after:bg-primary"
                onClick={() => setMobileMenuOpen(false)}
              >
                {link.name}
              </Link>
            ))}
            <Button asChild className="mt-2 w-full border-2 border-primary">
              <Link href="/signup">Get Started</Link>
            </Button>
          </div>
        </div>
      )}
    </header>
  )
}
