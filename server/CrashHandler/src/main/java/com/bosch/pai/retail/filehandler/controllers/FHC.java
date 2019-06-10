package com.bosch.pai.retail.filehandler.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller("FHC")
@Component("FHC")
public class FHC {

    private final Logger logger = LoggerFactory
            .getLogger(FHC.class);

    @Value("${crash.upload.path}")
    private static String crash_upload_path;

    @RequestMapping(value = "/uploadCrashReports/", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<StatusMessage> ucr(
            @RequestParam("file") MultipartFile file, @RequestParam("year") int year, @RequestParam("month") int month, @RequestParam("date") int date) {
        logger.debug("Controller received request to upload crash report files : year : {}, month: {}, date: {}, fileName: {} "
                , year, month, date, file.getName());
        StatusMessage statusMessage;

        final File uploadDir = new File(crash_upload_path + File.separator + year + File.separator + month + File.separator + date);
        if (!uploadDir.exists()) {
            final boolean created = uploadDir.mkdirs();
            logger.debug("Controller created directories. {}", created);
            if (!created) {
                logger.debug("Controller failed to  create upload directories. ");
                statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_UPLOAD_CRASH_REPORTS, "Failed to  create upload directories. ");
                return new ResponseEntity<>(statusMessage,
                        HttpStatus.ACCEPTED);
            }
        }
        final File fileToUpload = new File(uploadDir.getPath() + File.separator + file.getOriginalFilename());
        try {
            boolean isCreated = fileToUpload.createNewFile();
            logger.debug("created file. {}", isCreated);
            file.transferTo(fileToUpload);
        } catch (IOException e) {
            logger.debug("Controller failed to  uploaded files. : {} "
                    , file.getName());
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILED_TO_UPLOAD_CRASH_REPORTS, "Failed to upload crash reports. " + file.getName());
            return new ResponseEntity<>(statusMessage,
                    HttpStatus.ACCEPTED);
        }
        logger.debug("Controller successfully uploaded files.");
        statusMessage = new StatusMessage(StatusMessage.STATUS.SUCCESS, "Successfully uploaded files. ");

        return new ResponseEntity<>(statusMessage,
                HttpStatus.ACCEPTED);
    }
}
