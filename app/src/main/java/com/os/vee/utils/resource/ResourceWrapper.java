package com.os.vee.utils.resource;

/**
 * Created by Omar on 14-Jul-18 12:11 AM.
 */
public class ResourceWrapper<DATA> {

    public static class Loading<DATA> extends ResourceWrapper<DATA> {}

    public static class Success<DATA> extends ResourceWrapper<DATA> {
        public DATA data;

        public Success(DATA data) {
            this.data = data;
        }

        public DATA getData() {
            return data;
        }
    }

    public static class Error<DATA> extends ResourceWrapper<DATA> {
        public int errorCode = -1;
        public String errorMessage = "";
        public Throwable throwable = null;

        public Error(int errorCode, String errorMessage, Throwable throwable) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.throwable = throwable;
        }

        public Error(Throwable throwable) {
            this.throwable = throwable;
        }
    }

}
