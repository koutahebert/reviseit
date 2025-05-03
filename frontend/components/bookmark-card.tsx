"use client"

import type React from "react"

import { formatDistanceToNow } from "date-fns"
import type { BookmarkDTO } from "@/types/bookmark"
import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Trash2 } from "lucide-react"

interface BookmarkCardProps {
  bookmark: BookmarkDTO
  onClick: () => void
  onDelete: () => void
  isSelected: boolean
}

export default function BookmarkCard({ bookmark, onClick, onDelete, isSelected }: BookmarkCardProps) {
  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation()
    onDelete()
  }

  return (
    <Card
      className={`cursor-pointer transition-all hover:shadow-md ${
        isSelected
          ? "border-blue-500 ring-2 ring-blue-200 dark:border-blue-400 dark:ring-blue-900"
          : "dark:border-gray-700"
      }`}
      onClick={onClick}
    >
      <CardContent className="p-6">
        <div className="mb-2 flex items-center justify-between">
          <span className="rounded-full bg-blue-100 px-2 py-1 text-xs font-medium text-blue-800 dark:bg-blue-900 dark:text-blue-200">
            {bookmark.domain}
          </span>
          <span className="text-xs text-muted-foreground">
            {formatDistanceToNow(new Date(bookmark.createdAt), { addSuffix: true })}
          </span>
        </div>
        <h3 className="mb-2 line-clamp-2 text-lg font-semibold">{bookmark.title}</h3>
        {bookmark.notes && <p className="line-clamp-3 text-sm text-muted-foreground">{bookmark.notes}</p>}
      </CardContent>
      <CardFooter className="flex justify-between p-4 pt-0">
        <Button variant="ghost" size="sm" onClick={onClick}>
          View Details
        </Button>
        <Button
          variant="ghost"
          size="icon"
          onClick={handleDelete}
          className="text-red-500 hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-950"
        >
          <Trash2 className="h-4 w-4" />
          <span className="sr-only">Delete</span>
        </Button>
      </CardFooter>
    </Card>
  )
}
