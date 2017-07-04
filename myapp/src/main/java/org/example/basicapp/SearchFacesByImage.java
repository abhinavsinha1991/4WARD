package org.example.basicapp;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;


public class SearchFacesByImage {

    public static void main(String[] args) throws Exception{
        Float similarityThreshold = 90F;
        String collectionId="bharat";
        int maxFaces = 2;
        String sourceImage = "/home/abhinav/Pictures/pic2.png";
        ByteBuffer sourceImageBytes=null;

        AWSCredentials credentials;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
                    + "Please make sure that your credentials file is at the correct "
                    + "location (/Users/userid/.aws/credentials), and is in valid format.", e);
        }


        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();


        //Load source and target images and create input parameters
        try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
            sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        }
        catch(Exception e)
        {
            System.out.println("Failed to load source image " + sourceImage);
            System.exit(1);
        }


        Image source=new Image()
                .withBytes(sourceImageBytes);

        SearchFacesByImageResult searchFacesByImageResult =
                callSearchFacesByImage(collectionId, source, similarityThreshold, maxFaces,
                        rekognitionClient);
        if(!searchFacesByImageResult.getFaceMatches().isEmpty()) {
            System.out.println("Faces matching largest face in image  " + sourceImage);
            List<FaceMatch> faceImageMatches = searchFacesByImageResult.getFaceMatches();
            for (FaceMatch face : faceImageMatches) {
                System.out.println("Confidence: " + face.getFace().getConfidence().toString());
                System.out.println();
            }
        }
        else{
            System.out.println("No matches found in image  " + sourceImage);
        }
    }

    private static SearchFacesByImageResult callSearchFacesByImage(String collectionId,
                                                                   Image image, Float threshold, int maxFaces, AmazonRekognition amazonRekognition) {
        SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                .withCollectionId(collectionId)
                .withImage(image)
                .withFaceMatchThreshold(threshold)
                .withMaxFaces(maxFaces);
        return amazonRekognition.searchFacesByImage(searchFacesByImageRequest);
    }

}
