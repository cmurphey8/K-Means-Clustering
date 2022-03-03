//*******************************************************************
//
//   File: TaskMan.java
//
//   Author: CS112      Email:  
//
//   Class: NBody 
// 
//   Time spent on this problem: 
//   --------------------
//   
//      This program 
//
//*******************************************************************
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
    public static int[] length;     // duration     (ms)
    public static int[] cluster;    // cluster assignments

    public static int[] cx;         // centroid x-coordinates
    public static int[] cy;         // centroid y-coordinates

    public static int[] cx0;         // old centroid x-coordinates
    public static int[] cy0;         // old centroid y-coordinates

    public static Color[] color;    // centroid pen color
    
    public static void main(String[] args) {
        // read .txt file from command line
        String fileName = "songs.txt"; //args[0];

        // YOU DO: complete the method load songs
        loadSongs(fileName);

        // test if data has been loaded correctly
        System.out.println(name[0] + " " + dance[0] + " " + energy[0] + " " + length[0]);
        System.out.println(name[name.length-1] + " " + dance[name.length-1] + " " + energy[name.length-1] + " " + length[name.length-1]);

        // BEGIN K-MEANS ALGORITHM

        int numClusters = 5;

        // set random initial centroid coordinates
        initCentroids(numClusters);

        do {
            // assign each song to its nearest centroid
            assignSongs(numClusters);

            // update centroids as mean coordinates of all elements in its group
            shiftCentroids(numClusters);

            // draw background
            osg.setColor(Color.BLACK);
            osg.fillRect(0,0,WIDTH, HEIGHT);

            // plot data
            graphCentroids(numClusters);
            graphSongs();
        } while (norm(numClusters) > 0.05);
        System.out.println("~fin");
    }

    // assign each song to its nearest centroid
    public static void assignSongs(int numClusters) {
        for (int i = 0; i < cluster.length; i++) {
            double[] dist = new double[numClusters];
            for (int j = 0; j < numClusters; j++) {
                dist[j] = distance(energy[i], dance[i], cx[j], cy[j]);
            }
            cluster[i] = findMin(dist);
        }
    }

    // set random initial centroid coordinates
    public static int findMin(double[] dist) {
        int minIndex = 0;
        double minDist = dist[0];
        for (int i = 1; i < dist.length; i++) {
            if (dist[i] < minDist) {
                minIndex = i;
                minDist = dist[i];
            }
        }
        return minIndex;
    }

    // set random initial centroid coordinates
    public static double norm(int numClusters) {
        double sum = 0;
        for (int i = 0; i < numClusters; i++) {
            sum += distance(cx0[i], cy0[i], cx[i], cy[i]); 
        }
        return sum;
    }

    // set random initial centroid coordinates
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
    }

    // update centroids as mean coordinates of all elements in its group
    public static void shiftCentroids(int numClusters) {
        int[] coordSumsX = new int[numClusters]; 
        int[] coordSumsY = new int[numClusters]; 
        int[] coordCounts = new int[numClusters]; 
        
        for (int i = 0; i < cluster.length; i++) {
            coordCounts[cluster[i]]++;
            coordSumsX[cluster[i]] += energy[i];
            coordSumsY[cluster[i]] += dance[i];
        }

        for (int i = 0; i < numClusters; i++) {
            cx0[i] = cx[i];
            cy0[i] = cy[i];

            cx[i] = (int) ((double) coordSumsX[i] / coordCounts[i]);
            cy[i] = (int) ((double) coordSumsY[i] / coordCounts[i]);
        }
    }

    // set random initial centroid coordinates
    public static void initCentroids(int numClusters) {
        color = new Color[numClusters];
        cx = new int[numClusters];
        cy = new int[numClusters];
        cx0 = new int[numClusters];
        cy0 = new int[numClusters];
        

        for (int i = 0; i < numClusters; i++) {
            final double SHIFT = .1;
            final int maxColor = 255;
            cx[i] = rand(WIDTH, SHIFT);
            cy[i] = rand(HEIGHT, SHIFT);
            color[i] = new Color(rand(maxColor, SHIFT), rand(maxColor, SHIFT), rand(maxColor, SHIFT));
        }
    }

    public static int rand(int max, double SHIFT){
        return (int) (Math.random() * ((1 - SHIFT) * max) + SHIFT / 2 * max);
    }

    // graph centroids
    public static void graphCentroids(int numClusters) {
        int size = 4;
        for (int i = 0; i < numClusters; i++) {

            osg.setColor(color[i]);

            // find coordinates for centered circles over each plot-point
            int x = (int) (cx[i] - size / 2.0);
            int y = (int) (cy[i] + size / 2.0) ;
            
            // plot coordinates for each centroid
            osg.drawOval(x, y, size, size);    
        }
    }

    // graph energy vs dance for each song
    public static void graphSongs() {
        int size = 2;
        for (int i = 0; i < name.length; i++) {

            // set pen color by cluster assignment
            osg.setColor(color[cluster[i]]);

            // find coordinates for centered circle over plot-point
            int x = (int) (energy[i] - size / 2.0);
            int y = (int) (dance[i] + size / 2.0) ;
            
            // plot coordinates
            osg.fillOval(x, y, size, size);      
        }
        // copy off screen image onto DrawingPanel
        g.drawImage(offscreen, 0, 0, null);  

        // wait 500 ms (1/2 second)
        panel.sleep(500);
    }

    // read in song attributes
    public static void loadSongs(String fileName) {
        Scanner input = null;
        
        // File IO with exception handling
        try {
            input = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("Could not open " + fileName);
            System.exit(1);
        }

        // move to next word with standard (whitespace) delimiter
        input.next();

        // record number of songs
        int N = Integer.parseInt(input.next());

        // skip 2 fields to move on to first line of data
        skipFields(input, 2);  

        // set delimiter from whitespace to commas
        input.useDelimiter(",");

        // define array size
        name = new String[N];
        dance = new int[N];
        energy = new int[N];
        length = new int[N];
        cluster = new int[N];

        // YOU DO: read N tasks into arrays name, dance, energy, and length
        for (int i = 0; i < N; i++) {
            // move from id to name
            input.next();
            name[i] = input.next();

            // move from artist_id to dance
            input.next();

            // import dance (y) and energy (x), scaled to the Panel size
            dance[i] = (int) (HEIGHT - input.nextDouble() * HEIGHT);
            energy[i] = (int) (input.nextDouble() * WIDTH) ;

            // move from key to duration
            skipFields(input, 5);
            length[i] = input.nextInt(); 
        }

        // close .txt file
        input.close();
    }

    public static void skipFields(Scanner file, int numFields) {
        for (int i = 0; i < numFields; i++) file.next();
    }
}
