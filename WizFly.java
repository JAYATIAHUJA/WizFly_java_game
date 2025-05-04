import java.awt.* ;
import java.awt.event.* ;
import java.util.ArrayList ; //store the crystals in an array list
import javax.swing.* ;
import java.util.Random ; //for random number generation of crystals

public class WizFly extends JPanel implements ActionListener , KeyListener {
    int boardWidth = 840 ;
    int boardHeight = 640 ; 

    //IMAGES
    Image backgroundImg ;
    Image wizardImg ;
    Image topCrystalImg ;
    Image bottomCrystalImg ;

    //wizard
    int wizardX = boardWidth/12 ; // X coordinate of the wizard
    int wizardY = boardHeight/2 ; // Y coordinate of the wizard
    int wizardWidth = 79 ; // Width of the wizard           
    int wizardHeight = 53 ; // Height of the wizard

    class wizard {
        int x = wizardX ; // X coordinate of the wizard
        int y = wizardY ; // Y coordinate of the wizard
        int width = wizardWidth ; // Width of the wizard
        int height = wizardHeight ; // Height of the wizard
        Image img ;

        wizard(Image img) {
            this.img = img ;
        }
    }

    //crystal
    int CrystalX = boardWidth ; // X coordinate of the crystals
    int CrystalY = 0 ; // Y coordinate of the crystals
    int CrystalWidth = 120 ; // Width of the crystals
    int CrystalHeight = 547 ; // Height of the crystals

    class Crystal {
        int x = CrystalX ; // X coordinate of the crystals
        int y = CrystalY ; // Y coordinate of the crystals
        int width = CrystalWidth ; // Width of the crystals
        int height = CrystalHeight ; // Height of the crystals
        Image img ;
        boolean passed = false ; // Flag to check if the crystals has been passed by the wizard

        Crystal(Image img) {
            this.img = img ;
        }

    }


    //game logic
    wizard wizard ;
    int velocityX = -4 ; // X velocity of the crystals
    int velocityY = 0 ; // Y velocity of the wizard
    int gravity = 1 ; // Gravity effect on the wizard

    ArrayList<Crystal> Crystals; // ArrayList to store the crystals
    Random random = new Random() ; // Random number generator for crystals placement

    Timer gameLoop;
    Timer placeCrystalsTimer;

    boolean gameOver = false ; // Flag to check if the game is over

    double score = 0 ; // Score of the game


    WizFly() {
        setPreferredSize(new Dimension(boardWidth, boardHeight)) ; // Set the size of the panel
        setBackground(Color.blue) ; // Set the background color of the panel
        
        setFocusable(true) ; // Make the panel focusable to receive key events
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./wizflybg.png")).getImage() ;
        wizardImg = new ImageIcon(getClass().getResource("./wizard.png")).getImage() ;
        topCrystalImg = new ImageIcon(getClass().getResource("./topcrystal.png")).getImage() ;
        bottomCrystalImg = new ImageIcon(getClass().getResource("./bottomcrystal1.png")).getImage() ;
 
        //wizard
        wizard= new wizard(wizardImg) ; // Create a new wizard object with the wizard image
        
        Crystals = new ArrayList<Crystal>() ; // Initialize the crystals ArrayList

        //place crystals timer
        placeCrystalsTimer = new Timer(2500, new ActionListener() { // Create a timer that fires every 2 seconds
           
                @Override
                public void actionPerformed(ActionEvent e) {
                    placeCrystals() ; // Call the place crystals method to create a new crystals
                }
            }) ;
       
        placeCrystalsTimer.start() ; // Start the place crystals timer

        //game timer
        gameLoop = new Timer(1000/60, this) ;// Create a timer that fires every 1000/60 milliseconds (60 FPS)
        gameLoop.start() ; // Start the game loop timer
    }  
    
