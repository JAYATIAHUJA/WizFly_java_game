import javax.swing.* ;

public class App {
    // This is a simple Java program that prints "Hello, World!" to the console.
    public static void main(String[] args) throws Exception {
        int boardWidth = 840 ;
        int boardHeight = 640 ;

        JFrame frame = new JFrame("WizFly");
        //frame.setVisible(true) ;
        frame.setSize(boardWidth, boardHeight) ; // Set the size of the windo
        frame.setLocationRelativeTo(null) ; // Center the window on the screen
        frame.setResizable(false) ; // Disable resizing the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ; // Exit the program when the window is closed
        
        WizFly WizFly = new WizFly() ; // Create an instance of the FlappyBird class
        frame.add(WizFly) ; // Add the FlappyBird panel to the frame            
        frame.pack() ; // Pack the frame to fit the preferred size of the panel
        WizFly.requestFocus() ; // Request focus for the panel to receive key events
        frame.setVisible(true) ; // Make the frame visible
    
    
    
    
    
    }

}