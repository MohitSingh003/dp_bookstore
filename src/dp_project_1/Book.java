/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dp_project_1;

/**
 *
 * @author HP
 */
abstract class Book {
    private int bookid;
    private String title;
    private String author;
    private double price;
    private BookState state;

    public Book(int bookID, String title, String author, double price) {
        this.bookid = bookID;
        this.title = title;
        this.author = author;
        this.price = price;
        this.state = new AvailableState();
    }

    public int getBookID(){
        return bookid;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public void setState(BookState state) {
        this.state = state;
    }

    public void issue() {
        state.issue(this);
    }

    public void returnBook() {
        state.returnBook(this);
    }

    public BookState getState(){
        return this.state;
    }

   
}

