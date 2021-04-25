package com.rurocker.aws.lambdacloudwatch.parameter;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Get value from parameter store.
 * @author ru-rocker
 * Created on 18-Apr-2021 11:09 AM
 */
@Component
public class ParameterRetrieval {

    private AWSSimpleSystemsManagement awsSimpleSystemsManagement;

    @Value("${aws.config.parameter-store-key}")
    private String paremeterStoreKey;

    @Autowired
    public void setAwsSimpleSystemsManagement(AWSSimpleSystemsManagement awsSimpleSystemsManagement) {
        this.awsSimpleSystemsManagement = awsSimpleSystemsManagement;
    }

    public char[] getApiKey() {

        GetParameterRequest parametersRequest =
                new GetParameterRequest()
                        .withName(paremeterStoreKey)
                        .withWithDecryption(false);

        final GetParameterResult parameterResult =
                awsSimpleSystemsManagement.getParameter(parametersRequest);

        return parameterResult.getParameter().getValue().toCharArray();
    }
}
