package seiko.neiko.rx;

/**
 * Created by Seiko on 2016/12/27. Y
 */

public class RxEvent {

    public static final int EVENT_MAIN_SITED = 11;
    public static final int EVENT_MAIN_LIKE = 12;
    public static final int EVENT_MAIN_HIST = 13;

    public static final int EVENT_COPY_SITED = 991;
    public static final int EVENT_COPY_LIKE  = 992;

    public static final int EVENT_DOWN1_PROCESS = 41;
    public static final int EVENT_DOWN2_STATUS  = 42;

    public static final int EVENT_SECTION1_SAVE = 31;


    private int type;
    private Object[] data;

    public RxEvent(int type, Object... data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {return type;}

    public Object getData() {return getData(0);}

    public Object getData(int index) {return index < data.length ? data[index] : null;}
}
