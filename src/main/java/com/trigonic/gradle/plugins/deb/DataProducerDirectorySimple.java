package com.trigonic.gradle.plugins.deb;

import groovy.transform.Canonical;
import java.io.IOException;
import org.vafer.jdeb.DataConsumer;
import org.vafer.jdeb.DataProducer;

//@Canonical
public class DataProducerDirectorySimple implements DataProducer {

    protected String dirname;
    protected String user;
    protected int uid = 0;
    protected String group;
    protected int gid = 0;
    protected int mode;
    
    @Override
    public void produce(DataConsumer receiver) throws IOException {
        // TODO Investigate what happens if we don't have a uid/gid
        receiver.onEachDir(dirname, null, user, uid, group, gid, mode, 0);
    }
}
