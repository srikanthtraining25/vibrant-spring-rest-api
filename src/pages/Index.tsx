
import React, { useState, useEffect } from 'react';
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { useToast } from "@/hooks/use-toast";
import { BookOpen, Plus, Search, Edit, Trash2, Calendar, User, Hash, Tag } from "lucide-react";

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

const Index = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const [filteredBooks, setFilteredBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  const [totalBooks, setTotalBooks] = useState(0);
  const { toast } = useToast();

  // Mock API base URL - in a real app, this would be your Spring Boot server
  const API_BASE = 'http://localhost:8080/api';

  // Initial form state
  const initialFormState = {
    title: '',
    author: '',
    isbn: '',
    publicationYear: new Date().getFullYear(),
    genre: '',
    description: ''
  };

  const [formData, setFormData] = useState(initialFormState);

  // Mock data for demonstration (replace with actual API calls)
  const mockBooks: Book[] = [
    {
      id: 1,
      title: "The Great Gatsby",
      author: "F. Scott Fitzgerald",
      isbn: "978-0-7432-7356-5",
      publicationYear: 1925,
      genre: "Fiction",
      description: "A classic American novel about the Jazz Age",
      createdAt: "2023-12-07T10:00:00",
      updatedAt: "2023-12-07T10:00:00"
    },
    {
      id: 2,
      title: "To Kill a Mockingbird",
      author: "Harper Lee",
      isbn: "978-0-06-112008-4",
      publicationYear: 1960,
      genre: "Fiction",
      description: "A gripping tale of racial injustice and childhood innocence",
      createdAt: "2023-12-07T10:00:00",
      updatedAt: "2023-12-07T10:00:00"
    },
    {
      id: 3,
      title: "1984",
      author: "George Orwell",
      isbn: "978-0-452-28423-4",
      publicationYear: 1949,
      genre: "Dystopian Fiction",
      description: "A dystopian social science fiction novel",
      createdAt: "2023-12-07T10:00:00",
      updatedAt: "2023-12-07T10:00:00"
    }
  ];

  useEffect(() => {
    fetchBooks();
    fetchStats();
  }, []);

  useEffect(() => {
    filterBooks();
  }, [books, searchTerm]);

  const fetchBooks = async () => {
    setLoading(true);
    try {
      // Simulate API call with mock data
      setTimeout(() => {
        setBooks(mockBooks);
        setLoading(false);
        toast({
          title: "Books loaded",
          description: "Successfully loaded all books",
        });
      }, 500);
    } catch (error) {
      console.error('Error fetching books:', error);
      setLoading(false);
      toast({
        title: "Error",
        description: "Failed to load books",
        variant: "destructive",
      });
    }
  };

  const fetchStats = async () => {
    // Simulate stats API call
    setTotalBooks(mockBooks.length);
  };

  const filterBooks = () => {
    if (!searchTerm.trim()) {
      setFilteredBooks(books);
      return;
    }

    const filtered = books.filter(book =>
      book.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.genre.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredBooks(filtered);
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (editingBook) {
        // Update existing book
        const updatedBook = { ...editingBook, ...formData };
        setBooks(books.map(book => book.id === editingBook.id ? updatedBook : book));
        toast({
          title: "Book updated",
          description: `"${formData.title}" has been updated successfully`,
        });
      } else {
        // Create new book
        const newBook: Book = {
          id: Math.max(...books.map(b => b.id), 0) + 1,
          ...formData,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        setBooks([...books, newBook]);
        setTotalBooks(totalBooks + 1);
        toast({
          title: "Book created",
          description: `"${formData.title}" has been added to the library`,
        });
      }

      // Reset form
      setFormData(initialFormState);
      setShowForm(false);
      setEditingBook(null);
    } catch (error) {
      console.error('Error saving book:', error);
      toast({
        title: "Error",
        description: "Failed to save book",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (book: Book) => {
    setEditingBook(book);
    setFormData({
      title: book.title,
      author: book.author,
      isbn: book.isbn,
      publicationYear: book.publicationYear,
      genre: book.genre,
      description: book.description
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this book?')) {
      setBooks(books.filter(book => book.id !== id));
      setTotalBooks(totalBooks - 1);
      toast({
        title: "Book deleted",
        description: "The book has been removed from the library",
      });
    }
  };

  const resetForm = () => {
    setFormData(initialFormState);
    setShowForm(false);
    setEditingBook(null);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-600 to-green-600 text-white shadow-lg">
        <div className="container mx-auto px-6 py-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <BookOpen className="h-8 w-8" />
              <div>
                <h1 className="text-3xl font-bold">Book Library API</h1>
                <p className="text-blue-100">Spring Boot REST API Demo</p>
              </div>
            </div>
            <div className="text-right">
              <div className="text-2xl font-bold">{totalBooks}</div>
              <div className="text-sm text-blue-100">Total Books</div>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-6 py-8">
        {/* Controls */}
        <div className="mb-8 flex flex-col sm:flex-row gap-4 items-center justify-between">
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
            <Input
              placeholder="Search books by title, author, or genre..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>
          <Button
            onClick={() => setShowForm(!showForm)}
            className="bg-gradient-to-r from-blue-600 to-green-600 hover:from-blue-700 hover:to-green-700 transition-all duration-200"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add New Book
          </Button>
        </div>

        {/* Form */}
        {showForm && (
          <Card className="mb-8 border-2 border-blue-200 shadow-lg">
            <CardHeader className="bg-gradient-to-r from-blue-50 to-green-50">
              <CardTitle className="flex items-center">
                <BookOpen className="h-5 w-5 mr-2 text-blue-600" />
                {editingBook ? 'Edit Book' : 'Add New Book'}
              </CardTitle>
              <CardDescription>
                {editingBook ? 'Update the book information' : 'Enter the details for the new book'}
              </CardDescription>
            </CardHeader>
            <CardContent className="p-6">
              <form onSubmit={handleFormSubmit} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="title">Title *</Label>
                    <Input
                      id="title"
                      value={formData.title}
                      onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                      required
                      className="mt-1"
                    />
                  </div>
                  <div>
                    <Label htmlFor="author">Author *</Label>
                    <Input
                      id="author"
                      value={formData.author}
                      onChange={(e) => setFormData({ ...formData, author: e.target.value })}
                      required
                      className="mt-1"
                    />
                  </div>
                  <div>
                    <Label htmlFor="isbn">ISBN *</Label>
                    <Input
                      id="isbn"
                      value={formData.isbn}
                      onChange={(e) => setFormData({ ...formData, isbn: e.target.value })}
                      required
                      className="mt-1"
                    />
                  </div>
                  <div>
                    <Label htmlFor="publicationYear">Publication Year *</Label>
                    <Input
                      id="publicationYear"
                      type="number"
                      value={formData.publicationYear}
                      onChange={(e) => setFormData({ ...formData, publicationYear: parseInt(e.target.value) })}
                      required
                      className="mt-1"
                    />
                  </div>
                  <div>
                    <Label htmlFor="genre">Genre</Label>
                    <Input
                      id="genre"
                      value={formData.genre}
                      onChange={(e) => setFormData({ ...formData, genre: e.target.value })}
                      className="mt-1"
                    />
                  </div>
                </div>
                <div>
                  <Label htmlFor="description">Description</Label>
                  <Textarea
                    id="description"
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    rows={3}
                    className="mt-1"
                  />
                </div>
                <div className="flex space-x-3 pt-4">
                  <Button
                    type="submit"
                    disabled={loading}
                    className="bg-gradient-to-r from-blue-600 to-green-600 hover:from-blue-700 hover:to-green-700"
                  >
                    {loading ? 'Saving...' : (editingBook ? 'Update Book' : 'Create Book')}
                  </Button>
                  <Button type="button" variant="outline" onClick={resetForm}>
                    Cancel
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        )}

        {/* Books Grid */}
        {loading && !showForm ? (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredBooks.length === 0 ? (
              <div className="col-span-full text-center py-12">
                <BookOpen className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                <h3 className="text-xl font-semibold text-gray-500 mb-2">No books found</h3>
                <p className="text-gray-400">
                  {searchTerm ? 'Try adjusting your search terms' : 'Add your first book to get started'}
                </p>
              </div>
            ) : (
              filteredBooks.map((book) => (
                <Card key={book.id} className="hover:shadow-xl transition-all duration-300 border-2 hover:border-blue-200">
                  <CardHeader className="pb-3">
                    <CardTitle className="line-clamp-2 text-lg">{book.title}</CardTitle>
                    <CardDescription className="flex items-center text-blue-600">
                      <User className="h-4 w-4 mr-1" />
                      {book.author}
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    <div className="flex items-center justify-between text-sm text-gray-600">
                      <div className="flex items-center">
                        <Calendar className="h-4 w-4 mr-1" />
                        {book.publicationYear}
                      </div>
                      <div className="flex items-center">
                        <Hash className="h-4 w-4 mr-1" />
                        {book.isbn}
                      </div>
                    </div>
                    {book.genre && (
                      <div className="flex items-center">
                        <Tag className="h-4 w-4 mr-2 text-green-600" />
                        <Badge variant="secondary" className="bg-green-100 text-green-800">
                          {book.genre}
                        </Badge>
                      </div>
                    )}
                    {book.description && (
                      <p className="text-sm text-gray-600 line-clamp-3">{book.description}</p>
                    )}
                  </CardContent>
                  <Separator />
                  <CardFooter className="pt-4">
                    <div className="flex space-x-2 w-full">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleEdit(book)}
                        className="flex-1 hover:bg-blue-50"
                      >
                        <Edit className="h-4 w-4 mr-1" />
                        Edit
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleDelete(book.id)}
                        className="flex-1 hover:bg-red-50 hover:text-red-700 hover:border-red-200"
                      >
                        <Trash2 className="h-4 w-4 mr-1" />
                        Delete
                      </Button>
                    </div>
                  </CardFooter>
                </Card>
              ))
            )}
          </div>
        )}

        {/* API Information */}
        <Card className="mt-12 bg-gradient-to-r from-blue-50 to-green-50 border-2 border-blue-200">
          <CardHeader>
            <CardTitle className="text-blue-800">Spring Boot REST API Endpoints</CardTitle>
            <CardDescription>Available API operations for this Book Library system</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
              <div className="space-y-2">
                <div className="font-semibold text-green-700">GET /api/books</div>
                <div className="text-gray-600">Retrieve all books</div>
                
                <div className="font-semibold text-green-700">GET /api/books/{id}</div>
                <div className="text-gray-600">Get a specific book</div>
                
                <div className="font-semibold text-blue-700">POST /api/books</div>
                <div className="text-gray-600">Create a new book</div>
              </div>
              <div className="space-y-2">
                <div className="font-semibold text-yellow-700">PUT /api/books/{id}</div>
                <div className="text-gray-600">Update an existing book</div>
                
                <div className="font-semibold text-red-700">DELETE /api/books/{id}</div>
                <div className="text-gray-600">Delete a book</div>
                
                <div className="font-semibold text-purple-700">GET /api/books/stats</div>
                <div className="text-gray-600">Get library statistics</div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Index;
