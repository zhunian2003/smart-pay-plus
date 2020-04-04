package com.pku.smart.modules.sys.vopackage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SysDeptVo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String text;
    private String icon;
    private Map<String, Boolean> state;

    private List<SysDeptVo> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<SysDeptVo> getChildren() {
        return children;
    }

    public void setChildren(List<SysDeptVo> children) {
        this.children = children;
    }

    public Map<String, Boolean> getState() {
        return state;
    }

    public void setState(Map<String, Boolean> state) {
        this.state = state;
    }
}
