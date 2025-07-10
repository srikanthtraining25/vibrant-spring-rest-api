
import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useToast } from "@/hooks/use-toast";
import { BookOpen, Plus, Search, Edit, Trash2, Calendar } from "lucide-react";

interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  publicationYear: number;
  genre: string;
  description: string;
  createdAt: string;
  updatedAt: string;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

const API_BASE_URL = "http://localhost:8080/api";

const Index = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [searchType, setSearchType] = useState<"author" | "genre" | "">("");
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    title: "",
    author: "",
    isbn: "",
    publicationYear: new Date().getFullYear(),
    genre: "",
    description: ""
  });

  const { toast } = useToast();
  const queryClient = useQueryClient();

  // Fetch books with optional filtering
  const { data: booksResponse, isLoading, error } = useQuery({
    queryKey: ["books", searchTerm, searchType],
    queryFn: async (): Promise<ApiResponse<Book[]>> => {
      let url = `${API_BASE_URL}/books`;
      const params = new URLSearchParams();
      
      if (searchTerm && searchType) {
        params.append(searchType, searchTerm);
      }
      
      if (params.toString()) {
        url += `?${params.toString()}`;
      }

      const response = await fetch(url);
      if (!response.ok) {
        throw new Error("Failed to fetch books");
      }
      return response.json();
    },
  });

  // Create book mutation
  const createBookMutation = useMutation({
    mutationFn: async (book: Omit<Book, "id" | "createdAt" | "updatedAt">): Promise<ApiResponse<Book>> => {
      const response = await fetch(`${API_BASE_URL}/books`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(book),
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to create book");
      }
      
      return response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["books"] });
      toast({
        title: "Success",
        description: "Book created successfully!",
      });
      resetForm();
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message,
        variant: "destructive",
      });
    },
  });

  // Update book mutation
  const updateBookMutation = useMutation({
    mutationFn: async ({ bookId, book }: { bookId: number; book: Omit<Book, "id" | "createdAt" | "updatedAt"> }): Promise<ApiResponse<Book>> => {
      const response = await fetch(`${API_BASE_URL}/books/${bookId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(book),
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to update book");
      }
      
      return response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["books"] });
      toast({
        title: "Success",
        description: "Book updated successfully!",
      });
      setIsEditing(false);
      setSelectedBook(null);
      resetForm();
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message,
        variant: "destructive",
      });
    },
  });

  // Delete book mutation
  const deleteBookMutation = useMutation({
    mutationFn: async (bookId: number): Promise<ApiResponse<void>> => {
      const response = await fetch(`${API_BASE_URL}/books/${bookId}`, {
        method: "DELETE",
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to delete book");
      }
      
      return response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["books"] });
      toast({
        title: "Success",
        description: "Book deleted successfully!",
      });
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message,
        variant: "destructive",
      });
    },
  });

  const resetForm = () => {
    setFormData({
      title: "",
      author: "",
      isbn: "",
      publicationYear: new Date().getFullYear(),
      genre: "",
      description: ""
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (isEditing && selectedBook) {
      updateBookMutation.mutate({ bookId: selectedBook.id, book: formData });
    } else {
      createBookMutation.mutate(formData);
    }
  };

  const handleEdit = (book: Book) => {
    setSelectedBook(book);
    setFormData({
      title: book.title,
      author: book.author,
      isbn: book.isbn,
      publicationYear: book.publicationYear,
      genre: book.genre,
      description: book.description
    });
    setIsEditing(true);
  };

  const handleDelete = (book: Book) => {
    if (window.confirm(`Are you sure you want to delete "${book.title}"?`)) {
      deleteBookMutation.mutate(book.id);
    }
  };

  const books = booksResponse?.data || [];

  if (error) {
    return (
      <div className="container mx-auto p-4">
        <Card>
          <CardContent className="p-6">
            <p className="text-red-500">Error loading books: {error.message}</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4 space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <BookOpen className="h-6 w-6" />
          <h1 className="text-3xl font-bold">Book Library API</h1>
        </div>
        <Badge variant="secondary">
          {books.length} book{books.length !== 1 ? 's' : ''}
        </Badge>
      </div>

      {/* Search Section */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Search className="h-5 w-5" />
            <span>Search Books</span>
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex space-x-4">
            <div className="flex-1">
              <Input
                placeholder="Search books..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <select
              className="px-3 py-2 border rounded-md"
              value={searchType}
              onChange={(e) => setSearchType(e.target.value as "author" | "genre" | "")}
            >
              <option value="">All Fields</option>
              <option value="author">Author</option>
              <option value="genre">Genre</option>
            </select>
          </div>
        </CardContent>
      </Card>

      {/* Add/Edit Book Form */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Plus className="h-5 w-5" />
            <span>{isEditing ? "Edit Book" : "Add New Book"}</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-1">Title</label>
                <Input
                  required
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="Book title"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Author</label>
                <Input
                  required
                  value={formData.author}
                  onChange={(e) => setFormData({ ...formData, author: e.target.value })}
                  placeholder="Author name"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">ISBN</label>
                <Input
                  required
                  value={formData.isbn}
                  onChange={(e) => setFormData({ ...formData, isbn: e.target.value })}
                  placeholder="ISBN"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Publication Year</label>
                <Input
                  type="number"
                  required
                  value={formData.publicationYear}
                  onChange={(e) => setFormData({ ...formData, publicationYear: parseInt(e.target.value) })}
                  placeholder="Year"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Genre</label>
                <Input
                  value={formData.genre}
                  onChange={(e) => setFormData({ ...formData, genre: e.target.value })}
                  placeholder="Genre"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Description</label>
              <textarea
                className="w-full px-3 py-2 border rounded-md"
                rows={3}
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Book description"
              />
            </div>
            <div className="flex space-x-2">
              <Button 
                type="submit" 
                disabled={createBookMutation.isPending || updateBookMutation.isPending}
              >
                {isEditing ? "Update Book" : "Add Book"}
              </Button>
              {isEditing && (
                <Button 
                  type="button" 
                  variant="outline"
                  onClick={() => {
                    setIsEditing(false);
                    setSelectedBook(null);
                    resetForm();
                  }}
                >
                  Cancel
                </Button>
              )}
            </div>
          </form>
        </CardContent>
      </Card>

      {/* Books List */}
      <Card>
        <CardHeader>
          <CardTitle>Books Collection</CardTitle>
          <CardDescription>
            {isLoading ? "Loading books..." : `Found ${books.length} book${books.length !== 1 ? 's' : ''}`}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-2">
              {[...Array(3)].map((_, i) => (
                <div key={i} className="h-20 bg-gray-100 rounded animate-pulse" />
              ))}
            </div>
          ) : books.length === 0 ? (
            <p className="text-gray-500 text-center py-8">
              No books found. Add your first book above!
            </p>
          ) : (
            <div className="space-y-4">
              {books.map((book) => (
                <div key={book.id} className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h3 className="font-bold text-lg">{book.title}</h3>
                      <p className="text-gray-600">by {book.author}</p>
                      <div className="flex items-center space-x-4 mt-2 text-sm text-gray-500">
                        <span>📚 {book.genre}</span>
                        <span>📅 {book.publicationYear}</span>
                        <span>🔖 {book.isbn}</span>
                      </div>
                      {book.description && (
                        <p className="mt-2 text-gray-700">{book.description}</p>
                      )}
                      <div className="flex items-center space-x-2 mt-2 text-xs text-gray-400">
                        <Calendar className="h-3 w-3" />
                        <span>Created: {new Date(book.createdAt).toLocaleDateString()}</span>
                      </div>
                    </div>
                    <div className="flex space-x-2 ml-4">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleEdit(book)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        size="sm"
                        variant="destructive"
                        onClick={() => handleDelete(book)}
                        disabled={deleteBookMutation.isPending}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default Index;
