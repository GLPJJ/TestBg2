package simple.view.tablayout.listener;

public interface OnTabSelectListener {
    /**
     * 第一次点击
     * @param position
     */
    void onTabSelect(int position);

    /**
     * 第二次点击
     * @param position
     */
    void onTabReselect(int position);
}