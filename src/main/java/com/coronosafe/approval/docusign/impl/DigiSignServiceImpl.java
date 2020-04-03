package com.coronosafe.approval.docusign.impl;

import com.coronosafe.approval.constants.DigiConstants;
import com.coronosafe.approval.docusign.DigiSignService;
import com.coronosafe.approval.jdbc.DigiSanctionsRepository;
import com.coronosafe.approval.dto.DocuSignTokenDto;
import com.coronosafe.approval.dto.DocuSignUserInfoDto;
import com.coronosafe.approval.jdbc.DigiUploadsRepository;
import com.coronosafe.approval.jdbc.data.DigiUploads;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.util.Arrays;

@Service
public class DigiSignServiceImpl implements DigiSignService {
    private static final Logger log = LoggerFactory.getLogger(DigiSignServiceImpl.class);

    @Value("${docusign.auth.url}")
    String docuSignAuthUrl;
    @Value("${docusign.auth.integrationkey}")
    String docuAuthIntegKey;
    @Value("${docusign.secret.key}")
    String docuSecretKey;
    @Value("${docusign.redirect.url}")
    String docuSignRedirectUrl;
    @Value("${docusign.login.hint}")
    String docuSignLoginHint;
    @Value("${docusign.token.url}")
    String docuSignTokenUrl;
    @Value("${docusign.userinfo.url}")
    String docuSignUserUrl;
    @Value("${docusign.redirect.base.url}")
    String docuSignRedirectBaseUrl;
    @Value("${docusign.accountId}")
    String docuSignAccountId;


    @Autowired
    private DigiUploadsRepository digiUploadsRepository;
    @Autowired
    private DigiSanctionsRepository digiSanctionsRepository;

    /**
     * Method to retrieve auth token
     * @param stateString
     * @return
     *
     * TODO - docuSignLoginHint is now using a common account we may have to use accounts for the users who are logging in institution/ state mission director
     */
    @Override
    public String getAuthCode(String stateString){
        StringBuffer authUrl = new StringBuffer();
        authUrl.append(docuSignAuthUrl).append(DigiConstants.REQUEST_PARAM_ATTR)
                .append(DigiConstants.RESPONSE_TYPE).append(DigiConstants.REQUEST_PARAM_ATTR_VALUE).append(DigiConstants.RESPONSE_TYPE_VALUE).append(DigiConstants.REQUEST_PARAM_ATTR_SEPARATOR)
                .append(DigiConstants.SCOPE).append(DigiConstants.REQUEST_PARAM_ATTR_VALUE).append(DigiConstants.SCOPE_VALUE).append(DigiConstants.REQUEST_PARAM_ATTR_SEPARATOR)
                .append(DigiConstants.CLIENT_ID).append(DigiConstants.REQUEST_PARAM_ATTR_VALUE).append(docuAuthIntegKey).append(DigiConstants.REQUEST_PARAM_ATTR_SEPARATOR)
                .append(DigiConstants.STATE).append(DigiConstants.REQUEST_PARAM_ATTR_VALUE).append(stateString).append(DigiConstants.REQUEST_PARAM_ATTR_SEPARATOR)
                .append(DigiConstants.REDIRECT_URI).append(DigiConstants.REQUEST_PARAM_ATTR_VALUE).append(docuSignRedirectUrl).append(DigiConstants.REQUEST_PARAM_ATTR_SEPARATOR)
                .append(DigiConstants.login_hint).append(DigiConstants.REQUEST_PARAM_ATTR_VALUE).append(docuSignLoginHint);

        log.debug("The auth url for docusign "+authUrl.toString());
        return authUrl.toString();
    }

