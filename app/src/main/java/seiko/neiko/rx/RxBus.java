package seiko.neiko.rx;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * RxBus
 * Created by YoKeyword on 2015/6/17.
 */

public class RxBus {

    private static RxBus instance;

    private final Subject<Object> bus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public RxBus() {bus = PublishSubject.create().toSerialized();}

    // 单例RxBus
    public static RxBus getDefault() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    // 发送一个新的事件
    public void post(RxEvent event) {bus.onNext(event);}

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public Flowable<RxEvent> toObservable(final int type) {
        return bus.ofType(RxEvent.class)
                .filter(new Predicate<RxEvent>() {
                    @Override
                    public boolean test(RxEvent event) throws Exception {
                        return event.getType() == type;
                    }
                })
                .toFlowable(BackpressureStrategy.BUFFER)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
