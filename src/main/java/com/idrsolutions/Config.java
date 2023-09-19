package com.idrsolutions;

import com.idrsolutions.image.encoder.OutputFormat;

public class Config {
    /** The format to convert files into */
    public static final OutputFormat DST_FORMAT = OutputFormat.PNG;

    /**
     * The directory within the bucket where files are sourced from
     * <br>
     * This must match the Prefix configured in the Lambda function's S3 Trigger
     */
    public static final String SRC_DIR = "input/";

    /** The destination in the bucket where converted files are placed */
    public static final String DST_DIR = "output/";

    /** Whether to delete src files after conversion */
    public static final boolean DELETE_SRC = true;

    /** Whether to delete src files that failed to convert */
    public static final boolean DELETE_FAILED = true;


}
