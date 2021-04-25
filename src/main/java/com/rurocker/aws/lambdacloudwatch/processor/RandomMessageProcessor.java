package com.rurocker.aws.lambdacloudwatch.processor;

import com.rurocker.aws.lambdacloudwatch.dto.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ru-rocker
 * Created on 07-Apr-2021 2:28 PM
 */
@Component
public class RandomMessageProcessor implements MessageProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ReentrantLock reentrantLock;

    @Autowired
    public void setReentrantLock(ReentrantLock reentrantLock) {
        this.reentrantLock = reentrantLock;
    }

    @Async("asyncExecutor")
    public void process(RequestDto requestDto) {

        if(reentrantLock.isLocked()) {
            logger.info("Another thread is processing the processor!");
            return;
        }
        reentrantLock.lock();
        Random rand = new Random();
        try {
            Thread.sleep(rand.nextInt(2000));
            logger.info("Message is: {}", requestDto);
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
        } finally {
            reentrantLock.unlock();
        }
    }
}
