/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dp_project_1;


public class Librarian implements Observer {

    private String name;

    public Librarian(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println("Librarian " + name + " received notification: " + message);
    }

    public void addNewBook(Bookstore bookstore, Book newBook) {
        bookstore.addBook();
        System.out.println("Librarian " + name + " added a new book: " + newBook.getTitle());
    }

    public void removeBook(Bookstore bookstore, Book bookToRemove) {
        bookstore.removeBook();
        System.out.println("Librarian " + name + " removed the book: " + bookToRemove.getTitle());
    }
}
