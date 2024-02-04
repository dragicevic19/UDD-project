package com.example.ddmdemo.indexmodel;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "dummy_index")
@Setting(settingPath = "/configuration/serbian-analyzer-config.json")
public class DummyIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text, store = true, name = "title")
    private String title;

    @Field(type = FieldType.Text, store = true, name = "content_sr", analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    @HighlightField
    private String contentSr;

    @Field(type = FieldType.Text, store = true, name = "content_en", analyzer = "english", searchAnalyzer = "english")
    private String contentEn;

    @Field(type = FieldType.Text, store = true, name = "server_filename", index = false)
    private String serverFilename;

    @Field(type = FieldType.Integer, store = true, name = "database_id")
    private Integer databaseId;

    @Field(type = FieldType.Text, store = true, name = "name", analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String name;

    @Field(type = FieldType.Text, store = true, name = "surname", analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String surname;

    @Field(type = FieldType.Text, store = true, name = "government", analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String government;

    @Field(type = FieldType.Text, store = true, name = "gov_address", analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String govAddress;

    @Field(type = FieldType.Text, store = true, name = "gov_level", analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String govLevel;

    private String highlight;

}
