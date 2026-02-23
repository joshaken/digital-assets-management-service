package co.assets.manage.config.exception;

import co.assets.manage.config.constants.HttpConstants;
import co.assets.manage.dto.Result;
import co.assets.manage.enums.api.APIEnum;
import jakarta.validation.UnexpectedTypeException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * カスタムのグローバル例外処理クラス
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> factoryCustomizer() {
        return factory -> {
            ErrorPage notFound = new ErrorPage(HttpStatus.NOT_FOUND, HttpConstants.Path.NOT_FOUND);
            ErrorPage sysError = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, HttpConstants.Path.INTERNAL_ERROR);
            factory.addErrorPages(notFound, sysError);
        };

    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Result<String> requestNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("REQ_PATH[{}] HttpMessageNotReadableException [{}]", request.getDescription(Boolean.FALSE), ex.getMessage(), ex);
        return Result.error("HttpMessageNotReadableException");

    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public Result<String> requestNotReadable(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        log.error("REQ_PATH[{}] HttpMediaTypeNotSupportedException", request.getDescription(Boolean.FALSE), ex);
        return Result.error("HttpMediaTypeNotSupportedException");
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public Result<Void> sqlIntegrityConstraintError(SQLIntegrityConstraintViolationException ex) {
        log.error("SQLIntegrityConstraintViolationException", ex);
        return Result.getCustomResponse(APIEnum.DUPLICATED_INSERT);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public Result<Void> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("dataIntegrityViolationException", ex);
        return Result.getErrorResponse(APIEnum.PARAM_ERROR.getCode(), "データの保存中にエラーが発生しました。");
    }

    @ExceptionHandler({TypeMismatchException.class})
    public Result<String> requestTypeMismatch(TypeMismatchException ex) {
        log.error("TypeMismatchException", ex);
        return Result.error("TypeMismatchException");
    }


    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    public Result<Void> server500(RuntimeException ex) {
        log.error("500...", ex);
        return Result.getCustomResponse(APIEnum.SERVER_ERROR);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.warn("IllegalArgumentException caught [{}]", e.getMessage(), e);
        return Result.getErrorResponse(APIEnum.PARAM_ERROR.getCode(), e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public Result<Void> exceptionHandle(Exception e) {
        log.error("Exception caught [{}]", e.getMessage(), e);
        return Result.getErrorResponse(e);
    }

    /**
     * メソッド引数のバリデーション
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        try {
            List<ObjectError> errors = e.getBindingResult().getAllErrors();
            if (!CollectionUtils.isEmpty(errors) && errors.get(0) instanceof FieldError fieldError) {
                log.warn(
                        "Parameter validation failed → Field: [{}], Rejected value: [{}], Reason: {}",
                        fieldError.getField(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()
                );
                return Result.getErrorResponse(APIEnum.PARAM_ERROR.getCode(), fieldError.getDefaultMessage());
            }
        } catch (Exception ie) {
            log.error("methodArgumentNotValidExceptionHandle error", e);
        }
        log.error("methodArgumentNotValidExceptionHandle caught");
        return Result.getErrorResponse(e);
    }


    @ExceptionHandler(ValidationException.class)
    public Result<Void> validationExceptionHandle(ValidationException e) {
        log.error("validationExceptionHandle error: {}", e.getMessage(), e);
        return Result.getErrorResponse(APIEnum.PARAM_ERROR.getCode(), e.getCause().getMessage());
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public Result<Void> unexpectedTypeHandle(ValidationException e) {
        log.error("unexpectedTypeHandle error: {}", e.getMessage(), e);
        return Result.getErrorResponse(APIEnum.PARAM_ERROR.getCode(), e.getCause().getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> runtimeExceptionHandle(RuntimeException e) {
        log.error("RuntimeException caught [{}]", e.getMessage(), e);
        return Result.getCustomResponse(APIEnum.FAILED);
    }

    @ExceptionHandler(ForwardServiceException.class)
    public Result<Void> forwardServiceException(ForwardServiceException e) {
        log.error("forwardServiceException error [{}]", e.getMessage(), e);
        return Result.getCustomResponse(e.getMsg());
    }

    @ExceptionHandler(BizException.class)
    public Result<Void> bizExceptionHandle(BizException e) {
        log.error("BizException error: {}", e.getMessage());
        return Result.getCustomResponse(e.getMsg());
    }


}