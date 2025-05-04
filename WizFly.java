import java.awt.* ;
import java.awt.event.* ;
import java.util.ArrayList ; //store the pipes in an array list
import javax.swing.* ;
import java.util.Random ; //for random number generation of pipes

public class WizFly extends JPanel implements ActionListener , KeyListener {
    int boardWidth = 840 ;
    int boardHeight = 640 ; 

    //IMAGES
    Image backgroundImg ;
    Image wizardImg ;
    Image topCrystalImg ;
    Image bottomCrystalImg ;

    //BIRD
    int wizardX = boardWidth/12 ; // X coordinate of the bird
    int wizardY = boardHeight/2 ; // Y coordinate of the bird
    int wizardWidth = 79 ; // Width of the bird           
    int wizardHeight = 53 ; // Height of the bird

    class wizard {
        int x = wizardX ; // X coordinate of the bird
        int y = wizardY ; // Y coordinate of the bird
        int width = wizardWidth ; // Width of the bird
        int height = wizardHeight ; // Height of the bird
        Image img ;

        wizard(Image img) {
            this.img = img ;
        }
    }

    //pipes
    int CrystalX = boardWidth ; // X coordinate of the pipe
    int CrystalY = 0 ; // Y coordinate of the pipe
    int CrystalWidth = 120 ; // Width of the pipe 
    int CrystalHeight = 547 ; // Height of the pipe  

    class Crystal {
        int x = CrystalX ; // X coordinate of the pipe
        int y = CrystalY ; // Y coordinate of the pipe
        int width = CrystalWidth ; // Width of the pipe
        int height = CrystalHeight ; // Height of the pipe
        Image img ;
        boolean passed = false ; // Flag to check if the pipe has been passed by the bird

        Crystal(Image img) {
            this.img = img ;
        }

    }


    //game logic
    wizard wizard ;
    int velocityX = -4 ; // X velocity of the pipe
    int velocityY = 0 ; // Y velocity of the bird
    int gravity = 1 ; // Gravity effect on the bird

    ArrayList<Crystal> Crystals; // ArrayList to store the pipes
    Random random = new Random() ; // Random number generator for pipe placement

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
 
        //bird
        wizard= new wizard(wizardImg) ; // Create a new Bird object with the bird image
        
        Crystals = new ArrayList<Crystal>() ; // Initialize the pipes ArrayList

        //place pipes timer
        placeCrystalsTimer = new Timer(2500, new ActionListener() { // Create a timer that fires every 2 seconds
           
                @Override
                public void actionPerformed(ActionEvent e) {
                    placeCrystals() ; // Call the placePipes method to create a new pipe
                }
            }) ;
       
        placeCrystalsTimer.start() ; // Start the place pipes timer

        //game timer
        gameLoop = new Timer(1000/60, this) ;// Create a timer that fires every 1000/60 milliseconds (60 FPS)
        gameLoop.start() ; // Start the game loop timer
    }  
    
    public void placeCrystals() {
        int randomCrystalY = (int) (CrystalY - CrystalHeight/4 - Math.random()*(CrystalHeight/2)) ; // Generate a random Y coordinate for the pipe
        
        int openingSpace = boardHeight/4 ; // Space between the top and bottom pipes
        // Create a new Pipe object with the top and bottom pipe images
        Crystal topCrystal = new Crystal(topCrystalImg) ; // Create a new Pipe object with the top pipe image
        topCrystal.y = randomCrystalY ; // Set the Y coordinate of the top pipe
        Crystals.add(topCrystal) ; // Add the pipe to the pipes ArrayList
    
        Crystal bottomCrystal = new Crystal(bottomCrystalImg) ; // Create a new Pipe object with the bottom pipe image
        bottomCrystal.y= topCrystal.y + CrystalHeight + openingSpace ; // Set the Y coordinate of the bottom pipe
        Crystals.add(bottomCrystal) ; // Add the pipe to the pipes ArrayList
    
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g) ; // Call the superclass's paintComponent method to clear the panel
        draw(g) ; // Call the draw method to draw the game elements
    }

    public void draw(Graphics g) {
        // Draw the background image
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null) ;
        // Draw the bird image
        g.drawImage(wizard.img, wizard.x, wizard.y, wizard.width, wizard.height, null) ;
       
        //pipes
        for (int i =0 ; i < Crystals.size() ; i++) { // Loop through the pipes ArrayList
            Crystal Crystal = Crystals.get(i) ; // Get the current pipe
            g.drawImage(Crystal.img, Crystal.x, Crystal.y, Crystal.width, Crystal.height, null) ; // Draw the pipe image
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
        //bird 
        velocityY += gravity ; // Apply gravity to the bird's velocity
        wizard.y += velocityY ;
        wizard.y = Math.max(wizard.y , 0) ;// Update the Y coordinate of the bird based on its velocity
    
        //pipes
        for (int i = 0 ; i < Crystals.size() ; i++) { // Loop through the pipes ArrayList
            Crystal Crystal = Crystals.get(i) ; // Get the current pipe
            
            Crystal.x += velocityX ; // Move the pipe to the left based on its velocity
            if (collision(wizard, Crystal) ) {
                gameOver = true ; // Set the game over flag to true if there is a collision
            }
        }

        if (wizard.y > boardHeight) { // Check if the bird has fallen below the bottom of the screen
            gameOver = true ; // Set the game over flag to true
        }   

        for (Crystal Crystal : Crystals) { // Loop through the pipes ArrayList
            if (!Crystal.passed && wizard.x > Crystal.x + Crystal.width) { // Check if the bird has passed the pipe
            score += 0.5; // Increment the score
            Crystal.passed = true; // Set the passed flag to true for the pipe
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
        placeCrystalsTimer.stop() ; // Stop the place pipes timer
        
    }

}

  @Override
  public void keyTyped(KeyEvent e) { }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Check if the space key is pressed
        velocityY = -9 ; // Set the bird's velocity to -6 when the space key is pressed
        if (gameOver){
            //restart the game by resetiing the conditions
            wizard.y=wizardY ; // Reset the bird's Y coordinate to the initial position
            velocityY=0;
            Crystals.clear() ; // Clear the pipes ArrayList
            score = 0 ; // Reset the score to 0
            gameOver = false ; // Set the game over flag to false   
            gameLoop.start() ; // Start the game loop timer
            placeCrystalsTimer.start() ; // Start the place pipes timer
       
        }
    }
}
   

  @Override
  public void keyReleased(KeyEvent e) { }

  

}

