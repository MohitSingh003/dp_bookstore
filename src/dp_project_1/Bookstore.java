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

    private Bookstore(Scanner scanner, java.sql.Statement stmt) throws SQLException {

        // Database initialization
        this.scanner = scanner;
        this.stmt = stmt;

        // Factory method implementation
        this.fictionBookFactory = new FictionBookFactory();
        this.nonFictionBookFactory = new NonFictionBookFactory();

        this.Books = new ArrayList<>();
        
        updateResult();

        // Get books from DB and add to local ArrayList
        

    }

    public static Bookstore getInstance(Scanner scanner, java.sql.Statement stmt) throws SQLException {
        if (bookStore == null) {
            bookStore = new Bookstore(scanner, stmt);
        }
        return bookStore;

    }

    private ArrayList<Observer> observers = new ArrayList<>();

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
        int isIssued = 0;
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

        String addBook = "INSERT INTO books value (" + bookID + ",'" + title + "','" + author + "'," + price + ",'"+ type + "'," + isIssued + ")";

        try {
            stmt.execute(addBook);
        } catch (SQLSyntaxErrorException e) {
            System.out.println("SQL Syntax exception");
        }catch(SQLTimeoutException e){
            System.out.println("SQL Timeout");
        }catch (SQLException e) {
            System.out.println("SQL Exception");
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

    // void issueBook() {
    //     System.out.println("\n--- Issue a Book ---");
    //     // displayAvailableBooks(Books);
    //     displayNonIssuedBooks();

    //     System.out.print("Enter the title of the book you want to issue: ");
    //     scanner.nextLine(); // Consume the newline character left by nextInt()
    //     String input = scanner.nextLine();

        
    //     for (Book book : Books) {
    //         if (book.getTitle().equalsIgnoreCase(input)) {
    //             book.issue();
    //             this.notifyObservers("Book issued: " + book.getTitle());
    //             return;
    //         }
    //     }

    //     System.out.println("Book with title '" + input + "' not found.");
    // }

    void issueBook() throws SQLException{
        System.out.println("\n--- Issue a Book ---");
        displayNonIssuedBooks();   

        System.out.print("Enter the title of the book you want to issue: ");
        scanner.nextLine(); // Consume the newline character left by nextInt()
        String input = scanner.nextLine();
        String updateStatus;
        for (Book book : Books) {
            
            if (book.getTitle().equalsIgnoreCase(input)) {
                book.issue();
                updateStatus = "UPDATE books SET isIssued = 1 WHERE title = '" + input + "';";
                stmt.execute(updateStatus);
                notifyObservers("Book issued: " + book.getTitle());
                return;
            }
        }



        System.out.println("Book with title '" + input + "' not found.");
    }

    void returnBook() throws SQLException{
        displayIssuedBooks();
        System.out.println("");
        System.out.println("\n--- Return a Book ---");
        System.out.print("Enter the title of the book you want to return: ");
        scanner.nextLine();
        String title = scanner.nextLine();
        String updateStatus;
        for (Book book : Books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                book.returnBook();
                updateStatus = "UPDATE books SET isIssued = 0 WHERE title = '" + title + "';";
                stmt.execute(updateStatus);
                notifyObservers("Book returned: " + book.getTitle());
                return;
            }
        }

        System.out.println("Book with title '" + title + "' not found.");
    }

    void displayAvailableBooks() {
        updateResult();
        System.out.println("\n--- Available Books ---");

        for (Book book : Books) {

            String bookType = (book instanceof FictionBook) ? "Fiction" : "Non-Fiction";
            System.out.println(book.getBookID() + ". " + book.getTitle() + " by " + book.getAuthor() + " - $"
                    + book.getPrice() + " (" + bookType + ")");

        }
    }

    void removeBook() {
        updateResult();
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

    void displayNonIssuedBooks(){
        updateResult();
        System.out.println("");
        System.out.println("---- Available Books ----");

        for (Book book : Books) {
            if (book.getState() instanceof AvailableState) {
                String bookType = (book instanceof FictionBook) ? "Fiction" : "Non-Fiction";
                System.out.println(book.getBookID() + ". " + book.getTitle() + " by " + book.getAuthor() + " - $"
                        + book.getPrice() + " (" + bookType + ")");
            }
        }
    }

    void displayIssuedBooks(){
        System.out.println("");
        System.out.println("---- Issued Books ----");

        for (Book book : Books) {
            if (book.getState() instanceof IssuedState) {
                String bookType = (book instanceof FictionBook) ? "Fiction" : "Non-Fiction";
                System.out.println(book.getBookID() + ". " + book.getTitle() + " by " + book.getAuthor() + " - $"
                        + book.getPrice() + " (" + bookType + ")");
            }
        }
    }
    
    void updateResult(){
        Books.clear();

        String getBooks = "SELECT bookid, title, author, price, booktype, isIssued from books";
        ResultSet bookList;
        try {
            bookList = stmt.executeQuery(getBooks);
            while (bookList.next()) {
                int bookid = bookList.getInt("bookid");
                String title = bookList.getString("title");
                String author = bookList.getString("author");
                double price = bookList.getDouble("price");

                String type = bookList.getString("booktype");

                boolean isIssued = (bookList.getInt("isIssued")==0) ? (false):(true);

                Book newBook;
                if (type.equals("f")) {
                    newBook = fictionBookFactory.createBook(bookid, title, author, price);
                    Books.add(newBook);
                } else {
                    newBook = nonFictionBookFactory.createBook(bookid, title, author, price);
                    Books.add(newBook);
                }

                if(isIssued){
                    newBook.setState(new IssuedState());
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
}
