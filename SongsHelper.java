import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;


public class SongsHelper extends SongsCluster {
    
    // init drawing panel
    static DrawingPanel panel = new DrawingPanel(WIDTH, HEIGHT);
    static Graphics2D g = panel.getGraphics();

    // enable double buffering
    static BufferedImage offscreen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    static Graphics2D osg = offscreen.createGraphics();
    

    // update plots
    public static void update() {
        osg.setColor(Color.BLACK);
        osg.fillRect(0,0,WIDTH, HEIGHT);
        graphCentroids();
        graphSongs();
    }
            
    // graph centroids
    public static void graphCentroids() {
        // size of circle to center over x, y coordinate pairs 
        final int size = 4;

        // plot all centroids, color-coded
        for (int i = 0; i < numClusters; i++) {

            // set osg pen color assigned to centroid
            osg.setColor(color[i]);

            // find coordinates for circle centered over plot-point
            int x = (int) (cx[i] - size / 2.0);
            int y = (int) (cy[i] + size / 2.0) ;
            
            // plot centroid to osg
            osg.drawOval(x, y, size, size);    
        }
    }

    // graph energy vs dance for each song
    public static void graphSongs() {
        // size of circle to center over each plot-point 
        final int size = 2;

        // plot all songs, color-coded by nearest centroid
        for (int i = 0; i < name.length; i++) {

            // set osg pen color by cluster assignment
            osg.setColor(color[cluster[i]]);

            // find x, y arguments for circle centered over plot-point
            int x = (int) (energy[i] - size / 2.0);
            int y = (int) (dance[i] + size / 2.0) ;
            
            // plot song to osg
            osg.fillOval(x, y, size, size);      
        }
        // copy off screen image onto DrawingPanel
        g.drawImage(offscreen, 0, 0, null);  

        // wait 500 ms (1/2 second)
        panel.sleep(500);
    }

     // WE DO: read in song attributes
     public static void loadSongs() throws FileNotFoundException {
        
        // init scanner
        Scanner input = new Scanner(new File("songs.txt"));

        // move to next word with standard (whitespace) delimiter
        input.next();

        // record number of songs
        int N = Integer.parseInt(input.next());

        // skip 2 fields to move on to first line of data
        input.nextLine();  
        input.nextLine();  

        // define array size
        name = new String[N];
        dance = new int[N];
        energy = new int[N];
        cluster = new int[N];

        // read N tasks into arrays name, dance, energy, and length
        for (int i = 0; i < N; i++) {
            // WE DO: store all fields in next line of file as String array
            String[] nextLine = input.nextLine().split(",");

            // WE DO: store song name in name array
            name[i] = nextLine[1];

            // WE DO: store dance (y) and energy (x), scaled to the Panel size
            dance[i] = (int) (HEIGHT - Double.parseDouble(nextLine[3]) * HEIGHT);
            energy[i] = (int) (Double.parseDouble(nextLine[4]) * WIDTH) ;
        }

        // close .txt file
        input.close();
    }

    // set random initial centroid coordinates
    public static void initCentroids() {

        // initialize all centroid arrays to size numClusters
        color = new Color[numClusters];
        cx = new int[numClusters];
        cy = new int[numClusters];
        
        // random numbers in range helper constants
        final double remPercent = .25;
        final int maxColor = 255;

        // set random centroid locations and color assignments, excluding remPercent% of extreme values
        for (int i = 0; i < numClusters; i++) {    
            cx[i] = rand(WIDTH, remPercent);
            cy[i] = rand(HEIGHT, remPercent);
            color[i] = new Color(rand(maxColor, remPercent), rand(maxColor, remPercent), rand(maxColor, remPercent));
        }
    }
    
    // rng in range helper method
    public static int rand(int max, double remPercent){
        return (int) (Math.random() * ((1 - remPercent) * max) + remPercent / 2 * max);
    }

    // standard L2 norm - root of sum of squares distance formula
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
    } 
}