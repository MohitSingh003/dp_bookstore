/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dp_project_1;

/**
 *
 * @author HP
 */
class AvailableState implements BookState {
    @Override
    public void issue(Book book) {
        System.out.println("");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        System.out.println("");
        System.out.println("Book issued: " + book.getTitle());
        book.setState(new IssuedState());
        if(book.getState() instanceof AvailableState){
            System.out.println("instance of Availalbe state");
        }
    }

    @Override
    public void returnBook(Book book) {
        System.out.println("Book is already available: " + book.getTitle());
    }
}
