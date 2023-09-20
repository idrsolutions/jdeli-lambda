# jdeli-lambda
An AWS Lambda function for performing automatic image conversion using JDeli on files uploaded to an S3 Bucket.

This implementation uses a single bucket, with recursive execution protection using metadata.

This repo is a part of an example usage tutorial for JDeli, you can find the full tutorial on our [Support Site](https://support.idrsolutions.com/jdeli/examples/aws-lambda)

-----

# Customisation #
Several configuration options have been exposed in `con.idrsolutions.Config`:

| Option        | Default | Description                                                                                                                        | 
|---------------|---------|------------------------------------------------------------------------------------------------------------------------------------|
| DST_FORMAT    | PNG     | The image format that uploaded files should be converted into                                                                      |
| SRC_DIR       | /input  | The directory within the bucket where files will be uploaded to <br/>(Must match the Prefix configured in the Lambda's S3 trigger) |
| DST_DIR       | /output | The directory within the bucket where converted files will be placed                                                               |
| DELETE_SRC    | true    | Whether the original file that was converted should be deleted                                                                     |
| DELETE_FAILED | true    | Whether files that failed to convert should be deleted                                                                             |

-----

# Installation #
## Building
To build this project into a jar, simply run
```shell
mvn clean package
```
> Note: You need to have access to JDeli, see our [support page](https://support.idrsolutions.com/jdeli/tutorials/add-jdeli-as-a-maven-dependency)
> for more info.

## Deploying
### Manually
To manually deploy, navigate to your AWS Lambda function `Code -> Code source`, select
the "Upload from" drop down and click `.zip or .jar file`, then Upload your built jar file
(target/jdeli-lambda-1.0-SNAPSHOT.jar).

### AWS CLI
To deploy this project to your Lambda function, first set up the [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html),
then, in your project root, run:
```shell
aws lambda update-function-code --function-name {your-function-name} --zip-file fileb://target/jdeli-lambda-1.0-SNAPSHOT.jar
```
Where `{your-function-name}` is replaced with the name of your lambda function.

## Finishing up
Before the function can execute, we need to tell Lambda where to find the Handler in the Jar.

On your Lambda function, goto `Code -> Runtime settings` and click Edit, then set the handler to
```text
com.idrsolutions.Handler::handleRequest
```

-----

# Who do I talk to? #

Found a bug, or have a suggestion / improvement? Let us know through the Issues page.

Got questions? You can contact us [here](https://idrsolutions.atlassian.net/servicedesk/customer/portal/8).

-----

Copyright 2023 IDRsolutions

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
