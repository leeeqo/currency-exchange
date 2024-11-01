package com.oli;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

public class Application {

    public static void main(String[] args) throws LifecycleException {
        String contextPath = "/";
        String webappDirLocation = "src/main/java/";
        String baseDirectory = new File(webappDirLocation).getAbsolutePath();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        StandardContext context = (StandardContext) tomcat.addWebapp(contextPath, baseDirectory);

        String buildPath = "target/classes";
        String webAppMount = "/WEB-INF/classes";

        File additionalWebInfClasses = new File(buildPath);
        WebResourceRoot resources = new StandardRoot(context);
        resources.addPreResources(new DirResourceSet(resources, webAppMount, additionalWebInfClasses.getAbsolutePath(), contextPath));
        context.setResources(resources);

        tomcat.start();
        tomcat.getServer().await();
    }
}
