"use client"

import { useState } from "react"
import type { FlashCardDTO } from "@/types/bookmark"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight, RotateCw } from "lucide-react"

interface FlashcardListProps {
  flashcards: FlashCardDTO[]
}

export default function FlashcardList({ flashcards }: FlashcardListProps) {
  const [currentIndex, setCurrentIndex] = useState(0)
  const [flipped, setFlipped] = useState(false)

  if (!flashcards.length) {
    return <p className="text-center text-muted-foreground">No flashcards available</p>
  }

  const currentCard = flashcards[currentIndex]

  const handleNext = () => {
    if (currentIndex < flashcards.length - 1) {
      setCurrentIndex(currentIndex + 1)
      setFlipped(false)
    }
  }

  const handlePrevious = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1)
      setFlipped(false)
    }
  }

  const toggleFlip = () => {
    setFlipped(!flipped)
  }

  return (
    <div className="space-y-6">
      <div className="text-center">
        <p className="text-sm text-muted-foreground">
          Card {currentIndex + 1} of {flashcards.length}
        </p>
      </div>

      <Card
        className="mx-auto min-h-[200px] w-full max-w-md cursor-pointer transition-all duration-300 dark:border-gray-700"
        onClick={toggleFlip}
      >
        <CardContent className="flex min-h-[200px] items-center justify-center p-6 text-center">
          <div className="w-full">
            {flipped ? (
              <div className="animate-fade-in">
                <h4 className="mb-2 text-sm font-medium text-muted-foreground">Answer:</h4>
                <p>{currentCard.answer}</p>
              </div>
            ) : (
              <div>
                <h4 className="mb-2 text-sm font-medium text-muted-foreground">Question:</h4>
                <p>{currentCard.question}</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      <div className="flex items-center justify-center gap-4">
        <Button
          variant="outline"
          size="icon"
          onClick={handlePrevious}
          disabled={currentIndex === 0}
          className="dark:border-gray-700"
        >
          <ChevronLeft className="h-4 w-4" />
          <span className="sr-only">Previous</span>
        </Button>

        <Button variant="outline" size="sm" onClick={toggleFlip} className="dark:border-gray-700">
          <RotateCw className="mr-1 h-3 w-3" />
          Flip
        </Button>

        <Button
          variant="outline"
          size="icon"
          onClick={handleNext}
          disabled={currentIndex === flashcards.length - 1}
          className="dark:border-gray-700"
        >
          <ChevronRight className="h-4 w-4" />
          <span className="sr-only">Next</span>
        </Button>
      </div>
    </div>
  )
}
