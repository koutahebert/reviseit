export interface BookmarkDTO {
  id: number
  title: string
  url: string
  domain: string
  notes: string | null
  createdAt: string
}

export interface FlashCardSetDTO {
  id: number
  name: string
  numberOfFlashcards?: number // Add optional count field
}

export interface QuizSetDTO {
  id: number
  name: string
  questions: QuestionDTO[]
}

export interface FlashCardDTO {
  id: number
  question: string
  answer: string
}

export interface QuestionDTO {
  id: number
  text: string
  choices: ChoiceDTO[]
}

export interface ChoiceDTO {
  id: number
  text: string
  isCorrect: boolean
}

export interface BookmarkDetailDTO {
  bookmark: BookmarkDTO
  flashCardSets: FlashCardSetDTO[]
  quizSets: QuizSetDTO[]
}
