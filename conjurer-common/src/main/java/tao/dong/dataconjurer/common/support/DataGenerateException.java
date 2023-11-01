package tao.dong.dataconjurer.common.support;

import lombok.Getter;

@Getter
public class DataGenerateException extends RuntimeException {
    private final DataGenerationErrorType errorType;
    private final String id;

    public DataGenerateException(DataGenerationErrorType errorType, String message, String id) {
        super(message);
        this.errorType = errorType;
        this.id = id;

    }

    public DataGenerateException(DataGenerationErrorType errorType, String message) {
        this(errorType, message, (String)null);
    }

    public DataGenerateException(DataGenerationErrorType errorType, String message, Throwable cause) {
        this(errorType, message, null, cause);
    }

    public DataGenerateException(DataGenerationErrorType errorType, String message, String id, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.id = id;
    }
}
