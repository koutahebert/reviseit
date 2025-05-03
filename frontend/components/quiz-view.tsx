"use client"

import { useState } from "react"
import type { QuizSetDTO } from "@/types/bookmark"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { CheckCircle, XCircle } from "lucide-react"

interface QuizViewProps {
  quizSet: QuizSetDTO
}

export default function QuizView({ quizSet }: QuizViewProps) {
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0)
  const [selectedChoiceId, setSelectedChoiceId] = useState<number | null>(null)
  const [showResult, setShowResult] = useState(false)
  const [score, setScore] = useState(0)
  const [completed, setCompleted] = useState(false)
  const [answers, setAnswers] = useState<Record<number, number>>({})

  if (!quizSet.questions || quizSet.questions.length === 0) {
    return <p className="text-center text-muted-foreground">No questions available in this quiz</p>
  }

  const currentQuestion = quizSet.questions[currentQuestionIndex]

  const handleSelectChoice = (choiceId: number) => {
    if (showResult) return
    setSelectedChoiceId(choiceId)
  }

  const handleCheckAnswer = () => {
    if (!selectedChoiceId) return

    const isCorrect = currentQuestion.choices.find((choice) => choice.id === selectedChoiceId)?.isCorrect

    // Save the answer
    setAnswers({
      ...answers,
      [currentQuestion.id]: selectedChoiceId,
    })

    if (isCorrect) {
      setScore(score + 1)
    }

    setShowResult(true)
  }

  const handleNextQuestion = () => {
    if (currentQuestionIndex < quizSet.questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1)
      setSelectedChoiceId(null)
      setShowResult(false)
    } else {
      setCompleted(true)
    }
  }

  const handleRestartQuiz = () => {
    setCurrentQuestionIndex(0)
    setSelectedChoiceId(null)
    setShowResult(false)
    setScore(0)
    setCompleted(false)
    setAnswers({})
  }

  if (completed) {
    return (
      <div className="space-y-6">
        <Card className="mx-auto max-w-md dark:border-gray-700">
          <CardHeader>
            <CardTitle className="text-center">Quiz Completed!</CardTitle>
          </CardHeader>
          <CardContent className="text-center">
            <p className="mb-4 text-2xl font-bold">
              Your Score: {score} / {quizSet.questions.length}
            </p>
            <p className="mb-6 text-muted-foreground">
              {score === quizSet.questions.length
                ? "Perfect score! Excellent work!"
                : score > quizSet.questions.length / 2
                  ? "Good job! Keep practicing to improve."
                  : "Keep studying and try again!"}
            </p>
            <Button onClick={handleRestartQuiz}>Restart Quiz</Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="text-center">
        <p className="text-sm text-muted-foreground">
          Question {currentQuestionIndex + 1} of {quizSet.questions.length}
        </p>
      </div>

      <Card className="mx-auto max-w-md dark:border-gray-700">
        <CardHeader>
          <CardTitle className="text-lg">{currentQuestion.text}</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {currentQuestion.choices.map((choice) => {
            const isSelected = selectedChoiceId === choice.id
            const isCorrect = choice.isCorrect
            let className =
              "flex items-center justify-between rounded-md border p-3 transition-colors dark:border-gray-700"

            if (showResult) {
              if (isCorrect) {
                className += " bg-green-50 border-green-200 dark:bg-green-950 dark:border-green-800"
              } else if (isSelected && !isCorrect) {
                className += " bg-red-50 border-red-200 dark:bg-red-950 dark:border-red-800"
              }
            } else if (isSelected) {
              className += " bg-blue-50 border-blue-200 dark:bg-blue-950 dark:border-blue-800"
            } else {
              className += " hover:bg-muted/50"
            }

            return (
              <div key={choice.id} className={className} onClick={() => handleSelectChoice(choice.id)}>
                <span>{choice.text}</span>
                {showResult && isCorrect && <CheckCircle className="h-5 w-5 text-green-500" />}
                {showResult && isSelected && !isCorrect && <XCircle className="h-5 w-5 text-red-500" />}
              </div>
            )
          })}
        </CardContent>
      </Card>

      <div className="flex justify-center">
        {!showResult ? (
          <Button onClick={handleCheckAnswer} disabled={!selectedChoiceId}>
            Check Answer
          </Button>
        ) : (
          <Button onClick={handleNextQuestion}>
            {currentQuestionIndex < quizSet.questions.length - 1 ? "Next Question" : "Finish Quiz"}
          </Button>
        )}
      </div>
    </div>
  )
}
