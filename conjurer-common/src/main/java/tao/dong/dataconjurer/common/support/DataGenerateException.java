package tao.dong.dataconjurer.common.support;

import lombok.Getter;

@Getter
public class DataGenerateException extends RuntimeException {
    private final DataGenerationErrorType errorType;

    public DataGenerateException(DataGenerationErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public DataGenerateException(DataGenerationErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
}
