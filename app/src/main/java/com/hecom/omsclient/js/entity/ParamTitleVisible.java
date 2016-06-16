package com.hecom.omsclient.js.entity;

/**
 * @author tianlupan 2015/11/9
 */
public class ParamTitleVisible extends ParamBase {
    private boolean visible;

    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
