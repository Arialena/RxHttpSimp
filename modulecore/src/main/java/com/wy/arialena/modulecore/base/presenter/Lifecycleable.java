package com.wy.arialena.modulecore.base.presenter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import io.reactivex.subjects.Subject;

/**
 * @author wuyan
 * ================================================
 * 让 { Activity}/{@link Fragment} 实现此接口,即可正常使用 { RxLifecycle}
 * 无需再继承 { RxLifecycle} 提供的 Activity/Fragment ,扩展性极强
 *
 * Created by JessYan on 25/08/2017 18:39
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface Lifecycleable<E> {
    @NonNull
    Subject<E> provideLifecycleSubject();
}
