package cz.zcu.kgm.analyst.rest.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestBean {

    public Date ID;
    public String message;
    
    public TestBean(){
    }
    
    public TestBean(String message) {
        super();
        this.ID = new Date();
        this.message = message;
    }
}