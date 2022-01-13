package com.ayst.factorytest.model;

public class ResultEvent {
    private TestItem mItem;

    public ResultEvent(TestItem item) {
        this.mItem = item;
    }

    public TestItem getTestItem() {
        return mItem;
    }
}
