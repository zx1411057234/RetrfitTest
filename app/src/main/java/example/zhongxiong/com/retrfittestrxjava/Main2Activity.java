package example.zhongxiong.com.retrfittestrxjava;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class Main2Activity extends AppCompatActivity {
    private MyRetrofitService mRetrofitService;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://waishuo.leanapp.cn/api/v1.0/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mRetrofitService = mRetrofit.create(MyRetrofitService.class);
        setBitmap("5d2f22a9a91c9300694722dd");
    }

    private void setBitmap(String userId) {
        DisposableObserver disposableObserver = new DisposableObserver<ResponseBody>() {

            @Override
            public void onNext(@NonNull ResponseBody responseBody) {

                String s = null;
                try {
                    s = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("zx", "onNext: " + s);
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("zx", "onError: "+e.toString() );
            }
        };

        mCompositeDisposable.add(disposableObserver);

        mRetrofitService
                .getImagesByName(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver);
    }

    private interface MyRetrofitService {
        @GET("users/{userId}")
        Observable<ResponseBody> getImagesByName(@Path("userId") String userId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
