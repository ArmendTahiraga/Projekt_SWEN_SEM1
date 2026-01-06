package at.technikum.server;

public enum Method {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String methodType;

    Method(String methodType) {
        this.methodType = methodType;
    }

    public String getMethodType() {
        return methodType;
    }
}
