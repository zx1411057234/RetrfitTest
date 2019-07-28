package example.zhongxiong.com.retrfittestrxjava;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends ListActivity {
    private MyRetrofitService mRetrofitService;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //再次提交测试
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mRetrofitService = mRetrofit.create(MyRetrofitService.class);

        ArrayAdapter mArrayAdapter = new ArrayAdapter(this, 0) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = new ImageView(getContext());
                }

                setBitmap("bd_logo1.png", (ImageView) convertView);

                return convertView;
            }

            @Override
            public int getCount() {
                return 20;
            }
        };

        setListAdapter(mArrayAdapter);
    }

    private void setBitmap(String picName, final ImageView imageView) {
        DisposableObserver disposableObserver = new DisposableObserver<ResponseBody>() {

            @Override
            public void onNext(@NonNull ResponseBody responseBody) {
                try {
                    byte[] bytes = responseBody.bytes();
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }
        };

        mCompositeDisposable.add(disposableObserver);

        mRetrofitService
                .getImagesByName(picName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver);
    }

    private interface MyRetrofitService {
        @GET("img/{picName}")
        Observable<ResponseBody> getImagesByName(@Path("picName") String picName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}