package com.bosch.pai.retail.session.controllers;

import com.bosch.pai.retail.common.responses.StatusMessage;
import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.db.model.SessionDetail;
import com.bosch.pai.retail.db.model.SubSessionDetail;
import com.bosch.pai.retail.session.service.RetailSessionService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class UserSessionController {
    private static String[] exLoc = new String[]{"CHECKOUT", "EXIT", "BILLING"};
    private final Logger logger = LoggerFactory.getLogger(UserSessionController.class);

    @Autowired
    private RetailSessionService rss;


    /*
    *   Collections not created so commenting.
    * */
  /*  @RequestMapping(value = "/companies/{companyId}/stores/{storeId}/sessions/saveOrUpdate/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> sUpSS(
            @PathVariable(name = "companyId") String ci,
            @PathVariable(name = "storeId") String sti,
            @RequestBody SaveOrUpdateSessionRequest saveOrUpdateSessionRequest,
            @RequestHeader(value = "device_type", defaultValue = "device_type_android") String device_type) {
        logger.debug("Controller received SaveOrUpdateSessionRequest : {} "
                , saveOrUpdateSessionRequest);

        final MultiValueMap<String, String> headers = new HttpHeaders();
        String statusMessage;
        final String u1 = saveOrUpdateSessionRequest.getUserId();
        final String sId = saveOrUpdateSessionRequest.getSessionId();
        final String S1 = saveOrUpdateSessionRequest.getSiteName();
        final String l1 = saveOrUpdateSessionRequest.getLocationName();
        final String t1 = saveOrUpdateSessionRequest.getTimeZoneID();

        if (S1 != null && !S1.isEmpty() && l1 != null && !l1.isEmpty())

        {
            final boolean isEX2 = val1ExlInL(l1);
            final String s23 = rss.checkAndUpdateSessionDetails(ci, sti, u1, sId, S1, l1, t1, isEX2, device_type);
            headers.add("session-id", s23);
            statusMessage = "session updated successfully";
        } else

        {
            statusMessage = "session not updated";
        }

        final ResponseEntity<String> responseEntity = new ResponseEntity<>(statusMessage, headers,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for SaveOrUpdateSession : {} "
                , responseEntity);
        return responseEntity;
    }*/

    @RequestMapping(value = "/companies/{companyId}/sessions/saveCompleteSession/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> sSS(
            @PathVariable(name = "companyId") String ci,
            @RequestBody String sessionRequest,
            @RequestHeader(value = "platform", defaultValue = "android") String platform) {

            logger.debug("Controller received SaveOrUpdateSessionRequest for USER {} : {} "
                    , ContextHolder.getContext().getUserId(), sessionRequest);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        StatusMessage statusMessage = null;
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Type type = new TypeToken<Map<SessionDetail, List<SubSessionDetail>>>() {
        }.getType();
        final Map<SessionDetail, List<SubSessionDetail>> listMap = gson.fromJson(sessionRequest, type);
        logger.debug("Session Request for USER {} : {}",ContextHolder.getContext().getUserId(), listMap);
        try {
            if (sessionRequest != null && !sessionRequest.isEmpty()) {
                statusMessage = rss.startSessionSubSession(ci, listMap,platform);
            } else {
                statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, "Session not created");
            }
        } catch (Exception e) {
            logger.error("exception in saveCompleteSession for USER {}",ContextHolder.getContext().getUserId());
            statusMessage = new StatusMessage(StatusMessage.STATUS.FAILURE, "Some error occurred");
        }
        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(statusMessage, headers,
                HttpStatus.OK);
        logger.debug("Controller response for SaveOrUpdateSession for USER {} : {} "
                ,ContextHolder.getContext().getUserId(), responseEntity);
        return responseEntity;
    }


    private boolean val1ExlInL(String l) {
        return Arrays.asList(exLoc).contains(l.toUpperCase());
    }

    /*
    * saveOrUpdate api is not working so commented
    * */
/*    @RequestMapping(value = "/companies/{companyId}/stores/{storeId}/sessions/endSession/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<StatusMessage> eS(
            @PathVariable(name = "companyId") String ci,
            @PathVariable(name = "storeId") String sti,
            @RequestBody EndSessionRequest esr,
            @RequestHeader(value = "device_type", defaultValue = "device_type_android") String device_type)

    {
        logger.debug("Controller received endSession request : {} "
                , esr);
        final StatusMessage statusMessage = rss.endUserSession(ci, sti, esr.getUserId(), esr.getSessionId(), device_type);
        final ResponseEntity<StatusMessage> responseEntity = new ResponseEntity<>(statusMessage,
                HttpStatus.ACCEPTED);
        logger.debug("Controller response for endSession request : {} "
                , statusMessage);
        return responseEntity;
    }*/

}
