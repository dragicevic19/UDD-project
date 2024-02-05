package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.exceptionhandling.exception.LoadingException;
import com.example.ddmdemo.exceptionhandling.exception.StorageException;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.indexrepository.DummyIndexRepository;
import com.example.ddmdemo.model.DummyTable;
import com.example.ddmdemo.respository.DummyRepository;
import com.example.ddmdemo.service.interfaces.FileService;
import com.example.ddmdemo.service.interfaces.IndexingService;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.language.detect.LanguageDetector;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {

    private final DummyIndexRepository dummyIndexRepository;
    private final DummyRepository dummyRepository;
    private final FileService fileService;
    private final LanguageDetector languageDetector;
    private final GeoService geoService;

    @Override
    @Transactional
    public String indexDocument(MultipartFile documentFile) {
        var newEntity = new DummyTable();
        var newIndex = new DummyIndex();

        var title = Objects.requireNonNull(documentFile.getOriginalFilename()).split("\\.")[0];
        newIndex.setTitle(title);
        newEntity.setTitle(title);

        var documentContent = extractDocumentContent(documentFile);
        parseDocumentContent(documentContent, newIndex);

        if (detectLanguage(documentContent).equals("SR")) {
            newIndex.setContentSr(documentContent);
        } else {
            newIndex.setContentEn(documentContent);
        }

        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());
        newIndex.setServerFilename(serverFilename);
        newEntity.setServerFilename(serverFilename);

        newEntity.setMimeType(detectMimeType(documentFile));
        var savedEntity = dummyRepository.save(newEntity);

        newIndex.setDatabaseId(savedEntity.getId());
        dummyIndexRepository.save(newIndex);

        writeLogs(newIndex);
        return serverFilename;
    }

    private void writeLogs(DummyIndex newIndex) {
        log.info("STATISTIC-LOG indexDocument -> employee : " + newIndex.getName().concat(" ").concat(newIndex.getSurname()));
        log.info("STATISTIC-LOG indexDocument -> gov : " + newIndex.getGovernment());
        try {
            log.info("STATISTIC-LOG indexDocument -> city :" + newIndex.getGovAddress().split(",")[2]);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void parseDocumentContent(String documentContent, DummyIndex newIndex) {
        Pattern pattern = Pattern.compile("Uprava za (.*?),");
        Matcher matcher = pattern.matcher(documentContent);
        if (matcher.find()) newIndex.setGovernment(matcher.group(1));

        pattern = Pattern.compile("nivo uprave: (.*?),");
        matcher = pattern.matcher(documentContent);
        if (matcher.find()) newIndex.setGovLevel(matcher.group(1));

        pattern = Pattern.compile("nivo uprave: (.*?), (.*?, \\d+, .+?) u");
        matcher = pattern.matcher(documentContent);
        if (matcher.find()) {
            newIndex.setGovAddress(matcher.group(2));
            var coords = geoService.extractCoordinates(newIndex.getGovAddress());
            newIndex.setGeoLocation(new GeoPoint(coords.get(0), coords.get(1)));
        }

        pattern = Pattern.compile("\n(.*?) \r\nPotpisnik ugovora za klijenta");
        matcher = pattern.matcher(documentContent);
        if (matcher.find()) {
            newIndex.setName(matcher.group(1).split(" ")[0]);
            newIndex.setSurname(matcher.group(1).split(" ")[1]);
        }
    }


    private String extractDocumentContent(MultipartFile multipartPdfFile) {
        String documentContent;
        try (var pdfFile = multipartPdfFile.getInputStream()) {
            var pdDocument = PDDocument.load(pdfFile);
            var textStripper = new PDFTextStripper();
            documentContent = textStripper.getText(pdDocument);
            pdDocument.close();
        } catch (IOException e) {
            throw new LoadingException("Error while trying to load PDF file content.");
        }

        return documentContent;
    }

    private String detectLanguage(String text) {
        var detectedLanguage = languageDetector.detect(text).getLanguage().toUpperCase();
        if (detectedLanguage.equals("HR")) {
            detectedLanguage = "SR";
        }

        return detectedLanguage;
    }

    private String detectMimeType(MultipartFile file) {
        var contentAnalyzer = new Tika();

        String trueMimeType;
        String specifiedMimeType;
        try {
            trueMimeType = contentAnalyzer.detect(file.getBytes());
            specifiedMimeType =
                    Files.probeContentType(Path.of(Objects.requireNonNull(file.getOriginalFilename())));
        } catch (IOException e) {
            throw new StorageException("Failed to detect mime type for file.");
        }

        if (!trueMimeType.equals(specifiedMimeType) &&
                !(trueMimeType.contains("zip") && specifiedMimeType.contains("zip"))) {
            throw new StorageException("True mime type is different from specified one, aborting.");
        }

        return trueMimeType;
    }
}