    public void placeCrystals() {
        int randomCrystalY = (int) (CrystalY - CrystalHeight/4 - Math.random()*(CrystalHeight/2)) ; // Generate a random Y coordinate for the crystal
        
        int openingSpace = boardHeight/4 ; // Space between the top and bottom crystals
        // Create a new crystals object with the top and bottom crystals images
        Crystal topCrystal = new Crystal(topCrystalImg) ; // Create a new crystals object with the top crystals image
        topCrystal.y = randomCrystalY ; // Set the Y coordinate of the top crystals
        Crystals.add(topCrystal) ; // Add the crystals to the crystals ArrayList
    
        Crystal bottomCrystal = new Crystal(bottomCrystalImg) ; // Create a new crystals object with the bottom crystals image
        bottomCrystal.y= topCrystal.y + CrystalHeight + openingSpace ; // Set the Y coordinate of the bottom crystals
        Crystals.add(bottomCrystal) ; // Add the crystals to the crystals ArrayList
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g) ; // Call the superclass's paintComponent method to clear the panel
        draw(g) ; // Call the draw method to draw the game elements
    }

    public void draw(Graphics g) {
        // Draw the background image
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null) ;
        // Draw the wizard image
        g.drawImage(wizard.img, wizard.x, wizard.y, wizard.width, wizard.height, null) ;
       
        //crystals
        for (int i =0 ; i < Crystals.size() ; i++) { // Loop through the crystals ArrayList
            Crystal Crystal = Crystals.get(i) ; // Get the current crystals
            g.drawImage(Crystal.img, Crystal.x, Crystal.y, Crystal.width, Crystal.height, null) ; // Draw the crystals image
        }

        //score
        g.setColor(Color.white) ; // Set the color to white
        
        g.setFont(new Font("Arial", Font.BOLD, 32)) ; // Set the font for the score
        if (gameOver) { // Check if the game is over
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
         }else{  // Draw the game over message
            g.drawString(String.valueOf((int) score), 10, 35); // Draw the score on the screen
           }        
        }
    

    public void move(){
        //wizard 
        velocityY += gravity ; // Apply gravity to the wizard's velocity
        wizard.y += velocityY ;
        wizard.y = Math.max(wizard.y , 0) ;// Update the Y coordinate of the wizard based on its velocity
    
        //crystals
        for (int i = 0 ; i < Crystals.size() ; i++) { // Loop through the crystals ArrayList
            Crystal Crystal = Crystals.get(i) ; // Get the crystals crystal
            
            Crystal.x += velocityX ; // Move the crystals to the left based on its velocity
            if (collision(wizard, Crystal) ) {
                gameOver = true ; // Set the game over flag to true if there is a collision
            }
        }

        if (wizard.y > boardHeight) { // Check if the wizard has fallen below the bottom of the screen
            gameOver = true ; // Set the game over flag to true
        }   

        for (Crystal Crystal : Crystals) { // Loop through the crystals ArrayList
            if (!Crystal.passed && wizard.x > Crystal.x + Crystal.width) { // Check if the wizard has passed the crystal
            score += 0.5; // Increment the score
            Crystal.passed = true; // Set the passed flag to true for the crystals
            }
        }
    }

    public boolean collision(wizard a , Crystal b){
        return (a.x < b.x + b.width &&  // a's top left corner doesnt reach b's top left corner
        a.x + a.width > b.x &&  // a's top right corner passes b's top left corner
        a.y < b.y + b.height &&  //a's top left corner doesnt reach b's bottom left corner
         a.y + a.height > b.y) ; // a's bottom left corner passes b's top left corner
    }
    
  @Override
    public void actionPerformed(ActionEvent e) {
      move(); // Call the move method to update the game state
      repaint();

    if (gameOver) { // Check if the game is over
        gameLoop.stop() ; // Stop the game loop timer
        placeCrystalsTimer.stop() ; // Stop the place crystals timer
        
    }

}

  @Override
  public void keyTyped(KeyEvent e) { }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Check if the space key is pressed
        velocityY = -9 ; // Set the wizard's velocity to -6 when the space key is pressed
        if (gameOver){
            //restart the game by resetiing the conditions
            wizard.y=wizardY ; // Reset the wizard's Y coordinate to the initial position
            velocityY=0;
            Crystals.clear() ; // Clear the crystals ArrayList
            score = 0 ; // Reset the score to 0
            gameOver = false ; // Set the game over flag to false   
            gameLoop.start() ; // Start the game loop timer
            placeCrystalsTimer.start() ; // Start the place crystals timer
       
        }
    }
}
   

  @Override
  public void keyReleased(KeyEvent e) { }

  

}

