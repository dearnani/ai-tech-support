package com.techsupport.rag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetrievedDocument {
    private String id;
    private float score;
    private String content;
}
