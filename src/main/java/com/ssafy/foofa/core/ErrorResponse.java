package com.ssafy.foofa.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Schema(description = "API 에러 응답")
public record ErrorResponse(
        @Schema(description = "에러 메시지", example = "회원을 찾을 수 없습니다.")
        String message,

        @JsonInclude(Include.NON_NULL)
        @Schema(description = "필드 오류 목록 (있을 경우)", example = "[{\"field\": \"todoName\", \"message\": \"이 필드는 필수입니다.\"}]")
        List<FieldError> fieldErrors,

        @JsonInclude(Include.NON_NULL)
        @Schema(description = "제약 조건 위반 오류 목록 (있을 경우)", example = "[{\"path\": \"dueDate\", \"message\": \"날짜는 반드시 현재보다 미래여야 합니다.\"}]")
        List<ConstraintViolationError> violationErrors
) {

    public ErrorResponse(String message) {
        this(message, null, null);
    }

    public ErrorResponse(String message, BindingResult bindingResult) {
        this(message, FieldError.from(bindingResult), null);
    }

    public ErrorResponse(String message, Set<ConstraintViolation<?>> constraintViolations) {
        this(message, null, ConstraintViolationError.from(constraintViolations));
    }

    private record FieldError(String field, Object rejectedValue, String reason) {

        private static List<FieldError> from(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue(),
                            error.getDefaultMessage())
                    )
                    .toList();
        }
    }

    private record ConstraintViolationError(String field, Object rejectedValue, String reason) {

        private static final int FIELD_POSITION = 1;

        private static List<ConstraintViolationError> from(Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                    .map(constraintViolation -> new ConstraintViolationError(
                            getField(constraintViolation.getPropertyPath()),
                            constraintViolation.getInvalidValue().toString(),
                            constraintViolation.getMessage()))
                    .toList();
        }

        private static String getField(Path propertyPath) {
            return propertyPath.toString().split("\\.")[FIELD_POSITION];
        }
    }
}
