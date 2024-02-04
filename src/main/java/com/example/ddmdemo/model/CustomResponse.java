package com.example.ddmdemo.model;

import com.example.ddmdemo.indexmodel.DummyIndex;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
public class CustomResponse {

    private DummyIndex content;

    public CustomResponse(DummyIndex content, List<String> highlights) {
        this.content = content;
        this.content.setHighlight(highlights.get(0));
    }
}
