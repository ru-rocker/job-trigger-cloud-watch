package com.rurocker.aws.lambdacloudwatch.processor;


import com.rurocker.aws.lambdacloudwatch.dto.RequestDto;

/**
 * @author ru-rocker
 * Created on 07-Apr-2021 2:24 PM
 */
public interface MessageProcessor {

    void process(RequestDto requestDto);
}
