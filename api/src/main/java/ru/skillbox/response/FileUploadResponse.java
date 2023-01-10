package ru.skillbox.response;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.dto.FileUploadDto;

@Getter
@Setter
public class FileUploadResponse {

    private String error;
    private Long timestamp;
    private FileUploadDto data;
    private String errorDescription;

}