    /**
     * Method to retrieve the access token based on the auth code
     * @param authCode
     * @return
     */
    @Override
    public DocuSignTokenDto getAccessToken(String authCode){
        Process process = null;
        try {
            String combinedStr = docuAuthIntegKey + ":" + docuSecretKey;
            String encodedStr = new String(Base64.encode(combinedStr.getBytes()));
            StringBuffer curlCommand = new StringBuffer();
            curlCommand.append("curl --header  \"Authorization: Basic " + encodedStr + "\" --data \"grant_type=authorization_code&code=" + authCode + "\" --request POST " + docuSignTokenUrl);
            log.debug("The curl command for access token "+curlCommand.toString());

            process = Runtime.getRuntime().exec(curlCommand.toString());

            ObjectMapper objMapper = new ObjectMapper();
            DocuSignTokenDto docuSignTokenDto = objMapper.readValue(process.getInputStream(), DocuSignTokenDto.class);
            return docuSignTokenDto;
        }catch (IOException ioe){
            log.error("Exception while running the curl command while retrieving the access token: "+ioe);
        }finally{
            process.destroy();
        }
        return null;
    }

    /**
     * Method to retrieve the user info (account id and base uri) based on the access token
     * @param accessToken
     * @return
     */
    @Override
    public DocuSignUserInfoDto getUserInfo(String accessToken) {
        Process process = null;
        try{
            StringBuffer curlCommand = new StringBuffer();
            curlCommand.append("curl  --request GET " + docuSignUserUrl + " --header  \"Authorization: Bearer " + accessToken +"\"");
            log.debug("The curl command for user info "+curlCommand.toString());
            process = Runtime.getRuntime().exec(curlCommand.toString());

            ObjectMapper objMapper = new ObjectMapper();
            JsonNode userInfoJson = objMapper.readValue(process.getInputStream(), JsonNode.class);
            DocuSignUserInfoDto[] docuSignUserInfoDto = objMapper.treeToValue(userInfoJson.get(DigiConstants.JSON_USER_INFO),
                    DocuSignUserInfoDto[].class) ;
            return  docuSignUserInfoDto[0];
        }catch (IOException ioe){
            log.error("Exception while running the curl command while retrieving the account id and base uri: "+ioe);
        }
        return null;
    }

    /**
     * Method that prepares the email signature
     * @param emailText
     * @param docRef
     * @param digiUser
     */
    @Override
    public Object prepareDocumentSignature(String emailText, String docRef, DigiUser digiUser,
                                           String accessToken,DocuSignUserInfoDto docuSignUserInfoDto,
                                           String redirectState) {
        EnvelopeDefinition envelopeDefinition = prepareEnvelope(emailText.getBytes(),
                docRef,digiUser);
        return callDocuSignForEnvelope(accessToken,envelopeDefinition,digiUser,docuSignUserInfoDto.getBase_uri(),
                docuSignUserInfoDto.getAccount_id(),redirectState,docRef);
    }



