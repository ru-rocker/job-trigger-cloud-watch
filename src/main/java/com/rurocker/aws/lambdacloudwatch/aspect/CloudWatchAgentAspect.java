package com.rurocker.aws.lambdacloudwatch.aspect;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ru-rocker
 * Created on 07-Apr-2021 8:27 PM
 */
@Aspect
@Component
public class CloudWatchAgentAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private AmazonCloudWatch amazonCloudWatch;

    @Autowired
    public void setAmazonCloudWatch(AmazonCloudWatch amazonCloudWatch) {
        this.amazonCloudWatch = amazonCloudWatch;
    }

    @Around("execution(* com.rurocker.aws.lambdacloudwatch.processor..*(..)))")
    public Object sendMetrics(ProceedingJoinPoint proceedingJoinPoint) {

        Object result = null;
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();

        Dimension dimension = new Dimension()
                .withName("JOB_LATENCY")
                .withValue(className);

        double status = 1d;
        long start = System.currentTimeMillis();
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable t) {
            logger.error("Error!", t);
            status = 0d;
        }
        long end = System.currentTimeMillis();
        double value = (double) (end - start);

        MetricDatum latencyDatum = new MetricDatum()
                .withMetricName("JOB_LATENCY")
                .withUnit(StandardUnit.Milliseconds)
                .withValue(value)
                .withDimensions(dimension);

        MetricDatum statusDatum = new MetricDatum()
                .withMetricName("JOB_STATUS")
                .withUnit(StandardUnit.None)
                .withValue(status)
                .withDimensions(dimension);

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("job-scheduler-poc")
                .withMetricData(latencyDatum,statusDatum);

        amazonCloudWatch.putMetricData(request);
        return result;
    }
}
