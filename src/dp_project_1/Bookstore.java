/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dp_project_1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author HP
 */
public class Bookstore implements Observable {

    private static Bookstore bookStore;
    private Scanner scanner;
    private java.sql.Statement stmt;
    private ArrayList<Book> Books;
    private BookFactory fictionBookFactory;
    private BookFactory nonFictionBookFactory;

    public Bookstore(Scanner scanner, java.sql.Statement stmt) throws SQLException {

        // Database initialization
        this.scanner = scanner;
        this.stmt = stmt;

        // Factory method implementation
        this.fictionBookFactory = new FictionBookFactory();
        this.nonFictionBookFactory = new NonFictionBookFactory();

        this.Books = new ArrayList<>();

        // Get books from DB and add to local ArrayList
        String getBooks = "SELECT bookid, title, author, price, booktype from books";
        ResultSet bookList;
        try {
            bookList = stmt.executeQuery(getBooks);
            while (bookList.next()) {
                int bookid = bookList.getInt("bookid");
                String title = bookList.getString("title");
                String author = bookList.getString("author");
                double price = bookList.getDouble("price");

                String type = bookList.getString("booktype");

                if (type.equals("f")) {
                    Books.add(fictionBookFactory.createBook(bookid, title, author, price));
                } else {
                    Books.add(nonFictionBookFactory.createBook(bookid, title, author, price));
                }
            }
        } catch (SQLSyntaxErrorException e) {
            System.out.println("Invalid SQL Syntax");
        } catch (SQLTimeoutException e) {
            System.out.println("SQL Login timeout");
        } catch (SQLException e) {
            System.out.println("Unknown SQL Exception encountered");
        }

    }

    public static Bookstore getInstance(Scanner scanner, java.sql.Statement stmt) throws SQLException {
        if (bookStore == null) {
            bookStore = new Bookstore(scanner, stmt);
        }
        return bookStore;

    }

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    void addBook() {
        System.out.println("\n--- Add a New Book ---");
        System.out.print("Enter the Book ID: ");
        int bookID = scanner.nextInt();

        // Consume the newline character
        scanner.nextLine();

        System.out.print("Enter the title of the new book: ");
        String title = scanner.nextLine();
        System.out.print("Enter the author of the new book: ");
        String author = scanner.nextLine();
        System.out.print("Enter the price of the new book: ");
        double price;
        char type;

        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for price. Please enter a valid number.");
            return;
        }

        System.out.print("Enter the type of the new book (1 for Fiction, 2 for Non-Fiction): ");
        int bookTypeChoice;

        try {
            bookTypeChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for book type. Please enter 1 for Fiction or 2 for Non-Fiction.");
            return;
        }

        BookFactory bookFactory;
        if (bookTypeChoice == 1) {
            bookFactory = new FictionBookFactory();
            type = 'f';
        } else if (bookTypeChoice == 2) {
            bookFactory = new NonFictionBookFactory();
            type = 'n';
        } else {
            System.out.println("Invalid book type. Please enter 1 for Fiction or 2 for Non-Fiction.");
            return;
        }

        String addBook = "INSERT INTO books value (" + bookID + ", '" + title + "', '" + author + "', " + price + ", '"
                + type + "')";

        try {
            stmt.execute(addBook);
        } catch (SQLException e) {
            System.out.println("SQL Exception encountered");
        } catch (Exception e) {
            System.out.println("idk");
        }
        System.out.println("Book Added to database");

        Book nBook = bookFactory.createBook(bookID, title, author, price);
        System.out.println("New book added: " + nBook.getTitle());
        // Add the new book to the list of books
        Books.add(nBook);
        // books = Arrays.copyOf(books, books.length + 1);
        // books[books.length - 1] = newBook;
        displayAvailableBooks();
    }

    Book[] getBooks() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    boolean removeBook(String title) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void displayAvailableBooks() {
        System.out.println("\n--- Available Books ---");

        for (Book book : Books) {

            String bookType = (book instanceof FictionBook) ? "Fiction" : "Non-Fiction";
            System.out.println(book.getBookID() + ". " + book.getTitle() + " by " + book.getAuthor() + " - $"
                    + book.getPrice() + " (" + bookType + ")");

        }
    }

    void removeBook() {
        System.out.println("\n--- Remove a Book ---");
        displayAvailableBooks();
        System.out.print("Enter the ID of book you want to remove : ");
        int id = scanner.nextInt();

        Iterator<Book> it = Books.iterator();

        while (it.hasNext()) {
            int i = it.next().getBookID();
            if (i == id) {
                it.remove();
            }
        }

        String removeBook_q = "DELETE FROM books WHERE bookid = " + id + ";";

        try {
            stmt.execute(removeBook_q);
        } catch (SQLException e) {
            System.out.println("Could not remove book from Database, please check if the Book exists in the Store");
        } catch (Exception e) {
            System.out.println("Some error encountered");
        }

        // Books.remove(index-1);
        System.out.println("Book removed");
        System.out.println("--------------------------------------------------------------------------------------");
        displayAvailableBooks();
    }
}
