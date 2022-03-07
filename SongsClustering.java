import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class SongsClustering {

    // init Panel
    static final int WIDTH = 600;
    static final int HEIGHT = 400;
    
    static DrawingPanel panel = new DrawingPanel(WIDTH, HEIGHT);
    static Graphics2D g = panel.getGraphics();

    // enable double buffering
    static BufferedImage offscreen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    static Graphics2D osg = offscreen.createGraphics();

    // declare data arrays
    public static String[] name;    // song name
    public static int[] dance;      // danceability (y coordinate)
    public static int[] energy;     // energy       (x coordinate)
    public static int[] cluster;    // cluster assignments

    // declare centroid arrays
    public static int[] cx;         // centroid x-coordinates
    public static int[] cy;         // centroid y-coordinates
    public static Color[] color;    // centroid pen color

    // declare number of centroids to form clusters around
    public static int numClusters = 8;
    
    public static void main(String[] args) throws FileNotFoundException {
        // read .txt file from command line - filename is hard-coded for debugging
        String fileName = "songs.txt"; //args[0];

        // YOU DO: complete the method load songs
        loadSongs(fileName);

        // test if data has been loaded correctly
        System.out.println(name[0] + " " + dance[0] + " " + energy[0]);
        System.out.println(name[name.length-1] + " " + dance[name.length-1] + " " + energy[name.length-1]);

        // set random initial centroid coordinates
        initCentroids();

        // init stop condition
        double sumDist;

        do {
            // YOU DO: complete helper method findMin to assign each song to its nearest centroid
            assignSongs();

            // YOU DO: complete method to update centroid coordinates
            sumDist = shiftCentroids();

            // draw background
            osg.setColor(Color.BLACK);
            osg.fillRect(0,0,WIDTH, HEIGHT);

            // plot centroid coordinates
            graphCentroids();

            // YOU DO: complete method to plot song coordinates
            graphSongs();

        } while (sumDist > 0.05);
        System.out.println("~fin");
    }

    // YOU DO: complete method to read in song attributes
    public static void loadSongs(String fileName) throws FileNotFoundException {
        
        // init scanner
        Scanner input = new Scanner(new File(fileName));

        // move to next word with standard (whitespace) delimiter
        input.next();

        // record number of songs
        int N = Integer.parseInt(input.next());

        // skip 2 fields to move on to first line of data
        input.nextLine();  
        input.nextLine();  

        // set delimiter from whitespace to commas - read in csv format
        input.useDelimiter(",");

        // define array size
        name = new String[N];
        dance = new int[N];
        energy = new int[N];
        cluster = new int[N];

        // YOU DO: read N tasks into arrays name, dance, energy, and length
        for (int i = 0; i < N; i++) {
            // move from id to name

            // load song name

            // move from artist_id to dance

            // import dance (y) and energy (x), scaled to the Panel size

            // move to next line

        }

        // close .txt file
        input.close();
    }

    // called by method: assignSongs
    // YOU DO: complete method to return the index of centroid with minimum distance
    public static int findMin(double[] dist) {
        // init vars to track index and value of minimum distance in dist array
        int minIndex = 0;
        double minDist = dist[0];
        for (int i = 1; i < dist.length; i++) {
            // YOU DO: find index and value of the minimum distance in the dist array
            
        }
        return minIndex;
    }

    // YOU DO: complete method to update centroids
    public static double shiftCentroids() {
            // declare arrays to accumulate centroid data
            int[] coordSumsX = new int[numClusters];    // sum of x-coordinate values for all songs in each cluster 
            int[] coordSumsY = new int[numClusters];    // sum of y-coordinate values for all songs in each cluster
            int[] coordCounts = new int[numClusters];   // number of songs in each cluster
            
            for (int i = 0; i < cluster.length; i++) {
                // YOU DO: store sum of x, y song coordinate values for each cluster in coordSumsX, coordSumsY

                // YOU DO: store total number of songs for each cluster in coordCounts

            }
    
            // init counter to sum deltas from all centroid shifts
            double sum = 0;

            // update centoid arrays with average x, y coordinates for their clusters
            for (int i = 0; i < numClusters; i++) {
                // store old values
                int cx0 = cx[i];
                int cy0 = cy[i];
    
                // update new values
                if (coordCounts[i] > 0) {
                    cx[i] = (int) ((double) coordSumsX[i] / coordCounts[i]);
                    cy[i] = (int) ((double) coordSumsY[i] / coordCounts[i]);    
                }
                
                // sum all deltas from centroid shifts
                sum += distance(cx0, cy0, cx[i], cy[i]); 
            }
            return sum;    
        }
    
    // YOU DO: complete method to graph energy vs dance for each song
    public static void graphSongs() {
        // size of circle to center over each plot-point 
        final int size = 2;

        // plot all songs, color-coded by nearest centroid
        for (int i = 0; i < name.length; i++) {

            // YOU DO: set osg pen color by cluster assignment

            // YOU DO: find x, y arguments for circle centered over plot-point
            
            // YOU DO: plot song to osg
     
        }
        // copy off screen image onto DrawingPanel
        g.drawImage(offscreen, 0, 0, null);  

        // wait 500 ms (1/2 second)
        panel.sleep(500);
    }

/*  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ALL METHODS BELOW ARE COMPLETE, BUT MAY SERVE AS HELPFUL REFERENCES!  
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  */

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
    
    // assign each song to its nearest centroid
    public static void assignSongs() {

        // loop over all songs
        for (int i = 0; i < cluster.length; i++) {
            // declare distance data array
            double[] dist = new double[numClusters];

            // load distance data into array 
            for (int j = 0; j < numClusters; j++) {
                dist[j] = distance(energy[i], dance[i], cx[j], cy[j]);
            }
            cluster[i] = findMin(dist);
        }
    }
    
    // standard L2 norm - root of sum of squares distance formula
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
    } 

}   
    
