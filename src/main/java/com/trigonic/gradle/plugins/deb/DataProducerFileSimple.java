package com.trigonic.gradle.plugins.deb;

import groovy.transform.Canonical;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.vafer.jdeb.DataConsumer;
import org.vafer.jdeb.DataProducer;

//@Canonical
public class DataProducerFileSimple implements DataProducer {

    protected String filename;
    protected File file;
    protected String user;
    protected int uid;
    protected String group;
    protected int gid;
    protected int mode;

    @Override
    public void produce(DataConsumer receiver) throws IOException {
        if(file!=null && file.exists()){
            receiver.onEachFile( new FileInputStream(file), filename, null, user, uid, group, gid, mode, file.length());
        }
    }
}
