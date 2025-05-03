"use client"

import { useState } from "react"
import type { BookmarkDTO, BookmarkDetailDTO, FlashCardDTO, QuizSetDTO } from "@/types/bookmark"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Button } from "@/components/ui/button"
import { Loader2, X, ExternalLink, BookOpen, FileQuestion } from "lucide-react"
import FlashcardList from "@/components/flashcard-list"
import QuizView from "@/components/quiz-view"

interface BookmarkDetailPanelProps {
  bookmark: BookmarkDTO
  detail: BookmarkDetailDTO | null
  loading: boolean
  onClose: () => void
}

export default function BookmarkDetailPanel({ bookmark, detail, loading, onClose }: BookmarkDetailPanelProps) {
  const [activeTab, setActiveTab] = useState("flashcards")
  const [selectedFlashcardSetId, setSelectedFlashcardSetId] = useState<number | null>(null)
  const [flashcards, setFlashcards] = useState<FlashCardDTO[]>([])
  const [flashcardsLoading, setFlashcardsLoading] = useState(false)

  const [selectedQuizSetId, setSelectedQuizSetId] = useState<number | null>(null)
  const [quizSet, setQuizSet] = useState<QuizSetDTO | null>(null)
  const [quizLoading, setQuizLoading] = useState(false)

  const handleViewFlashcards = async (setId: number) => {
    try {
      setFlashcardsLoading(true)
      setSelectedFlashcardSetId(setId)

      // Try to fetch from local backend server
      try {
        const response = await fetch(`http://localhost:8080/api/flashcards/set/${setId}`, {
          credentials: "include", // Include cookies
        })

        if (response.status === 401) {
          console.log("Flashcards: Unauthorized, redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        }

        // Check if response is JSON
        const contentType = response.headers.get("content-type")
        if (contentType && contentType.includes("application/json") && response.ok) {
          const data = await response.json()
          setFlashcards(data)
          return
        } else if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
      } catch (apiError) {
        if (apiError instanceof Error && apiError.message.includes("401")) {
          console.log("Flashcards: Unauthorized (caught), redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        } else {
          console.warn("Flashcards: Local API not available or error, using mock data:", apiError)
          // Fallback to mock data if API fails or returns non-JSON
          console.info("Using mock flashcard data for development")
          // Mock data based on the API documentation
          const mockFlashcards = [
            {
              id: 101,
              question: "What is OAuth2?",
              answer: "An authorization framework enabling applications to obtain limited access to user accounts.",
            },
            {
              id: 102,
              question: "Difference between Authentication and Authorization?",
              answer: "Authentication verifies identity, Authorization verifies permissions.",
            },
            {
              id: 103,
              question: "What is a JWT token?",
              answer:
                "JSON Web Token is a compact, URL-safe means of representing claims to be transferred between two parties.",
            },
          ]

          setFlashcards(mockFlashcards)
        }
      }
    } catch (err) {
      console.error("Error in flashcard handling:", err)
    } finally {
      setFlashcardsLoading(false)
    }
  }

  const handleStartQuiz = async (setId: number) => {
    try {
      setQuizLoading(true)
      setSelectedQuizSetId(setId)

      // Try to fetch from local backend server
      try {
        const response = await fetch(`http://localhost:8080/api/quizzes/set/${setId}`, {
          credentials: "include", // Include cookies
        })

        if (response.status === 401) {
          console.log("Quiz: Unauthorized, redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        }

        // Check if response is JSON
        const contentType = response.headers.get("content-type")
        if (contentType && contentType.includes("application/json") && response.ok) {
          const data = await response.json()
          setQuizSet(data)
          return
        } else if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
      } catch (apiError) {
        if (apiError instanceof Error && apiError.message.includes("401")) {
          console.log("Quiz: Unauthorized (caught), redirecting to login...")
          window.location.href = "http://localhost:8080/oauth2/authorization/google"
          return
        } else {
          console.warn("Quiz: Local API not available or error, using mock data:", apiError)
          // Fallback to mock data if API fails or returns non-JSON
          console.info("Using mock quiz data for development")
          // Mock data based on the API documentation
          const mockQuizSet = {
            id: 5,
            name: "Web Security Quiz",
            questions: [
              {
                id: 201,
                text: "Which HTTP method is typically used for retrieving data?",
                choices: [
                  { id: 301, text: "POST", isCorrect: false },
                  { id: 302, text: "GET", isCorrect: true },
                  { id: 303, text: "PUT", isCorrect: false },
                  { id: 304, text: "DELETE", isCorrect: false },
                ],
              },
              {
                id: 202,
                text: "What does CSRF stand for?",
                choices: [
                  { id: 305, text: "Cross-Site Reference Forgery", isCorrect: false },
                  { id: 306, text: "Client-Side Request Forgery", isCorrect: false },
                  { id: 307, text: "Cross-Site Request Forgery", isCorrect: true },
                ],
              },
            ],
          }

          setQuizSet(mockQuizSet)
        }
      }
    } catch (err) {
      console.error("Error in quiz handling:", err)
    } finally {
      setQuizLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <Card className="relative max-h-[90vh] w-full max-w-4xl overflow-hidden dark:border-gray-700">
        <Button variant="ghost" size="icon" className="absolute right-2 top-2 z-10" onClick={onClose}>
          <X className="h-4 w-4" />
          <span className="sr-only">Close</span>
        </Button>

        <CardHeader className="bg-muted/50">
          <CardTitle className="flex items-start justify-between">
            <div>
              <div className="mb-1 text-sm font-normal text-muted-foreground">{bookmark.domain}</div>
              {bookmark.title}
            </div>
            <Button variant="outline" size="sm" asChild className="ml-4 shrink-0">
              <a href={bookmark.url} target="_blank" rel="noopener noreferrer">
                <ExternalLink className="mr-1 h-3 w-3" />
                Visit
              </a>
            </Button>
          </CardTitle>
        </CardHeader>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="grid w-full grid-cols-2 tabs-list dark:border-gray-700">
            <TabsTrigger value="flashcards" className="flex items-center gap-1">
              <BookOpen className="h-3 w-3" />
              Flashcards
            </TabsTrigger>
            <TabsTrigger value="quizzes" className="flex items-center gap-1">
              <FileQuestion className="h-3 w-3" />
              Quizzes
            </TabsTrigger>
          </TabsList>

          <CardContent className="max-h-[70vh] overflow-y-auto p-0">
            <TabsContent value="flashcards" className="p-6">
              {bookmark.notes && (
                <div className="mb-6">
                  <h3 className="mb-2 text-sm font-medium">Notes:</h3>
                  <p className="text-sm text-muted-foreground">{bookmark.notes}</p>
                </div>
              )}

              {flashcardsLoading ? (
                <div className="flex h-40 items-center justify-center">
                  <Loader2 className="h-8 w-8 animate-spin text-blue-500" />
                </div>
              ) : selectedFlashcardSetId ? (
                <FlashcardList flashcards={flashcards} />
              ) : (
                <div>
                  <h3 className="mb-3 text-sm font-medium">Flashcard Sets</h3>
                  {detail?.flashCardSets?.length ? (
                    <ul className="space-y-2">
                      {detail.flashCardSets.map((set) => (
                        <li
                          key={set.id}
                          className="flex items-center justify-between rounded-md border p-3 dark:border-gray-700"
                        >
                          <span>
                            {set.name}
                            {set.numberOfFlashcards !== undefined && (
                              <span className="ml-2 text-xs text-muted-foreground">({set.numberOfFlashcards} cards)</span>
                            )}
                          </span>
                          <Button size="sm" onClick={() => handleViewFlashcards(set.id)}>
                            View
                          </Button>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-sm text-muted-foreground">No flashcard sets available</p>
                  )}
                </div>
              )}
            </TabsContent>

            <TabsContent value="quizzes" className="p-6">
              {quizLoading ? (
                <div className="flex h-40 items-center justify-center">
                  <Loader2 className="h-8 w-8 animate-spin text-blue-500" />
                </div>
              ) : selectedQuizSetId && quizSet ? (
                <QuizView quizSet={quizSet} />
              ) : (
                <div>
                  <h3 className="mb-3 text-sm font-medium">Quiz Sets</h3>
                  {detail?.quizSets?.length ? (
                    <ul className="space-y-2">
                      {detail.quizSets.map((set) => (
                        <li
                          key={set.id}
                          className="flex items-center justify-between rounded-md border p-3 dark:border-gray-700"
                        >
                          <span>
                            {set.name}
                            {set.questions && (
                              <span className="ml-2 text-xs text-muted-foreground">({set.questions.length} questions)</span>
                            )}
                          </span>
                          <Button size="sm" onClick={() => handleStartQuiz(set.id)}>
                            Start Quiz
                          </Button>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-sm text-muted-foreground">No quiz sets available</p>
                  )}
                </div>
              )}
            </TabsContent>
          </CardContent>
        </Tabs>
      </Card>
    </div>
  )
}
