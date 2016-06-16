package com.hecom.omsclient.js.entity;

/**
 * Created by zhanggaokai on 16/6/16.
 */
public class ChosenEntity extends ParamBase {


    private Item[] source;

    public Item[] getSource() {
        return source;
    }

    public void setSource(Item[] source) {
        this.source = source;
    }

    public String[] getKeys() {
        String[] keys = new String[source.length];
        for (int i = 0; i < source.length; i++) {
            keys[i] = source[i].key;
        }
        return keys;
    }

    @Override
    public boolean isValid() {

        return source.length > 0;
    }

    public static class Item {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private String value;
    }

}
