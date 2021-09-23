package com.cahyaa.chatapp.utils;

public class ResultState<Model> {
//    public class Success {
//        Model data;
//
//        public Success(Model data) {
//            this.data = data;
//        }
//    }
//
//    public class Failure {
//        Exception exception;
//
//        public Failure(Exception exception) {
//            this.exception = exception;
//        }
//    }
//
//    public class Loading {
//    }

    Model data;
    Exception exception;
    Boolean isLoading;

    public ResultState(Model data, Boolean isLoading) {
        this.data = data;
        this.isLoading = isLoading;
    }

    public ResultState(Exception exception, Boolean isLoading) {
        this.exception = exception;
        this.isLoading = isLoading;
    }

    public ResultState(Boolean isLoading) {
        this.isLoading = isLoading;
    }

    public Model getData() {
        return data;
    }

    public void setModel(Model data) {
        this.data = data;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Boolean getLoading() {
        return isLoading;
    }

    public void setLoading(Boolean loading) {
        isLoading = loading;
    }
}
