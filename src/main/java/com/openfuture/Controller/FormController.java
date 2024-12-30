//form controller
package com.openfuture.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openfuture.Entity.Form;
import com.openfuture.Service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin("*")
public class FormController {

    private static final Logger logger= LoggerFactory.getLogger(FormController.class);

    @Autowired
    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping("/form/saveForm")
    public ResponseEntity<String> saveUser(@RequestPart("formData") String formData,
                                           @RequestPart(value = "cv", required = false) MultipartFile multipartFile) throws JsonProcessingException {
        logger.info("Received request to save user");
        ObjectMapper objectMapper = new ObjectMapper();
        Form user = objectMapper.readValue(formData, Form.class);

        // Handle cv (Only PDF, DOC, DOCX files are allowed)
        if (!multipartFile.isEmpty() && multipartFile != null) {
            String fileType = multipartFile.getContentType();

            // Check if the file is a PDF or Word document
            if ("application/pdf".equals(fileType) || "application/msword".equals(fileType) || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(fileType)) {
                try {
                    user.setCv(multipartFile.getBytes());
                    formService.saveUser(user);  // Save the user after processing the CV
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Only PDF and Word documents are allowed (not ZIP, images, or other formats).");
            }
        } else {
            user.setCv(null);  // No file uploaded, set cv as null
        }

        return ResponseEntity.status(HttpStatus.OK).body("User successfully saved");
    }

}
