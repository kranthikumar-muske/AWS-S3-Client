package com.aws.s3client.biz;


import java.io.File;
import java.util.List;

public interface S3Operations {
    /**
     * gets list of buckets from S3.
     * @return
     */
    List<String> getBucketLists();
    /**
     * save object to S3 for a given bucket and key.
     * @return
     */
    String putObject(String bucketName, String s3Key, File file);
    /**
     * delete object from S3 for a given bucket and key.
     * @return
     */
    String deleteObject(String bucketName,String s3Key);
    /**
     * gets object from S3 for a given bucket and key.
     * @return
     */
    String getObject(String bucketName,String s3Key);
    /**
     * create a bucket in S3 store.
     * @return
     */
    String createBucket(String newBucketName);
}
