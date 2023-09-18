package com.idrsolutions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.idrsolutions.image.JDeli;
import com.idrsolutions.image.encoder.OutputFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


/**
 * A AWS Lambda Handler to convert
 */
public class Handler implements RequestHandler<S3Event, String> {
    /** The format to convert files into */
    static final OutputFormat DST_FORMAT = OutputFormat.WEBP;

    /** The directory within the bucket where files are sourced from */
    static final String SRC_DIR = "input/";

    /** The destination in the bucket where converted files are placed */
    static final String DST_DIR = "output/";

    /** Whether to delete src files after conversion */
    static final boolean DELETE_SRC = true;

    /** Whether to delete src files that failed to convert */
    static final boolean DELETE_FAILED = true;

    @Override
    public String handleRequest(final S3Event s3Event, final Context context) {
        final LambdaLogger logger = context.getLogger();

        final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        for (final S3EventNotification.S3EventNotificationRecord s3Record : s3Event.getRecords()) {
            final String srcBucket = s3Record.getS3().getBucket().getName();
            final String srcKey = s3Record.getS3().getObject().getUrlDecodedKey();

            try {
                logger.log(String.format("Converting %s: %s", srcBucket, srcKey));

                // Get File
                final S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey));

                // Quick validation that the file hasn't already been converted
                if (s3Object.getObjectMetadata().getUserMetadata().containsKey("converted")) {
                    continue;
                }

                final InputStream fileStream = s3Object.getObjectContent();

                // Convert
                final ByteArrayOutputStream output = new ByteArrayOutputStream();
                JDeli.convert(fileStream, output, DST_FORMAT.name());

                // Put file
                final byte[] buffer = output.toByteArray();
                final InputStream is = new ByteArrayInputStream(buffer);

                final ObjectMetadata meta = new ObjectMetadata();
                meta.setContentType(getMimeType());
                meta.setContentLength(buffer.length);
                meta.addUserMetadata("converted", "true");

                final String destKey = DST_DIR + getKeyWithoutExt(srcKey).substring(SRC_DIR.length()) + getExtension();

                s3Client.putObject(srcBucket, destKey, is, meta);
                logger.log(String.format("Converted %s: %s", srcBucket, destKey));

                if (DELETE_SRC) {
                    s3Client.deleteObject(srcBucket, srcKey);

                    logger.log(String.format("Deleted %s: %s", srcBucket, srcKey));
                }
            } catch (final Exception e) {
                logger.log(String.format("Failed to convert %s: %s%n%s", srcBucket, srcKey, e.getMessage()));

                if (DELETE_FAILED) {
                    s3Client.deleteObject(srcBucket, srcKey);

                    logger.log(String.format("Deleted failed conversion %s: %s", srcBucket, srcKey));
                }
            }
        }

        return "Ok";
    }

    /**
     * Get the key without the trailing file extension
     * @param key The key to strip the extension from
     * @return They key without its file extension
     */
    static String getKeyWithoutExt(final String key) {
        final int end = key.lastIndexOf(".");

        if (end >= 0) {
            return key.substring(0, end);
        } else {
            return key;
        }
    }

    /**
     * Get the file extension of the given file format
     *
     * @return The extension
     */
    String getExtension() {
        switch (Handler.DST_FORMAT) {
            case BMP:
                return ".bmp";
            case GIF:
                return ".gif";
            case HEIC:
                return ".heic";
            case JPEG2000:
            case JPEG:
                return ".jpg";
            case PNG:
                return ".png";
            case TIFF:
                return ".tiff";
            case WEBP:
                return ".webp";
            default:
                return "";
        }
    }


    /**
     * Get the mimetype of the given file format
     *
     * @return the Mimetype
     */
    String getMimeType() {
        switch (Handler.DST_FORMAT) {
            case BMP:
                return "image/bmp";
            case GIF:
                return "image/gif";
            case HEIC:
                return "image/heic";
            case JPEG2000:
            case JPEG:
                return "image/jpeg";
            case PNG:
                return "image/png";
            case TIFF:
                return "image/tiff";
            case WEBP:
                return "image/webp";
            default:
                return "";
        }
    }
}