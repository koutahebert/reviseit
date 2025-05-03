import Link from "next/link"
import Image from "next/image" // Import the Next.js Image component
import { Button } from "@/components/ui/button"

export default function Hero() {
  return (
    <section className="bg-gradient-to-b from-blue-50 to-white py-20 dark:from-gray-900 dark:to-background">
      <div className="container px-4 md:px-6">
        <div className="flex flex-col items-center space-y-4 text-center">
          <div className="space-y-2">
            <div className="flex items-center justify-center space-x-2">
              <Image src="/icon.png" alt="ReviseIt Logo" width={50} height={50} />
              <h1 className="text-4xl font-bold tracking-tighter sm:text-5xl md:text-6xl">
                Revise<span className="text-blue-500 dark:text-blue-400">It</span>
              </h1>
            </div>
            <p className="mx-auto max-w-[700px] text-gray-500 dark:text-gray-400 md:text-xl">
              Turn your bookmarks into flashcards and quizzes
            </p>
          </div>
          <div className="space-x-4">
            <Button asChild size="lg" className="px-8">
              <Link href="/signup">Get Started</Link>
            </Button>
          </div>
        </div>
      </div>
    </section>
  )
}
