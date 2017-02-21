package org.noear.sited;

/**
 * Created by Seiko on 2016/12/17. Y
 */

public class SdExt {
    private SdSource source;

    public SdExt(SdSource s) {this.source = s;}

    public void set(final String key, final String val) {
        Util.set(source, key, val);
    }

    public String get(final String key) {
        return Util.get(source, key);
    }
}
