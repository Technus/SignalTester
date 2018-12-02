package com.github.technus.dbAdditions.mongoDB.pojo;

import com.sun.security.auth.module.NTSystem;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemUser {
    public final String domainName,userName,systemName;

    public SystemUser(){
        domainName=getCurrentDomainName();
        userName=getCurrentUserName();
        systemName=getCurrentSystemName();
    }

    @BsonCreator
    public SystemUser(
            @BsonProperty("domainName") String domainName,
            @BsonProperty("userName") String userName,
            @BsonProperty("systemName") String systemName) {
        this.domainName = domainName;
        this.userName = userName;
        this.systemName = systemName;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getUserName() {
        return userName;
    }

    public String getSystemName() {
        return systemName;
    }

    private static final NTSystem ntSystem=new NTSystem();
    private static String getCurrentDomainName(){
        return ntSystem.getDomain();
    }
    private static String getCurrentUserName(){
        return ntSystem.getName();
    }
    private static String getCurrentSystemName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
