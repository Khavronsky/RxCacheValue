package ru.sboishtyan.rx_cache_value;


import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import javax.annotation.Nullable;

public final class VoidCacheValue<RESULT> extends InternalCacheValue<Void, RESULT> {

    @NonNull
    private final Consumer<RESULT> cacheOnSuccess;
    @NonNull
    private final Action clearExecuting;
    @Nullable
    private Single<RESULT> cacheValue;
    @Nullable
    private Single<RESULT> executing;

    public VoidCacheValue(@NonNull Fetcher<Void, Single<RESULT>> getValue) {
        this(getValue, null);
    }

    public VoidCacheValue(@NonNull Fetcher<Void, Single<RESULT>> getValue, @Nullable SingleTransformer<?, ?> schedulersStrategy) {
        super(schedulersStrategy, getValue);
        clearExecuting = () -> executing = null;
        cacheOnSuccess = result -> cacheValue = Single.just(result);
    }

    @Override
    @Nullable
    public Single<RESULT> invalidateCache(Void cacheKey) {
        Single<RESULT> removed = this.cacheValue;
        cacheValue = null;
        return removed;
    }

    @Override
    protected Consumer<? super RESULT> getOnSuccessCacheAction(Void cacheKey) {
        return cacheOnSuccess;
    }

    @Override
    protected Single<RESULT> getCacheValue(Void cacheKey) {
        return cacheValue;
    }

    @Nullable
    @Override
    protected Single<RESULT> getExecuting(Void cacheKey) {
        return executing;
    }

    @Override
    protected Action getAfterTerminateCacheAction(Void cacheKey) {
        return clearExecuting;
    }

    @Override
    protected void setExecuting(Void cacheKey, Single<RESULT> executing) {
        this.executing = executing;
    }
}
