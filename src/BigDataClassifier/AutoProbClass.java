/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BigDataClassifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import weka.clusterers.AbstractDensityBasedClusterer;
import weka.clusterers.Clusterer;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author u1457710
 */
public class AutoProbClass  extends weka.clusterers.AbstractClusterer implements Clusterer {

    @Override
    public void buildClusterer(Instances data) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int clusterInstance(Instance instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] distributionForInstance(Instance instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int numberOfClusters() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Capabilities getCapabilities() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static final int CLOSENESS_THRESHOLD = 1;
    private static ArrayList<DenseInstance> cloud = new ArrayList<>();
    private static ArrayList<String> cloudLabels = new ArrayList<>();
    private static ArrayList<String> labels = new ArrayList<>();
    private static ArrayList<BigDataClassifier.Cloud> clouds = new ArrayList<>();
    private static ArrayList<DenseInstance> outliers = new ArrayList<>();
    private static ArrayList<Float> simPercent = new ArrayList<>();
    private static EuclideanDistance eu;
    private static double instanceNewIdentifier,instanceOldIdentifier ;
    //private static ArrayList<DenseInstance> oldOutliers = new ArrayList<>();
    AbstractDensityBasedClusterer densityClass = new MakeDensityBasedClusterer();
    //CheckClusterer check = new CheckClusterer();
    BigDataClassifier.ClusterEvaluator clustEval = new BigDataClassifier.ClusterEvaluator();
    
     /**
     *
     * @param dataset
     */
    public void autoProbClass(Instances dataset) {

        try {

            //Define the initial zone of influence ZI
            double initialZI = 0.3;
            int k = 0; int newLabelCounter = 0;
            Instances xk = new Instances(dataset);
            eu = new EuclideanDistance(xk);
            double ncZI = initialZI;
            int ncPoints = 0;
            //nomenclature for the mean of the data samples
            double ncFocalpoint = 0;
            ListIterator iterator = xk.listIterator();
            //start reading in the instances
            while (iterator.hasNext()) {
                if (k == 0) {
                    DenseInstance nc = (DenseInstance) iterator.next();
                    //nc.setClassValue(k);
                    ncFocalpoint = xk.meanOrMode(k);
                    ncZI = initialZI;
                    ncPoints = 1;
                    //first inference rule
                    cloud.add(nc);
                    cloudLabels.add("Class"+k);
                    labels.add("Class"+k);
                    System.out.println(cloudLabels);
//                    Cloud cloudtoadd = new Cloud(ncFocalpoint,ncZI,ncPoints, (DenseInstance) xk.instance(k));
//                    clouds.add(cloudtoadd);
                    System.out.println("First cloud added");
                    System.out.println("Instance identifier for first cloud " + nc + " is: " + getInstanceIdentifier(nc));
                    System.out.println("==========");
                } else {
                    //read next instance
                    DenseInstance nc = (DenseInstance) iterator.next();
                    //System.out.println(nc.numValues());
                    //get the identifier for that instance
                    System.out.println("\n" + "Instance identifier for " + nc + " is: " + getInstanceIdentifier(nc));
                    //System.out.println(nc.weight());
                    System.out.println("==========");

                    ArrayList<Boolean> booleanList = compareInstancesTest(nc);
//                    ArrayList<String> similarListMeasure = compareInstancesTest(nc);
                    System.out.println("List of how close they are: " + booleanList);
                    System.out.println("List of how close they are: " + simPercent);
                   
                    float maximum = Collections.max(simPercent);
                    System.out.println("The closest is " + maximum + "% at index point " + simPercent.indexOf(maximum) ); 
                    if(maximum!=0.0){
                        //cloudLabels.add("Class"+ simPercent.indexOf(maximum));
                        labels.add(labels.get(simPercent.indexOf(maximum)));
                        System.out.println(labels);
                    }
                    else{
                        cloudLabels.add("Class"+cloudLabels.size());
                        labels.add(cloudLabels.get(cloudLabels.size()-1));
                        System.out.println(labels);
                        //newLabelCounter++;
                        System.out.println(cloudLabels.size());
                        System.out.println(cloudLabels);
                    }
                    
                }
                k = k + 1;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static ArrayList<Boolean> compareInstancesTest(DenseInstance instanceNew) {
        //String instanceAIdentifier = getInstanceIdentifier(instanceA);
        int trueScore = 0;
        int falseScore = 0;
        ArrayList<Boolean> howCloseList = new ArrayList<>();
        simPercent = new ArrayList<>();
        
        for (DenseInstance instanceOld : cloud) {
            System.out.println("==================");
            //String instanceBIdentifier = getInstanceIdentifier(instanceB);
            for (int i = 0; i < instanceNew.numAttributes(); i++) {
               instanceNewIdentifier = instanceNew.value(i);
//               System.out.println("INSTANCE VALUE OF NEW data " + getInstanceIdentifier(instanceNew) +" @ INDEX POINT " + i + " IS  = " + instanceNewIdentifier );
               instanceOldIdentifier = instanceOld.value(i);
//               System.out.println("INSTANCE VALUE OF EXISTING data " + getInstanceIdentifier(instanceOld) + " @ INDEX POINT " + i + " IS  = " + instanceOldIdentifier );
                if (instanceNewIdentifier == instanceOldIdentifier) {
//                	System.out.println("true they are same \n");
                        trueScore = trueScore + 1;
                }
                else if(instanceNewIdentifier != instanceOldIdentifier) {
                    falseScore = falseScore + 1;
//                	System.out.println("false they are the same \n");
                }
            }
            
            System.out.println("Disimilar estimate " + (float)falseScore/instanceOld.numAttributes());
            //find the fraction of the number of dissimilar values in %
            float diSimilarityMeasure = (float)falseScore/instanceOld.numAttributes() * 100;
            float similarityMeasure = 100 - diSimilarityMeasure;
            Instance instance1 = instanceNew; Instance instance2 = instanceOld;
//            NormalizableDistance nd = new EuclideanDistance();
//            double euclideandistanceMeasure = eu.distance(instance1, instance2);
//            System.out.println("---------------- EUCdist " + euclideandistanceMeasure );
            System.out.println("THEY ARE " + similarityMeasure + "% SIMLIAR");
            System.out.println("SCORE PERCENTAGE OF THE NUMBER OF FALSE MATCHES " + diSimilarityMeasure + "%");
            
            if (diSimilarityMeasure > 20){
                howCloseList.add(false);
                //The distance measure between 
                double distanceMeasure = eu.distance(instanceOld, instanceNew);
                System.out.println("---EUCLIDEAN DISTANCE MEASURE IS--- = " + distanceMeasure);
                simPercent.add(0.0f);
            }
            else{
                howCloseList.add(true);
                double distanceMeasure = eu.distance(instanceOld, instanceNew);
                System.out.println("---EUCLIDEAN DISTANCE MEASURE IS--- = " + distanceMeasure);
                simPercent.add(similarityMeasure);
            }

            System.out.println("--TRUE SCORE = " + trueScore);
            System.out.println("--FALSE SCORE = " + falseScore);
            trueScore = 0;
            falseScore = 0;
        }
        cloud.add(instanceNew);
        return howCloseList;
    } 

    private static String getInstanceIdentifier(Instance instance) {

        String identifier = "";

        try {
            for (int i = 0; i < instance.numAttributes(); i++) {
                //System.out.println(identifier);
                identifier += (int) instance.value(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return identifier;
    }

    
}