package comviktorgozhiy.github.ordertohome.Presenters;

public abstract class BasePresenter<T> {

    public T view;

    public void attachView(T callback) {
        this.view = callback;
    }

    public void deattachView() {
        this.view = null;
    }

}
