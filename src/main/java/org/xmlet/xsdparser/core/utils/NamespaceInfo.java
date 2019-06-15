package org.xmlet.xsdparser.core.utils;

public class NamespaceInfo {

    private String name;
    private String file;

    public NamespaceInfo(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
