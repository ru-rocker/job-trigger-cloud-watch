package com.rurocker.aws.lambdacloudwatch.controller;

import com.amazonaws.services.simplesystemsmanagement.model.ParameterNotFoundException;
import com.rurocker.aws.lambdacloudwatch.dto.RequestDto;
import com.rurocker.aws.lambdacloudwatch.parameter.ParameterRetrieval;
import com.rurocker.aws.lambdacloudwatch.processor.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author ru-rocker
 * Created on 10-Apr-2021 8:43 PM
 */
@RestController
public class WebhookController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private MessageProcessor messageProcessor;
    private ParameterRetrieval parameterRetrieval;

    @Autowired
    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Autowired
    public void setParameterRetrieval(ParameterRetrieval parameterRetrieval) {
        this.parameterRetrieval = parameterRetrieval;
    }

    @PostMapping("/aws-poc-notify")
    public void webhook(@RequestBody RequestDto requestDto,
                        @RequestHeader(name = "API_KEY", required = false) String clientApiKey) {
        logger.info("Message ID: {}", requestDto.getId());
        if(!isRequestValid(clientApiKey)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "API_KEY is not valid");
        }
        messageProcessor.process(requestDto);
    }

    private boolean isRequestValid(String clientApikey) {
        try {
            final char[] ssmApiKey = parameterRetrieval.getApiKey();
            return StringUtils.hasLength(clientApikey) &&
                    clientApikey.equals(String.valueOf(ssmApiKey));
        } catch (ParameterNotFoundException e) {
            logger.warn("API_KEY not found", e);
            return false;
        }
    }
}