    /**
     *
     * @param textFile - email content
     * @param docRef - the reference to the email (upload id can be used)
     * @param digiUser - the person to whom the email is to be sent
     *
     * @return  EnvelopeDefinition
     */
    private EnvelopeDefinition prepareEnvelope(byte[] textFile, String docRef,DigiUser digiUser){
        String docBase64 = new String(Base64.encode(textFile));

        // Create the DocuSign document object
        Document document = new Document();
        document.setDocumentBase64(docBase64);
        document.setName("Letter For CCC"); // can be different from actual file name
        document.setFileExtension("txt"); // many different document types are accepted
        document.setDocumentId(docRef); // a label used to reference the doc


        // The signer object
        // Create a signer recipient to sign the document, identified by name and email
        // We set the clientUserId to enable embedded signing for the recipient
        Signer signer = new Signer();
        signer.setEmail(digiUser.getEmail());
        signer.setName(digiUser.getFirstName()+" "+digiUser.getLastName());
        signer.setClientUserId(Long.toString(digiUser.getId()));
        signer.setRecipientId("1");

        // Create a signHere tabs (also known as a field) on the document,
        // We're using x/y positioning. Anchor string positioning can also be used
        SignHere signHere = new SignHere();
        signHere.setDocumentId(docRef);
        signHere.setPageNumber("1");
        signHere.setRecipientId("1");
        signHere.setTabLabel("SignHereTab");
        signHere.setXPosition("195");
        signHere.setYPosition("147");

       /* String attachment = new String(Base64.encode(digiUploads.getUploadedFile()));
        Document signerDocument = new Document();
        document.setDocumentBase64(attachment);
        document.setName(fileName); // can be different from actual file name
        document.setFileExtension(FilenameUtils.getExtension(fileName)); // many different document types are accepted
        int random = new Random().nextInt(1000);
        document.setDocumentId(Integer.toString(random)); // a label used to reference the doc

        SignerAttachment signerAttachment = new SignerAttachment();
        signerAttachment.setDocumentId(Integer.toString(random));
        signerAttachment.setPageNumber("1");
        signerAttachment.setRecipientId("1");
        signerAttachment.setTabLabel("AttachFile");
        signerAttachment.setXPosition("10");
        signerAttachment.setYPosition("20");*/

        // Add the tabs to the signer object
        // The Tabs object wants arrays of the different field/tab types
        Tabs signerTabs = new Tabs();
        signerTabs.setSignHereTabs(Arrays.asList(signHere));
        //signerTabs.setSignerAttachmentTabs(Arrays.asList(signerAttachment));
        signer.setTabs(signerTabs);

        // Next, create the top level envelope definition and populate it.
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        //envelopeDefinition.setDocuments(Arrays.asList(document,signerDocument));
        envelopeDefinition.setDocuments(Arrays.asList(document));
        // Add the recipient to the envelope object
        Recipients recipients = new Recipients();
        recipients.setSigners(Arrays.asList(signer));
        envelopeDefinition.setRecipients(recipients);
        envelopeDefinition.setStatus("sent"); // requests that the envelope be created and sent.

        return  envelopeDefinition;
    }

    /**
     *
     * @param accessToken
     * @param envelopeDefinition
     * @param digiUser
     * @param docuSignBasePath
     * @param docuSignAccountId
     * @param redirectState - String to which the redirect has to happen after signing
     * @param docRef - uploadedId
     * @return
     */
    private Object callDocuSignForEnvelope(String accessToken,EnvelopeDefinition envelopeDefinition,
                                         DigiUser digiUser,String docuSignBasePath,String docuSignAccountId,
                                           String redirectState,String docRef){
        try {
            ApiClient apiClient = new ApiClient(docuSignBasePath+"/restapi");
            apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
            apiClient.addAuthorization("docusignAccessCode", null);
            EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
            EnvelopeSummary results = envelopesApi.createEnvelope(docuSignAccountId, envelopeDefinition);
            String envelopeId = results.getEnvelopeId();

            RecipientViewRequest viewRequest = new RecipientViewRequest();
            // Set the url where you want the recipient to go once they are done signing
            // should typically be a callback route somewhere in your app.
            viewRequest.setReturnUrl(docuSignRedirectBaseUrl+redirectState);
            viewRequest.setAuthenticationMethod("none");
            viewRequest.setEmail(digiUser.getEmail());
            viewRequest.setUserName(digiUser.getFirstName()+" "+digiUser.getLastName());
            viewRequest.setClientUserId(Long.toString(digiUser.getId()));

            viewRequest.setPingFrequency("600"); // seconds
            viewRequest.setPingUrl(docuSignRedirectUrl);
            // call the CreateRecipientView API
            ViewUrl results1 = envelopesApi.createRecipientView(docuSignAccountId, envelopeId, viewRequest);

            // Step 4. The Recipient View URL (the Signing Ceremony URL) has been received.
            //         The user's browser will be redirected to it.
            String redirectUrl = results1.getUrl();
            RedirectView redirect = new RedirectView(redirectUrl);
            redirect.setExposeModelAttributes(false);
            log.debug("The redirect url "+redirectUrl);
            return redirect;
        }catch (ApiException apiException){
            log.error("ApiException while creating envelope for Docusign: "+apiException);
            apiException.printStackTrace();
        }
        return null;
    }
}
