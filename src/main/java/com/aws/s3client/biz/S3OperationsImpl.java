package com.aws.s3client.biz;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.utils.AttributeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * implementation for S3Operations.
 */
public class S3OperationsImpl implements S3Operations{
    public final S3Client s3Client;

    public S3OperationsImpl(){
        s3Client = S3Client.builder()
                .httpClient(
                        UrlConnectionHttpClient.builder().buildWithDefaults(AttributeMap.builder().build())).build();
    }

    @Override
    public List<String> getBucketLists() {
        List<String> bucketNames = new ArrayList<>();
        List<Bucket> buckets= s3Client.listBuckets().buckets();
        for(Bucket bucketName:buckets){
            bucketNames.add(bucketName.name());
        }
        return bucketNames;
    }

    @Override
    public String putObject(String bucketName, String s3Key, File file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(s3Key).build();
        RequestBody requestBody = RequestBody.fromFile(file);
        try{
            s3Client.putObject(putObjectRequest,requestBody);
        }catch(final AwsServiceException | SdkClientException e){
            System.out.println(e);
            return "Upload Not Successful, AWS Exception Occurred!!!";
        }
        return "Upload To S3 Bucket Success!!!";
    }

    @Override
    public String deleteObject(String bucketName,String s3Key) {
        DeleteObjectRequest deleteObjectRequest =
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();
        try  {
            s3Client.deleteObject(deleteObjectRequest);
        } catch (final AwsServiceException | SdkClientException e) {
            System.out.println(e);
            return "Delete not successful, AWS Exception Occured!!!";
        }
        return "Delete Successful!!";
    }

    @Override
    public String getObject(String bucketName,String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(s3Key).build();
        try {
            s3Client.getObject(getObjectRequest, new File("C:\\Users\\" + s3Key).toPath());
        } catch (final NoSuchKeyException e) {
            return "No such file exists in selected S3 bucket";
        } catch (final AwsServiceException | SdkClientException e) {
            return "Download Failed with AWS Exception!!!";
        }
        return "Downloaded S3 Object Successfully to c drive users folder!!!";
    }

    @Override
    public String createBucket(String newBucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(newBucketName).build());
        }catch (final AwsServiceException | SdkClientException e) {
            System.out.println(e);
            return "Bucket Creation is not successful, AWS Exception Occurred!!!";
        }
        return "Bucket Creation Successful!!!";
    }
}
