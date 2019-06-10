package com.bosch.pai.ipsadmin.bearing.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Training data.
 */
public final class TrainingData {

    private volatile static TrainingData trainingDataInstance;

    /**
     * List of all room data training files.
     */
    private ArrayList<String> trainDataFileNames;

    /**
     * List of file locations for all training files.
     */
    private ArrayList<String> trainDataFileLocs = new ArrayList<>();


    private String siteName = "office";

    private String userName = "bob";

    /**
     * The Num training classes.
     */
    private int numTrainingClasses = 0;


    private TrainingData() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TrainingData getInstance() {
        if (trainingDataInstance == null) {
            trainingDataInstance = new TrainingData();
        }
        return trainingDataInstance;
    }

    /**
     * Clear training data.
     */
    public void clearTrainingData() {
        trainDataFileNames.clear();
        trainDataFileLocs.clear();
    }


    /**
     * Gets train data file name at location.
     *
     * @param index the index
     * @return the train data file name at location
     */
// ------------------------------ trainDataFileNames ------------------------------------ //
    public String getTrainDataFileNameAtLocation(int index) {
        return trainDataFileNames.get(index);
    }

    /**
     * Add train data file name.
     *
     * @param fileName the file name
     */
    public void addTrainDataFileName(String fileName) {
        trainDataFileNames.add(fileName);
    }

    /**
     * Gets train data file names size.
     *
     * @return the train data file names size
     */
    public int getTrainDataFileNamesSize() {
        return trainDataFileNames.size();
    }

    /**
     * Gets train data file names.
     *
     * @return the train data file names
     */
    public List<String> getTrainDataFileNames() {
        return Collections.unmodifiableList(trainDataFileNames);
    }

    /**
     * Initialize train data file names.
     */
    public void initializeTrainDataFileNames() {
        trainDataFileNames = new ArrayList<>();
    }


    /**
     * Add train data file loc.
     *
     * @param fileName the file name
     */
// ------------------------------ trainDataFileLocs ------------------------------------ //
    public void addTrainDataFileLoc(String fileName) {
        trainDataFileLocs.add(fileName);
    }

    /**
     * Gets train data file locations.
     *
     * @return the train data file locations
     */
    public List<String> getTrainDataFileLocations() {
        return Collections.unmodifiableList(trainDataFileLocs);
    }

    /**
     * Initialize train data file locations.
     */
    public void initializeTrainDataFileLocations() {
        trainDataFileLocs = new ArrayList<>();
    }


    /**
     * Gets site name.
     *
     * @return the site name
     */
// ------------------------------ siteName ------------------------------------ //
    public String getSiteName() {
        return siteName;
    }

    /**
     * Sets site name.
     *
     * @param siteName the site name
     */
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    /**
     * Gets user name.
     *
     * @return the user name
     */
// ------------------------------ userName ------------------------------------ //
    public String getUserName() {
        return userName;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets num training classes.
     *
     * @return the num training classes
     */
// ------------------------------ numTrainingClasses ------------------------------------ //
    public int getNumTrainingClasses() {
        return numTrainingClasses;
    }

    /**
     * Sets num training classes.
     *
     * @param numTrainingClasses the num training classes
     */
    public void setNumTrainingClasses(int numTrainingClasses) {
        this.numTrainingClasses = numTrainingClasses;
    }
}
