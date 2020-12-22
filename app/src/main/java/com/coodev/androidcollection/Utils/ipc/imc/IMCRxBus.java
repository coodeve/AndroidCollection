package com.coodev.androidcollection.Utils.ipc.imc;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * 使用RxJava作为组件间通讯工具
 * 可以灵活调整线程 mainThread /  IO
 * 可以灵活处理数据/数据转换/数据拦截等
 * 可以有多个通知方式
 */
public class IMCRxBus {
    private final Subject bus;

    private IMCRxBus() {
        this.bus = PublishSubject.create();
    }

    private static class RxBusHolder {
        private static final IMCRxBus IMC_RX_BUS = new IMCRxBus();
    }

    public static IMCRxBus getInstance() {
        return RxBusHolder.IMC_RX_BUS;
    }

    /**
     * 发送消息
     *
     * @param object
     */
    public void post(Object object) {
        bus.onNext(object);
    }

    /**
     * 接收消息
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    /**
     * 监听测试
     */
    public void test() {
        final Observable<Object> objectObservable = IMCRxBus.getInstance().toObservable(Object.class);
        final Disposable subscribe = objectObservable.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

}
