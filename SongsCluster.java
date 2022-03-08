//*******************************************************************
//   File: SongsCLuster.java
//   Dependancies: songs.txt, SongsHelper.java, DrawingPanel.java  
//*******************************************************************

import java.awt.Color;
import java.io.FileNotFoundException;

public class SongsCluster {

    // init Panel
    static final int WIDTH = 600;
    static final int HEIGHT = 400;

    // declare song data arrays
    public static String[] name;    // song name
    public static int[] dance;      // danceability (y coordinate)
    public static int[] energy;     // energy       (x coordinate)
    public static int[] cluster;    // cluster assignments

    // store number of songs in our data set
    public static int numSongs;

    // declare centroid arrays
    public static int[] cx;         // centroid x-coordinates
    public static int[] cy;         // centroid y-coordinates
    public static Color[] color;    // centroid pen color

    // store number of centroids to form clusters around
    public static int numClusters = 6;


    
    public static void main(String[] args) throws FileNotFoundException {

        // init arrays
        SongsHelper.loadSongs();
        SongsHelper.initCentroids();

        // define length constant for song data arrays
        numSongs = name.length;

        // init algorithm stop condition
        double sumDist;

        do {
            // YOU DO: complete helper method findMin to assign each song to its nearest centroid
            assignSongs();

            // YOU DO: complete method to update centroid coordinates
            sumDist = shiftCentroids();

            // plot centroid coordinates
            SongsHelper.update();

        } while (sumDist > 0.05);
        System.out.println("~fin");
    }

    // YOU DO: complete method to update centroids
    public static double shiftCentroids() {
        // declare arrays to accumulate centroid data
        int[] coordSumsX = new int[numClusters];    // sum of x-coordinate values for all songs in each cluster 
        int[] coordSumsY = new int[numClusters];    // sum of y-coordinate values for all songs in each cluster
        int[] coordCounts = new int[numClusters];   // number of songs in each cluster
        
        for (int i = 0; i < numSongs; i++) {
            // store total number of songs for each cluster in coordCounts 
            coordCounts[cluster[i]]++;

            // YOU DO: store sum of x, y song coordinate values for each cluster in coordSumsX, coordSumsY        
            coordSumsX[cluster[i]] += energy[i];
            coordSumsY[cluster[i]] += dance[i];
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
            sum += SongsHelper.distance(cx0, cy0, cx[i], cy[i]); 
        }
        return sum;    
    }

    // assign each song to its nearest centroid
    public static void assignSongs() {

        // loop over all songs
        for (int i = 0; i < numSongs; i++) {

            // store in dist array the distance from this song to each centroid 
            double[] dist = new double[numClusters];

            // loop over all centroids
            for (int j = 0; j < numClusters; j++) {
                dist[j] = SongsHelper.distance(energy[i], dance[i], cx[j], cy[j]);
            }

            // find the minimum distance in the dist array for this song
            cluster[i] = findMin(dist);
        }
    }
    
    // YOU DO: complete method to return the index of centroid with minimum distance
    public static int findMin(double[] dist) {
        // init var to track index of minimum distance in dist array
        int minIndex = 0;

        // YOU DO: find index of the minimum distance in the dist array
        for (int i = 1; i < dist.length; i++) {
            if (dist[i] < dist[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }
}   