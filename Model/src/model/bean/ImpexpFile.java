package model.bean;

import java.io.File;

public class ImpexpFile {
    
    String viewName;
    String status;
    String name;
    File txtFile;
    
    public ImpexpFile() {
        super();
    }
    
    public ImpexpFile(File _file) {
        txtFile = _file;
        viewName = getName();
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return txtFile == null ? null : txtFile.getName();
    }

    public void setTxtFile(File txtFile) {
        this.txtFile = txtFile;
    }

    public File getTxtFile() {
        return txtFile;
    }
}
