package ru.skillbox.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import ru.skillbox.response.PostCommentDto;

import java.util.List;

@Setter
@Getter
public class CommentResponse {
    private long totalElements;
    private long totalPages;
    private long number;
    private long size;
    private List<PostCommentDto> content;

    private Sort sort;
    boolean first;
    boolean last;
    Integer numberOfElements;
    org.springframework.data.domain.Pageable pageable;
    boolean empty;
}