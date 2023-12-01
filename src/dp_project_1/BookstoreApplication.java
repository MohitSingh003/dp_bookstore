package dp_project_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Iterator;

public class BookstoreApplication {

    static java.sql.Statement stmt;
    static Connection con;

    public static void main(String[] args) throws SQLException {

        // Initializing JDBC
        try {
            con = DriverManager.getConnection("jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666213", "sql12666213","jVnDyJqk4b");
        } catch (SQLInvalidAuthorizationSpecException e) {
            System.out.println(e);
            System.out.println("\nInvalid Credentials\n");

        } catch (SQLException e) {
            System.out.println("\nSQL Exception encountered");
        }
        if (con == null) {
            System.out.println("Null");
        }

        stmt = con.createStatement();

        System.out.println("JDBC Connection Created!");

        Scanner scanner = new Scanner(System.in);



        // Singleton pattern
        Bookstore bookstore = Bookstore.getInstance(scanner, stmt);

  

        // Observer pattern
        Observer customer1 = new Customer("Mohit");
        Observer customer2 = new Customer("Prerna");
        Observer librarian = new Librarian("Lakshay");

        bookstore.addObserver(customer1);
        bookstore.addObserver(customer2);
        bookstore.addObserver(librarian);

        

        int role = 0;
        do {
            System.out.println("\n--- User Role ---");
            System.out.println("1. Customer");
            System.out.println("2. Librarian");
            System.out.println("3. Exit");
            System.out.print("Enter your role: ");
            try {
                role = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid role (1, 2, or 3).");
                continue;
            }

            switch (role) {
                case 1:
                    customerMenu(bookstore, scanner);
                    break;
                case 2:
                    librarianMenu(bookstore, scanner);
                    break;
                case 3:
                    System.out.println("Exiting the Bookstore. Thank you!");
                    break;
                default:
                    System.out.println("Invalid role. Please enter a valid option.");
            }
        } while (role != 3);

        scanner.close();
        stmt.close();
        con.close();
    }

    private static void customerMenu(Bookstore bookstore, Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Display available books");
            System.out.println("2. Issue a book");
            System.out.println("3. Return a book");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    bookstore.displayAvailableBooks();
                    break;
                case 2:
                    bookstore.issueBook();
                    break;
                case 3:
                    bookstore.returnBook();
                    break;
                case 4:
                    System.out.println("Exiting the Customer Menu. Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        } while (choice != 4);
    }

    private static void librarianMenu(Bookstore bookstore, Scanner scanner) {
        System.out.println("\n--- Librarian Menu ---");
        System.out.println("1. Display available books");
        System.out.println("2. Add a new book");
        System.out.println("3. Remove a book");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");

        int choice = Integer.parseInt(scanner.nextLine());

        // Implement Librarian-specific functionality here
        switch (choice) {
            case 1:
                // Display available books
                bookstore.displayAvailableBooks();
                break;
            case 2:
                bookstore.addBook();

                break;
            case 3:
                // Remove a book
                bookstore.removeBook();

                break;
            case 4:
                System.out.println("Exiting the Librarian Menu. Thank you!");
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
    }

    private static void displayAvailableBooks(ArrayList<Book> Books) {
        System.out.println("\n--- Available Books ---");

        for (Book book : Books) {

            String bookType = (book instanceof FictionBook) ? "Fiction" : "Non-Fiction";
            System.out.println(book.getBookID() + ". " + book.getTitle() + " by " + book.getAuthor() + " - $"
                    + book.getPrice() + " (" + bookType + ")");

        }

    }

    private static void addBook(Scanner scanner, ArrayList<Book> Books, java.sql.Statement stmt) {
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

        Book newBook = bookFactory.createBook(bookID, title, author, price);
        System.out.println("New book added: " + newBook.getTitle());
        // Add the new book to the list of books
        Books.add(newBook);
        // books = Arrays.copyOf(books, books.length + 1);
        // books[books.length - 1] = newBook;
        displayAvailableBooks(Books);
    }

    private static void removeBook(Scanner scanner, ArrayList<Book> Books) {
        System.out.println("\n--- Remove a Book ---");
        displayAvailableBooks(Books);
        System.out.print("Enter the ID of book you want to remove : ");
        int id = scanner.nextInt();

        // for(Book b : Books){
        // if(b.getBookID()==id){
        // Books.remove(b);
        // }
        // }

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
        displayAvailableBooks(Books);

    }

    

    

    private static void displayNonIssuedBooks(ArrayList<Book> Books) {
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

    private static void displayIssuedBooks(ArrayList<Book> Books) {

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
}