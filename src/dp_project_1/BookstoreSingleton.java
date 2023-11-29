/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dp_project_1;

/**
 *
 * @author HP
 */
public class BookstoreSingleton {
    private static BookstoreSingleton instance;

    private BookstoreSingleton() {
        // private constructor to prevent instantiation
    }

    public static BookstoreSingleton getInstance() {
        if (instance == null) {
            instance = new BookstoreSingleton();
        }
        return instance;
    }

    // Other methods and attributes can be added as needed
}

